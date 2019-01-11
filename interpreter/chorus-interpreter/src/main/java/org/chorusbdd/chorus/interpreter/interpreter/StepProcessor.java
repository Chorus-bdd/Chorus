/**
 * MIT License
 *
 * Copyright (c) 2019 Chorus BDD Organisation.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.chorusbdd.chorus.interpreter.interpreter;

import org.chorusbdd.chorus.context.ChorusContext;
import org.chorusbdd.chorus.executionlistener.ExecutionListenerSupport;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.parser.StepMacro;
import org.chorusbdd.chorus.results.ExecutionToken;
import org.chorusbdd.chorus.results.StepEndState;
import org.chorusbdd.chorus.results.StepToken;
import org.chorusbdd.chorus.stepinvoker.*;
import org.chorusbdd.chorus.stepinvoker.catalogue.StepCatalogue;
import org.chorusbdd.chorus.util.ChorusException;
import org.chorusbdd.chorus.util.ExceptionHandling;
import org.chorusbdd.chorus.util.PolledAssertion;

import java.io.InterruptedIOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 24/02/13
 * Time: 11:20
 *
 * Process a scenario step by identifying and calling handler class methods
 * or by processing child steps for step macros.
 */
public class StepProcessor {

    private ChorusLog log = ChorusLogFactory.getLog(StepProcessor.class);

    private ExecutionListenerSupport executionListenerSupport;
    private boolean dryRun;
    private volatile boolean interruptingOnTimeout;
    private ContextVariableStepExpander contextVariableStepExpander = new ContextVariableStepExpander();


    public StepProcessor(ExecutionListenerSupport executionListenerSupport) {
        this.executionListenerSupport = executionListenerSupport;
    }

    public void setDryRun(boolean dryRun) {
        this.dryRun = dryRun;
    }

    public void setInterruptingOnTimeout(boolean interruptingOnTimeout) {
        this.interruptingOnTimeout = interruptingOnTimeout;
    }

    /**
     * Process all steps in stepList
     *
     * @param skip, do not actually execute (but mark as skipped if not unimplemented)
     *
     * @return a StepEndState is the StepMacro step's end state, if these steps are executed as part of a StepMacro rather than scenario
     */
    public StepEndState runSteps(ExecutionToken executionToken, StepInvokerProvider stepInvokerProvider, List<StepToken> stepList, StepCatalogue stepCatalogue, boolean skip) {

        for (StepToken step : stepList) {
            StepEndState endState = processStep(executionToken, stepInvokerProvider, step, stepCatalogue, skip);
            switch (endState) {
                case PASSED:
                    break;
                case FAILED:
                    skip = true;//skip (don't execute) the rest of the steps
                    break;
                case UNDEFINED:
                    skip = true;//skip (don't execute) the rest of the steps
                    break;
                case PENDING:
                    skip = true;//skip (don't execute) the rest of the steps
                    break;
                case TIMEOUT:
                    skip = true;//skip (don't execute) the rest of the steps
                    break;
                case SKIPPED:
                case DRYRUN:
                    break;
                default :
                    throw new RuntimeException("Unhandled step state " + endState);

            }
        }

        StepEndState stepMacroEndState = StepMacro.calculateStepMacroEndState(stepList);
        return stepMacroEndState;
    }


    /**
     * @param stepInvokerProvider container for StepInvoker to run the steps
     * @param step      details of the step to be executed
     * @param stepCatalogue
     *@param skip      is true the step will be skipped if found  @return the exit state of the executed step
     */
    private StepEndState processStep(ExecutionToken executionToken, StepInvokerProvider stepInvokerProvider, StepToken step, StepCatalogue stepCatalogue, boolean skip) {
        log.trace("Starting to process step " + (step.isStepMacro() ? "macro " : "") + step);
        executionListenerSupport.notifyStepStarted(executionToken, step);

        StepEndState endState;
        if ( step.isStepMacro() ) {
            endState = runSteps(executionToken, stepInvokerProvider, step.getChildSteps(), stepCatalogue, skip);
        } else {
            endState = processHandlerStep(executionToken, stepInvokerProvider, step, stepCatalogue, skip);
        }

        step.setEndState(endState);
        executionListenerSupport.notifyStepCompleted(executionToken, step);
        return endState;
    }

