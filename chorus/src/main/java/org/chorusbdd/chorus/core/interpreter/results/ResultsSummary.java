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
public class ResultsSummary extends AbstractToken implements ResultToken {

    private static final long serialVersionUID = 2;

    //stats
    private int scenariosPassed = 0;
    private int scenariosFailed = 0;
    private int unavailableHandlers = 0;

    private int stepsPassed = 0;
    private int stepsFailed = 0;
    private int stepsPending = 0;
    private int stepsUndefined = 0;
    private int stepsSkipped = 0;

    public ResultsSummary() {
        super(getNextId());
    }

    private ResultsSummary(long tokenId) {
        super(tokenId);
    }

    public int getScenariosPassed() {
        return scenariosPassed;
    }

    public void incrementScenariosPassed() {
        scenariosPassed++;
    }

    public int getScenariosFailed() {
        return scenariosFailed;
    }

    public void incrementScenariosFailed() {
        scenariosFailed++;
    }

    public int getUnavailableHandlers() {
        return unavailableHandlers;
    }

    public void incrementUnavailableHandlers() {
        unavailableHandlers++;
    }

    public int getStepsPassed() {
        return stepsPassed;
    }

    public void incrementStepsPassed() {
        stepsPassed++;
    }

    public int getStepsFailed() {
        return stepsFailed;
    }

    public void incrementStepsFailed() {
        stepsFailed++;
    }

    public int getStepsPending() {
        return stepsPending;
    }

    public void incrementStepsPending() {
        stepsPending++;
    }

    public int getStepsUndefined() {
        return stepsUndefined;
    }

    public void incrementStepsUndefined() {
        stepsUndefined++;
    }

    public int getStepsSkipped() {
        return stepsSkipped;
    }

    public void incrementStepsSkipped() {
        stepsSkipped++;
    }

    /**
     * @return this is useful, for cases where we need to show passed, failed, and anything else in between
     */
    public int getUndefinedPendingOrSkipped() {
        return stepsUndefined + stepsPending + stepsSkipped;
    }

    /**
     * @return true, if all features were fully implemented
     */
    public boolean isFullyImplemented() {
        return unavailableHandlers + stepsUndefined + stepsPending == 0;
    }

    public boolean isPassed() {
        return stepsFailed == 0;
    }

    public ResultsSummary deepCopy() {
        ResultsSummary s = new ResultsSummary(getTokenId());
        s.scenariosPassed = scenariosPassed;
        s.scenariosFailed = scenariosFailed;
        s.unavailableHandlers = unavailableHandlers;

        s.stepsFailed = stepsFailed;
        s.stepsPassed = stepsPassed;
        s.stepsPending = stepsPending;
        s.stepsUndefined = stepsUndefined;
        s.stepsSkipped = stepsSkipped;
        return s;
    }

}
