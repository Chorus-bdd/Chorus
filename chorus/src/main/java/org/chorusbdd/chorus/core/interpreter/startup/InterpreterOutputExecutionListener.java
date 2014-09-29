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
package org.chorusbdd.chorus.core.interpreter.startup;

import org.chorusbdd.chorus.executionlistener.ExecutionListener;
import org.chorusbdd.chorus.output.OutputFormatter;
import org.chorusbdd.chorus.results.ExecutionToken;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.results.ScenarioToken;
import org.chorusbdd.chorus.results.StepToken;

import java.util.List;

/**
 * Created by: Steve Neal
 * Date: 11/01/12
 *
 * This execution listener is responsible for generating the console standard output for Chorus
 * 
 * It delegates to a OutputFormatter for the actual output
 */
public class InterpreterOutputExecutionListener implements ExecutionListener {

    private OutputFormatter chorusOutFormatter;

    private boolean showSummary = true;
    private boolean verbose = false;

    private int stepMacroDepth = 0;

    public InterpreterOutputExecutionListener(boolean showSummary, boolean verbose, OutputFormatter chorusOutFormatter) {
        this.showSummary = showSummary;
        this.verbose = verbose;
        this.chorusOutFormatter = chorusOutFormatter;
    }

    public void setFormatter(OutputFormatter formatter) {
        this.chorusOutFormatter = formatter;
    }

    public void testsStarted(ExecutionToken testExecutionToken) {
    }

    public void featureStarted(ExecutionToken testExecutionToken, FeatureToken feature) {
        chorusOutFormatter.printFeature(feature);
    }

    public void featureCompleted(ExecutionToken testExecutionToken, FeatureToken feature) {
        if (! feature.foundAllHandlers()) {
            chorusOutFormatter.printMessage(feature.getUnavailableHandlersMessage());
        }
        chorusOutFormatter.printMessage(""); //just a blank line between features
    }

    public void scenarioStarted(ExecutionToken testExecutionToken, ScenarioToken scenario) {
        chorusOutFormatter.printScenario(scenario);
    }

    public void scenarioCompleted(ExecutionToken testExecutionToken, ScenarioToken scenario) {
    }

    public void stepStarted(ExecutionToken testExecutionToken, StepToken step) {
        stepMacroDepth ++;  //are we processing a top level scenario step (depth == 1) or a step macro step ( depth > 1 )
        chorusOutFormatter.printStepStart(step, stepMacroDepth);
    }

    public void stepCompleted(ExecutionToken testExecutionToken, StepToken step) {
        printStepEnd(step, stepMacroDepth);
        stepMacroDepth --;
    }

    private void printStepEnd(StepToken step, int depth) {
        chorusOutFormatter.printStepEnd(step, depth);
        if (step.getException() != null && verbose) {
            chorusOutFormatter.printStackTrace(step.getStackTrace());
        }
    }

    public void testsCompleted(ExecutionToken testExecutionToken, List<FeatureToken> features) {
        if (showSummary) {
            chorusOutFormatter.printResults(testExecutionToken.getResultsSummary());
        }
    }
}
