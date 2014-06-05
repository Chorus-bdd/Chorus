/**
 *  Copyright (C) 2000-2013 The Software Conservancy and Original Authors.
 *  All rights reserved.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to
 *  deal in the Software without restriction, including without limitation the
 *  rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 *  sell copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 *  IN THE SOFTWARE.
 *
 *  Nothing in this notice shall be deemed to grant any rights to trademarks,
 *  copyrights, patents, trade secrets or any other intellectual property of the
 *  licensor or any contributor except as expressly stated herein. No patent
 *  license is granted separate from the Software, for code that you delete from
 *  the Software, or for combinations of the Software with other software or
 *  hardware.
 */
package org.chorusbdd.chorus.core.interpreter;

import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.executionlistener.ExecutionListenerSupport;
import org.chorusbdd.chorus.handlers.util.PolledAssertion;
import org.chorusbdd.chorus.results.ExecutionToken;
import org.chorusbdd.chorus.results.StepEndState;
import org.chorusbdd.chorus.results.StepToken;
import org.chorusbdd.chorus.util.ChorusRemotingException;
import org.chorusbdd.chorus.util.ExceptionHandling;
import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;

import java.io.InterruptedIOException;
import java.lang.reflect.InvocationTargetException;
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

    private static ChorusLog log = ChorusLogFactory.getLog(StepProcessor.class);

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
    public StepEndState runSteps(ExecutionToken executionToken, List<Object> handlerInstances, List<StepToken> stepList, boolean skip) {
        for (StepToken step : stepList) {
            StepEndState endState = processStep(executionToken, handlerInstances, step, skip);
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
     * @param handlerInstances the objects on which to execute the step (ordered by greatest precidence first)
     * @param step      details of the step to be executed
     * @param skip      is true the step will be skipped if found
     * @return the exit state of the executed step
     */
    private StepEndState processStep(ExecutionToken executionToken, List<Object> handlerInstances, StepToken step, boolean skip) {
        log.trace("Starting to process step " + (step.isStepMacro() ? "macro " : "") + step);
        executionListenerSupport.notifyStepStarted(executionToken, step);

        StepEndState endState;
        if ( step.isStepMacro() ) {
            endState = runSteps(executionToken, handlerInstances, step.getChildSteps(), skip);
        } else {
            endState = processHandlerStep(executionToken, handlerInstances, step, skip);
        }

        step.setEndState(endState);
        executionListenerSupport.notifyStepCompleted(executionToken, step);
        return endState;
    }

    private StepEndState processHandlerStep(ExecutionToken executionToken, List<Object> handlerInstances, StepToken step, boolean skip) {
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
            StepDefinitionMethodFinder stepDefinitionMethodFinder = new StepDefinitionMethodFinder(handlerInstances, step);
            stepDefinitionMethodFinder.findStepMethod();

            //call the method if found
            if (stepDefinitionMethodFinder.isMethodAvailable()) {
                endState = callStepMethod(executionToken, step, endState, stepDefinitionMethodFinder);
            } else {
                log.debug("Could not find a step method definition for step " + step);
                //no method found yet for this step
                endState = StepEndState.UNDEFINED;
                executionToken.incrementStepsUndefined();
            }
        }
        return endState;
    }

    private StepEndState callStepMethod(ExecutionToken executionToken, StepToken step, StepEndState endState, StepDefinitionMethodFinder stepDefinitionMethodFinder) {
        //setting a pending message in the step annotation implies the step is pending - we don't execute it
        String pendingMessage = stepDefinitionMethodFinder.getPendingMessage();
        if (!pendingMessage.equals(Step.NO_PENDING_MESSAGE)) {
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
                endState = executeStepMethod(executionToken, step, stepDefinitionMethodFinder);
            }
        }
        return endState;
    }

    private StepEndState executeStepMethod(ExecutionToken executionToken, StepToken step, StepDefinitionMethodFinder stepDefinitionMethodFinder) {
        StepEndState endState;
        log.debug("Now executing the step using method " + stepDefinitionMethodFinder.getMethodToCall());
        long startTime = System.currentTimeMillis();
        try {
            //call the step method using reflection
            Object result = stepDefinitionMethodFinder.getMethodToCall().invoke(
                    stepDefinitionMethodFinder.getHandlerInstance(),
                    stepDefinitionMethodFinder.getMethodCallArgs()
            );
            log.debug("Finished executing the step, step passed, result was " + result);
            if (result != null) {
                step.setMessage(result.toString());
            }
            endState = StepEndState.PASSED;
            executionToken.incrementStepsPassed();
        } catch (InvocationTargetException e) {
            log.debug("Step execution failed, we hit an exception while executing the step method");
            //here if the method called threw an exception
            Throwable cause = e.getCause();                      
            endState = handleRootCause(executionToken, step, cause);
        } catch (PolledAssertion.PolledAssertionError ae ) {  //when using a PolledInvoker this will re-throw AssertionErrors
            endState = handleRootCause(executionToken, step, ae.getCause());
        } catch (AssertionError ae) {
            endState = handleRootCause(executionToken, step, ae);
        } catch (Throwable t) {
            log.error("Step execution failed, we hit an exception trying to invoke the step method", t);
            endState = handleRootCause(executionToken, step, t);
        } finally {
            step.setTimeTaken(System.currentTimeMillis() - startTime);
        }
        return endState;
    }

    private StepEndState handleRootCause(ExecutionToken executionToken, StepToken step, Throwable cause) {
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

        String exceptionMessage = cause.getMessage();
        log.debug("Step failed due to exception " + exceptionMessage);

        String stepMessage = getStepMessage(cause, exceptionMessage);
        step.setMessage(stepMessage);

        endState = StepEndState.FAILED;
        executionToken.incrementStepsFailed();
        return endState;
    }

    private String getStepMessage(Throwable cause, String exceptionMessage) {
        String location = "";
        boolean isRemotingException = cause instanceof ChorusRemotingException;
        if ( ! isRemotingException) {
            //the remoting exception contains its own location in the message
            location = ExceptionHandling.getExceptionLocation(cause);
        }
        String exceptionSimpleName = cause.getClass().getSimpleName();
        String message;
        if( exceptionMessage == null ) {
            message = exceptionSimpleName;
        } else {
            message = exceptionMessage;
            //ensure we have the name of the exception included
            if ( ! message.contains(exceptionSimpleName) && ! isRemotingException) {
                message = exceptionSimpleName + " " + message;
            }
        }
        return location + message;
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
        step.setMessage(cause.getClass().getSimpleName());
        Thread.currentThread().isInterrupted(); //clear the interrupted status
        return endState;
    }

    private StepEndState handleStepPendingException(ExecutionToken executionToken, StepToken step, StepPendingException cause) {
        StepEndState endState;
        StepPendingException spe = (StepPendingException) cause;
        step.setThrowable(spe);
        step.setMessage(spe.getMessage());
        endState = StepEndState.PENDING;
        executionToken.incrementStepsPending();
        log.debug("Step Pending Exception prevented execution");
        return endState;
    }
}
