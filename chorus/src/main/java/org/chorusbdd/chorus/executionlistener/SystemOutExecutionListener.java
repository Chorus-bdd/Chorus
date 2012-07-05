/**
 *  Copyright (C) 2000-2012 The Software Conservancy and Original Authors.
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
package org.chorusbdd.chorus.executionlistener;

import org.chorusbdd.chorus.core.interpreter.ExecutionListener;
import org.chorusbdd.chorus.core.interpreter.results.ExecutionToken;
import org.chorusbdd.chorus.core.interpreter.results.FeatureToken;
import org.chorusbdd.chorus.core.interpreter.results.ScenarioToken;
import org.chorusbdd.chorus.core.interpreter.results.StepToken;
import org.chorusbdd.chorus.util.ChorusOut;

import java.io.PrintWriter;
import java.util.List;

/**
 * Created by: Steve Neal
 * Date: 11/01/12
 */
public class SystemOutExecutionListener implements ExecutionListener {

    private ResultsFormatter formatter;

    private boolean showSummary = true;
    private boolean verbose = false;

    public SystemOutExecutionListener(boolean showSummary, boolean verbose) {
        this.formatter = new PlainResultsFormatter(new PrintWriter(ChorusOut.out, true));
        this.showSummary = showSummary;
        this.verbose = verbose;
    }

    public void setFormatter(ResultsFormatter formatter) {
        this.formatter = formatter;
    }

    public void testsStarted(ExecutionToken testExecutionToken) {
    }

    public void featureStarted(ExecutionToken testExecutionToken, FeatureToken feature) {
        formatter.printFeature(feature);
    }

    public void featureCompleted(ExecutionToken testExecutionToken, FeatureToken feature) {
        if (! feature.foundAllHandlers()) {
            formatter.printMessage(feature.getUnavailableHandlersMessage());
        }
        formatter.printMessage(""); //just a blank line between features
    }

    public void scenarioStarted(ExecutionToken testExecutionToken, ScenarioToken scenario) {
        formatter.printScenario(scenario);
    }

    public void scenarioCompleted(ExecutionToken testExecutionToken, ScenarioToken scenario) {
    }

    public void stepStarted(ExecutionToken testExecutionToken, StepToken step) {
    }

    public void stepCompleted(ExecutionToken testExecutionToken, StepToken step) {
        formatter.printStep(step);
        if (step.getThrowable() != null && verbose) {
            formatter.printStackTrace(step.getThrowable());
        }
    }

    public void testsCompleted(ExecutionToken testExecutionToken, List<FeatureToken> features) {
        if (showSummary) {
            formatter.printResults(testExecutionToken.getResultsSummary());
        }
        formatter.close();
    }
}
