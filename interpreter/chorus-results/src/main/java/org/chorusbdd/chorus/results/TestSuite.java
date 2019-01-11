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

import java.util.List;

/**
 * User: nick
 * Date: 29/12/12
 * Time: 23:06
 *
 * TestSuite is a wrapper around an ExecutionToken and a list of features executed as a test suite / single interpreter run
 *
 * TestSuite is convenience class which wraps together the ExecutionToken representing a run of the interpreter and collated results
 * with the list of features actually run (the features each contain the nested state of each scenario and step)
 *
 * It does not in itself provide any extra information which cannot be obtained from the ExecutionToken or Feature List.
 * Since it contains no extra state, it is not itself a Token instance, and it is not serialized and sent to local or
 * remote ExecutionListener. Instead such listeners may themselves create a TestSuite instance of they so desire.
 * This is easy since once a test suite is completed the interpreter calls the testsCompleted() method on any listeners
 * registered, and supplies an ExecutionToken and List&lt;Feature&gt; as parameters.
 *
 * TestSuite also provides an accept() method for a TokenVisitor, and this can be called to have the visitor visit
 * the ExecutionToken, ResultsSummary, and the Features, Scenarios and Steps in a predictable order.
 */
public class TestSuite {

    private final ExecutionToken executionToken;
    private final List<FeatureToken> featureList;

    public TestSuite(ExecutionToken executionToken, List<FeatureToken> featureList) {
        this.executionToken = executionToken;
        this.featureList = featureList;
    }

    public String getTestSuiteName() {
        return executionToken.getTestSuiteName();
    }

    public long getExecutionStartTime() {
        return executionToken.getExecutionStartTime();
    }

    public int getFeaturesFailed() {
        return executionToken.getFeaturesFailed();
    }

    public int getFeaturesPassed() {
        return executionToken.getFeaturesPassed();
    }

    public int getFeaturesPending() {
        return executionToken.getFeaturesPending();
    }

    public ResultsSummary getResultsSummary() {
        return executionToken.getResultsSummary();
    }

    public int getScenariosFailed() {
        return executionToken.getScenariosFailed();
    }

    public int getScenariosPassed() {
        return executionToken.getScenariosPassed();
    }

    public int getScenariosPending() {
        return executionToken.getScenariosPending();
    }

    public int getStepsFailed() {
        return executionToken.getStepsFailed();
    }

    public int getStepsPassed() {
        return executionToken.getStepsPassed();
    }

    public int getStepsPending() {
        return executionToken.getStepsPending();
    }

    public int getStepsSkipped() {
        return executionToken.getStepsSkipped();
    }

    public int getStepsUndefined() {
        return executionToken.getStepsUndefined();
    }

    public int getUnavailableHandlers() {
        return executionToken.getUnavailableHandlers();
    }

    public boolean isFullyImplemented() {
        return executionToken.isFullyImplemented();
    }

    public EndState getEndState() {
        return executionToken.getEndState();
    }

    public long getTimeTaken() {
        return executionToken.getTimeTaken();
    }

    public int getTotalFeatures() {
        return executionToken.getTotalFeatures();
    }

    public int getTotalScenarios() {
        return executionToken.getTotalScenarios();
    }

    public List<FeatureToken> getFeatureList() {
        return featureList;
    }

    public ExecutionToken getExecutionToken() {
        return executionToken;
    }

    public String getExecutionHost() {
        return executionToken.getExecutionHost();
    }

    public void accept(TokenVisitor tokenVisitor) {
        executionToken.accept(tokenVisitor);
        for (FeatureToken f : featureList) {
            f.accept(tokenVisitor);
        }
    }

    public String toString() {
        return "Test Suite " + executionToken.toString();
    }
}
