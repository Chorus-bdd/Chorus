/**
 *  Copyright (C) 2000-2012 The Software Conservancy as Trustee.
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

import org.chorusbdd.chorus.core.interpreter.results.FeatureToken;
import org.chorusbdd.chorus.core.interpreter.results.ResultsSummary;
import org.chorusbdd.chorus.core.interpreter.results.ScenarioToken;
import org.chorusbdd.chorus.core.interpreter.results.StepToken;

import java.io.PrintWriter;
import java.io.Writer;

/**
 * Created by: Steve Neal
 * Date: 30/09/11
 */
public class PlainResultsFormatter implements ResultsFormatter {

    private final PrintWriter out;

    public PlainResultsFormatter(Writer out) {
        this.out = new PrintWriter(out, true);
    }

    public void printResults(ResultsSummary summary) {
        if (summary != null) {
            printMessage(String.format("%nFeatures  (total:%d) (passed:%d) (failed:%d)",
                    summary.getFeaturesPassed() + summary.getFeaturesFailed(),
                    summary.getFeaturesPassed(),
                    summary.getFeaturesFailed()));

            //print scenarios summary
            printMessage(String.format("Scenarios (total:%d) (passed:%d) (failed:%d)",
                    summary.getScenariosPassed() + summary.getScenariosFailed(),
                    summary.getScenariosPassed(),
                    summary.getScenariosFailed()));

            //print steps summary
            printMessage(String.format("Steps     (total:%d) (passed:%d) (failed:%d) (undefined:%d) (pending:%d) (skipped:%d)",
                    summary.getStepsPassed() + summary.getStepsFailed() + summary.getStepsUndefined() + summary.getStepsPending() + summary.getStepsSkipped(),
                    summary.getStepsPassed(),
                    summary.getStepsFailed(),
                    summary.getStepsUndefined(),
                    summary.getStepsPending(),
                    summary.getStepsSkipped()));
        }
    }

    public void printFeature(FeatureToken feature) {
        out.printf("Feature: %-84s%-7s %s%n", feature.getNameWithConfiguration(), "", "");
    }

    public void printFeature(FeatureToken feature, String status, String message) {
        out.printf("Feature: %-84s%-7s %s%n", feature.getNameWithConfiguration(), status, message);
    }

    public void printScenario(ScenarioToken scenario) {
        out.printf("  Scenario: %s%n", scenario.getName());
    }

    public void printStep(StepToken step) {
        String message = (step.getThrowable() == null) ? step.getMessage() : step.getThrowable().getMessage();
        out.printf("    %-89s%-7s %s%n", step.toString(), step.getEndState(), message);
    }

    public void printStackTrace(Throwable t) {
        t.printStackTrace(out);
    }

    public void printMessage(String message) {
        out.printf("%s%n", message);
    }

    public void close() {
        out.flush();
        out.close();
    }
}