    private StepEndState processHandlerStep(ExecutionToken executionToken, StepInvokerProvider stepInvokerProvider, StepToken step, StepCatalogue stepCatalogue, boolean skip) {
        //return this at the end
        StepEndState endState = null;

        if (skip) {
            log.debug("Skipping step  " + step);
            //output skipped and don't call the method
            endState = StepEndState.SKIPPED;
            executionToken.incrementStepsSkipped();
        } else {
            log.debug("Processing step " + step);

            contextVariableStepExpander.processStep(step);

            //identify what method should be called and its parameters
            List<StepInvoker> stepInvokers = stepInvokerProvider.getStepInvokers();
            stepCatalogue.addSteps(stepInvokers);
            
            sortInvokersByPattern(stepInvokers);
            StepMatcher stepMatcher = new StepMatcher(stepInvokers, step.getAction());
            stepMatcher.findStepMethod();

            StepMatchResult stepMatchResult = stepMatcher.getStepMatchResult();
            switch(stepMatchResult) {
                case STEP_FOUND :
                    endState = callStepMethod(executionToken, step, endState, stepMatcher, stepCatalogue);
                    break;
                case STEP_NOT_FOUND:
                    log.debug("Could not find a step method definition for step " + step);
                    //no method found yet for this step
                    endState = StepEndState.UNDEFINED;
                    executionToken.incrementStepsUndefined();
                    break;
                case DUPLICATE_MATCH_ERROR:
                    endState = StepEndState.FAILED;
                    handleGenericException(executionToken, step, stepMatcher.getMatchException());
                    break;
                default:
                    //we should never get here but in case we do...
                    endState = StepEndState.FAILED;
                    ChorusException cause = new ChorusException("Unsupported StepMatchResult");
                    cause.fillInStackTrace();
                    handleGenericException(executionToken, step, cause);
                    break;
            }
        }
        return endState;
    }

    //without doing this, the order of matching is indeterminate, which can cause differences in output which are a problem for testing
    //prefer fully determinate behaviour. The only sensible solution is to sort by pattern
    private void sortInvokersByPattern(List<StepInvoker> stepInvokers) {
        Collections.sort(stepInvokers, new Comparator<StepInvoker>() {
            public int compare(StepInvoker o1, StepInvoker o2) {
                return o1.getStepPattern().toString().compareTo(o2.getStepPattern().toString());
            }
        });
    }

    private StepEndState callStepMethod(ExecutionToken executionToken, StepToken step, StepEndState endState, StepMatcher stepMatcher, StepCatalogue stepCatalogue) {
        //setting a pending message in the step annotation implies the step is pending - we don't execute it
        StepInvoker chosenStepInvoker = stepMatcher.getFoundStepInvoker();
        if ( chosenStepInvoker.isPending()) {
            String pendingMessage = chosenStepInvoker.getPendingMessage();
            log.debug("Step has a pending message " + pendingMessage + " skipping step");
            step.setMessage(pendingMessage);
            endState = StepEndState.PENDING;
            executionToken.incrementStepsPending();
        } else {
            if (dryRun) {
                log.debug("Dry Run, so not executing this step");
                step.setMessage("This step is OK");
                endState = StepEndState.DRYRUN;
                executionToken.incrementStepsPassed(); // treat dry run as passed? This state was unsupported in previous results
            } else {
                endState = executeStepMethod(executionToken, step, stepMatcher, stepCatalogue);
            }
        }
        return endState;
    }

    private StepEndState executeStepMethod(ExecutionToken executionToken, StepToken step, StepMatcher stepMatcher, StepCatalogue stepCatalogue) {
        StepEndState endState = StepEndState.UNDEFINED;
        log.debug("Now executing the step using method " + stepMatcher);
        long startTime = System.currentTimeMillis();
        StepInvoker foundStepInvoker = null;
        try {
            //call the step method using reflection
            foundStepInvoker = stepMatcher.getFoundStepInvoker();

            Object result = invokeAndGetResult(step, stepMatcher, foundStepInvoker);
            log.debug("Finished executing the step, step passed, result was " + result);

            //n.b. for remote components using old versions of chorus to export steps we will not receive VOID_RESULT
            //so we will have to live with a slightly different/degraded behaviour, in particular storing a null lastResult
            //into the context for remote step methods which actually had a void return type.
            if ( ! StepInvoker.VOID_RESULT.equals(result) ) {
                //void results don't get put into the context, but null results do
                ChorusContext.getContext().put(ChorusContext.LAST_RESULT, result);
                if ( result != null) {
                    step.setMessage(result.toString());
                }
                log.debug("Stored lastResult into ChorusContext with the value " + step.getMessage());
            }

            endState = StepEndState.PASSED;
            executionToken.incrementStepsPassed();
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();                      
            endState = handleRootCause(executionToken, step, cause);
        } catch (PolledAssertion.PolledAssertionError ae ) {  //when using a PolledInvoker this will re-throw AssertionErrors
            Throwable cause = ae.getCause();
            endState = handleRootCause(executionToken, step, cause);
        } catch (Throwable t) {
            endState = handleRootCause(executionToken, step, t);
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;
            step.setTimeTaken(executionTime);
            if ( foundStepInvoker != null) {
                stepCatalogue.addExecutedStep(foundStepInvoker, executionTime, StepEndState.PASSED == endState);
            }
        }
        return endState;
    }

