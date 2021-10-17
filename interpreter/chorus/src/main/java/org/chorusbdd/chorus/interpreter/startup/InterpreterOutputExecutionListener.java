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
package org.chorusbdd.chorus.interpreter.startup;

import org.chorusbdd.chorus.annotations.ExecutionPriority;
import org.chorusbdd.chorus.executionlistener.ExecutionListener;
import org.chorusbdd.chorus.logging.LogLevel;
import org.chorusbdd.chorus.output.AbstractChorusOutputWriterDecorator;
import org.chorusbdd.chorus.output.ChorusOutputWriter;
import org.chorusbdd.chorus.results.*;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by: Steve Neal
 * Date: 11/01/12
 *
 * This execution listener is responsible for generating the console standard output for Chorus
 * 
 * It delegates to a wrapped OutputWriter for the actual output
 * It also captures any Chorus log output into the current
 */
@ExecutionPriority(ExecutionPriority.INTERPRETER_OUTPUT_PRIORITY)
public class InterpreterOutputExecutionListener extends AbstractChorusOutputWriterDecorator implements ExecutionListener {

    private boolean showSummary = true;
    private boolean verbose = false;

    private int stepMacroDepth = 0;

    private TokenLogCapture tokenLogCapture = new TokenLogCapture();

    public InterpreterOutputExecutionListener(boolean showSummary, boolean verbose, ChorusOutputWriter chorusOutFormatter) {
        super(chorusOutFormatter);
        this.showSummary = showSummary;
        this.verbose = verbose;
    }

    @Override
    public void testsStarted(ExecutionToken testExecutionToken, List<FeatureToken> features) {
        tokenLogCapture.setLastToken(testExecutionToken);
    }

    @Override
    public void featureStarted(ExecutionToken testExecutionToken, FeatureToken feature) {
        tokenLogCapture.setLastToken(feature);
        printFeature(feature);
    }

    @Override
    public void featureCompleted(ExecutionToken testExecutionToken, FeatureToken feature) {
        printMessage(""); //just a blank line between features
    }

    @Override
    public void scenarioStarted(ExecutionToken testExecutionToken, ScenarioToken scenario) {
        tokenLogCapture.setLastToken(scenario);
        printScenario(scenario);
    }

    @Override
    public void scenarioCompleted(ExecutionToken testExecutionToken, ScenarioToken scenario) {
    }

    @Override
    public void stepStarted(ExecutionToken testExecutionToken, StepToken step) {
        tokenLogCapture.setLastToken(step);
        stepMacroDepth ++;  //are we processing a top level scenario step (depth == 1) or a step macro step ( depth > 1 )
        printStepStart(step, stepMacroDepth);
    }

    @Override
    public void stepCompleted(ExecutionToken testExecutionToken, StepToken step) {
        processStepEnd(step, stepMacroDepth);
        stepMacroDepth --;
    }

    private void processStepEnd(StepToken step, int depth) {
        printStepEnd(step, depth);
        if (step.getException() != null && verbose) {
            printStackTrace(step.getStackTrace());
        }
    }

    @Override
    public void testsCompleted(ExecutionToken testExecutionToken, List<FeatureToken> features, Set<CataloguedStep> cataloguedSteps) {
        if (showSummary) {
            printResults(testExecutionToken.getResultsSummary(), features, cataloguedSteps);
        }
    }

    @Override
    public void log(LogLevel type, Object message) {
        tokenLogCapture.addInterpreterOutput(type, message);
        super.log(type, message);
    }

    @Override
    public void logError(LogLevel type, Throwable t) {
        tokenLogCapture.addInterpreterError(type, t);
        super.logError(type, t);
    }

    /**
     * Capture interpreter output into the current Token so this can be serialized and sent to remote agents
     * or stored along with the rest of the execution history
     */
    private class TokenLogCapture {

        private AbstractToken lastToken;

        //cache any log output which we receive before the call to 'testsStarted', otherwise we'd miss some key startup output
        private List<String> cachedLogOutput = new LinkedList<>();
        private Throwable cachedThrowable;

        void setLastToken(AbstractToken lastToken) {
            this.lastToken = lastToken;
        }

        void addInterpreterOutput(LogLevel type, Object message) {
            String logLine = String.format("%s --> %-7s - %s", "Chorus", type, message);
            if ( lastToken == null ) {
                cachedLogOutput.add(logLine);
            } else {
                flushCachedOutput();
                lastToken.addInterpreterOutput(logLine);
            }
        }

        void addInterpreterError(LogLevel type, Throwable t) {
            if ( lastToken == null ) {
                cachedThrowable = t;
            } else {
                flushCachedOutput();
                lastToken.setThrowable(t);
            }
        }

        //if we received any output before the 'testsStarted' callback then make sure it is captured
        //in the ExecutionToken
        private void flushCachedOutput() {
            Iterator<String> i = cachedLogOutput.iterator();
            while( i.hasNext()) {
                lastToken.addInterpreterOutput(i.next());
                i.remove();
            }

            if ( cachedThrowable != null) {
                lastToken.setThrowable(cachedThrowable);
                cachedThrowable = null;
            }
        }


    }

}
