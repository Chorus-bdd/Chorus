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
package org.chorusbdd.chorus.results;

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
public class ResultsSummary extends AbstractToken implements PassPendingFailToken {

    private static final long serialVersionUID = 3;

    private int featuresPassed = 0;
    private int featuresPending = 0;
    private int featuresFailed = 0;

    //stats
    private int scenariosPassed = 0;
    private int scenariosPending = 0;
    private int scenariosFailed = 0;
    private int unavailableHandlers = 0;

    private int stepsPassed = 0;
    private int stepsFailed = 0;
    private int stepsPending = 0;
    private int stepsUndefined = 0;
    private int stepsSkipped = 0;

    private long timeTaken = 0; //time taken to run the test suite in millis
    
    public int getScenariosPassed() {
        return scenariosPassed;
    }

    public void setScenariosPassed(int scenariosPassed) {
        this.scenariosPassed = scenariosPassed;
    }

    public void incrementScenariosPassed() {
        scenariosPassed++;
    }

    public int getScenariosFailed() {
        return scenariosFailed;
    }

    public void setScenariosFailed(int scenariosFailed) {
        this.scenariosFailed = scenariosFailed;
    }

    public void incrementScenariosFailed() {
        scenariosFailed++;
    }

    public void incrementScenariosPending() {
        scenariosPending++;
    }

    public int getScenariosPending() {
        return scenariosPending;
    }

    public void setScenariosPending(int scenariosPending) {
        this.scenariosPending = scenariosPending;
    }

    public int getTotalScenarios() {
        return scenariosPassed + scenariosPending + scenariosFailed;
    }

    public int getFeaturesPassed() {
       return featuresPassed;
    }

    public void setFeaturesPassed(int featuresPassed) {
        this.featuresPassed = featuresPassed;
    }

    public void incrementFeaturesPassed() {
       featuresPassed++;
    }

    public int getFeaturesFailed() {
       return featuresFailed;
    }

    public void setFeaturesFailed(int featuresFailed) {
        this.featuresFailed = featuresFailed;
    }

    public void incrementFeaturesFailed() {
       featuresFailed++;
    }

    public void incrementFeaturesPending() {
        featuresPending++;
    }

    public int getFeaturesPending() {
        return featuresPending;
    }

    public void setFeaturesPending(int featuresPending) {
        this.featuresPending = featuresPending;
    }

    public int getTotalFeatures() {
        return featuresPassed + featuresPending + featuresFailed;
    }

    public int getUnavailableHandlers() {
        return unavailableHandlers;
    }

    public void setUnavailableHandlers(int unavailableHandlers) {
        this.unavailableHandlers = unavailableHandlers;
    }

    public void incrementUnavailableHandlers() {
        unavailableHandlers++;
    }

    public int getStepsPassed() {
        return stepsPassed;
    }

    public void setStepsPassed(int stepsPassed) {
        this.stepsPassed = stepsPassed;
    }

    public void incrementStepsPassed() {
        stepsPassed++;
    }

    public int getStepsFailed() {
        return stepsFailed;
    }

    public void setStepsFailed(int stepsFailed) {
        this.stepsFailed = stepsFailed;
    }

    public void incrementStepsFailed() {
        stepsFailed++;
    }

    public int getStepsPending() {
        return stepsPending;
    }

    public void setStepsPending(int stepsPending) {
        this.stepsPending = stepsPending;
    }

    public void incrementStepsPending() {
        stepsPending++;
    }

    public int getStepsUndefined() {
        return stepsUndefined;
    }

    public void setStepsUndefined(int stepsUndefined) {
        this.stepsUndefined = stepsUndefined;
    }

    public void incrementStepsUndefined() {
        stepsUndefined++;
    }

    public int getStepsSkipped() {
        return stepsSkipped;
    }

    public void setStepsSkipped(int stepsSkipped) {
        this.stepsSkipped = stepsSkipped;
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

    public EndState getEndState() {
        return isPassed() ? EndState.PASSED :
                isPending() ? EndState.PENDING : EndState.FAILED;
    }

    private boolean isPassed() {
        return featuresFailed == 0 && featuresPending == 0;
    }

    private boolean isPending() {
        return featuresFailed == 0 && featuresPending > 0;
    }

    public long getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(long timeTaken) {
        this.timeTaken = timeTaken;
    }

    public void calculateTimeTaken(long executionStartTime) {
        timeTaken = System.currentTimeMillis() - executionStartTime;
    }

    public void accept(TokenVisitor tokenVisitor) {
        tokenVisitor.startVisit(this);
        tokenVisitor.endVisit(this);
    }

    public ResultsSummary deepCopy() {
        ResultsSummary s = new ResultsSummary();
        super.deepCopy(s);
        s.featuresFailed = featuresFailed;
        s.featuresPending = featuresPending;
        s.featuresPassed = featuresPassed;

        s.scenariosPassed = scenariosPassed;
        s.scenariosPending = scenariosPending;
        s.scenariosFailed = scenariosFailed;
        s.unavailableHandlers = unavailableHandlers;

        s.stepsFailed = stepsFailed;
        s.stepsPassed = stepsPassed;
        s.stepsPending = stepsPending;
        s.stepsUndefined = stepsUndefined;
        s.stepsSkipped = stepsSkipped;

        s.timeTaken = timeTaken;
        return s;
    }

}
