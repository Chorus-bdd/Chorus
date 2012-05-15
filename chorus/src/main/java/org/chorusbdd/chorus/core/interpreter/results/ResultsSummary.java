/* Copyright (C) 2000-2011 The Software Conservancy as Trustee.
 * All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 *
 * Nothing in this notice shall be deemed to grant any rights to trademarks,
 * copyrights, patents, trade secrets or any other intellectual property of the
 * licensor or any contributor except as expressly stated herein. No patent
 * license is granted separate from the Software, for code that you delete from
 * the Software, or for combinations of the Software with other software or
 * hardware.
 */

package org.chorusbdd.chorus.core.interpreter.results;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple class that compiles a summary of a test results
 * from a list of executed Features
 *
 * This is now the result of invoking the interpreter, and a wrapper around the list of
 * features, and test execution token
 * Nick May 2012
 * 
 * Created by: Steve Neal
 * Date: 16/11/11
 */
public class ResultsSummary implements ResultToken {
    
    //stats
    private int scenariosPassed = 0;
    private int scenariosFailed = 0;
    private int unavailableHandlers = 0;

    private int stepsPassed = 0;
    private int stepsFailed = 0;
    private int stepsPending = 0;
    private int stepsUndefined = 0;
    private int stepsSkipped = 0;

    private TestExecutionToken testExecutionToken;
    private List<FeatureToken> results;

    private ResultsSummary() {
    }

    public ResultsSummary(TestExecutionToken testExecutionToken, List<FeatureToken> results) {
        this.testExecutionToken = testExecutionToken;
        this.results = results;

        for (FeatureToken feature : results) {
            if (feature.getUnavailableHandlersMessage() == null) {
                processHandledScenarios(feature);
            } else {
                unavailableHandlers++;
            }
        }
    }

    private void processHandledScenarios(FeatureToken feature) {
        for (ScenarioToken scenario : feature.getScenarios()) {
            boolean scenarioPassed = true;
            for (StepToken step : scenario.getSteps()) {
                switch (step.getEndState()) {
                    case PASSED:
                        stepsPassed++;
                        break;
                    case FAILED:
                        stepsFailed++;
                        scenarioPassed = false;
                        break;
                    case PENDING:
                        stepsPending++;
                        break;
                    case SKIPPED:
                        stepsSkipped++;
                        break;
                    case UNDEFINED:
                        stepsUndefined++;
                        scenarioPassed = false;
                        break;
                }
            }
            if (scenarioPassed) {
                scenariosPassed++;
            } else {
                scenariosFailed++;
            }
        }
    }


    //
    // - getters
    //


    public int getScenariosPassed() {
        return scenariosPassed;
    }

    public int getScenariosFailed() {
        return scenariosFailed;
    }

    public int getUnavailableHandlers() {
        return unavailableHandlers;
    }

    public int getStepsPassed() {
        return stepsPassed;
    }

    public int getStepsFailed() {
        return stepsFailed;
    }

    public int getStepsPending() {
        return stepsPending;
    }

    public int getStepsUndefined() {
        return stepsUndefined;
    }

    public int getStepsSkipped() {
        return stepsSkipped;
    }

    public List<FeatureToken> getFeatures() {
        return results;
    }

    public TestExecutionToken getTestExecutionToken() {
        return testExecutionToken;
    }

    public Object deepCopy() {
        ResultsSummary s = new ResultsSummary();
        s.scenariosPassed = scenariosPassed;
        s.scenariosFailed = scenariosFailed;
        s.unavailableHandlers = unavailableHandlers;

        s.stepsFailed = stepsFailed;
        s.stepsPassed = stepsPassed;
        s.stepsPending = stepsPending;
        s.stepsUndefined = stepsUndefined;
        s.stepsSkipped = stepsSkipped;

        s.results = new ArrayList<FeatureToken>();
        for (FeatureToken f : results ) {
            s.results.add(f.deepCopy());
        }

        s.testExecutionToken = testExecutionToken.deepCopy();
        return s;
    }
}