    private Object invokeAndGetResult(StepToken step, StepMatcher stepMatcher, StepInvoker foundStepInvoker) throws Exception {
        StepRetryDecorator retryInvoker = new StepRetryDecorator(foundStepInvoker);
        ResultWithRetryCount resultWithRetryCount = retryInvoker.invoke(
            step.getTokenId(),
            stepMatcher.getInvokerArgs()
        );
        step.setRetryAttempts(resultWithRetryCount.getRetryAttempts());
        return resultWithRetryCount.getResult();
    }

    private StepEndState handleRootCause(ExecutionToken executionToken, StepToken step, Throwable cause) {
        log.debug("Step execution failed, we hit an exception " + cause.getClass().getSimpleName() + " while executing the step method", cause);
        //here if the method called threw an exception

        StepEndState endState;
        if (cause instanceof StepPendingException) {
            endState = handleStepPendingException(executionToken, step, (StepPendingException) cause);
        } else if (cause instanceof InterruptedException || cause instanceof InterruptedIOException) {
            endState = handleInterruptedException(executionToken, step, cause);
        } else if (cause instanceof ThreadDeath ) {
            endState = handleThreadDeath();
        } else {
            endState = handleGenericException(executionToken, step, cause);
        }
        return endState;
    }

    private StepEndState handleGenericException(ExecutionToken executionToken, StepToken step, Throwable cause) {
        StepEndState endState;
        step.setThrowable(cause);

        String causeMessage = cause.getMessage();
        causeMessage = causeMessage == null ? "" : causeMessage;
        log.debug("Step failed due to exception " + cause);

        String errorDetails = getErrorDetails(cause);
        step.setMessage(causeMessage);
        step.setErrorDetails(errorDetails);

        endState = StepEndState.FAILED;
        executionToken.incrementStepsFailed();
        return endState;
    }

    private String getErrorDetails(Throwable cause) {
        String location = ExceptionHandling.getExceptionLocation(cause);
        String exceptionSimpleName = cause.getClass().getSimpleName();
        return location + exceptionSimpleName;
    }

    private StepEndState handleThreadDeath() {
        //thread has been stopped due to scenario timeout?
        log.error("ThreadDeath exception during step processing, tests will terminate");
        throw new ThreadDeath();  //we have to rethrow to actually kill the thread
    }

    private StepEndState handleInterruptedException(ExecutionToken executionToken, StepToken step, Throwable cause) {
        StepEndState endState;
        if ( interruptingOnTimeout ) {
            log.warn("Interrupted during step processing, will TIMEOUT remaining steps");
            interruptingOnTimeout = false;
            endState = StepEndState.TIMEOUT;
        } else {
            log.warn("Interrupted during step processing but this was not due to TIMEOUT, will fail step");
            endState = StepEndState.FAILED;
        }
        executionToken.incrementStepsFailed();
        step.setMessage("Interrupted");
        step.setErrorDetails(getErrorDetails(cause));
        Thread.currentThread().isInterrupted(); //clear the interrupted status
        return endState;
    }

    private StepEndState handleStepPendingException(ExecutionToken executionToken, StepToken step, StepPendingException cause) {
        StepEndState endState;
        step.setThrowable(cause);
        step.setMessage(cause.getMessage());
        endState = StepEndState.PENDING;
        executionToken.incrementStepsPending();
        log.debug("Step Pending Exception prevented execution");
        return endState;
    }
}
