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
package org.chorusbdd.chorus.tools.webagent;

import org.chorusbdd.chorus.results.*;
import org.chorusbdd.chorus.tools.xml.util.FormattingUtils;

import java.util.List;

/**
 * User: nick
 * Date: 30/12/12
 * Time: 00:11
 */
public class WebAgentTestSuite {

    private final String suiteTime;
    private TestSuite testSuite;

    public WebAgentTestSuite(TestSuite testSuite) {
        this.testSuite = testSuite;
        this.suiteTime = FormattingUtils.getStartTimeFormatter().format(
            testSuite.getExecutionToken().getExecutionStartTime()
        );
    }

    public String getEndStateString() {
        switch (getExecutionToken().getEndState()) {
            case PASSED: return "Passed";
            case PENDING: return "Pending";
            case FAILED: return "Failed";
            default:
                throw new UnsupportedOperationException("Unknown end state " + getExecutionToken().getEndState());
        }
    }
    public String getSuiteNameWithTime() {
        return getTestSuiteName() + " " + suiteTime;
    }

    public String getSuiteStartTime() {
        return suiteTime;
    }

    /**
     * @return Suite name with timestamp which together identify this TestSuite instance
     */
    public String getSuiteId() {
        return getTestSuiteName() + "-" + getExecutionStartTime();
    }

    public String toString() {
        return testSuite.toString();
    }

    public String getTestSuiteName() {
        return testSuite.getTestSuiteName();
    }

    public long getExecutionStartTime() {
        return testSuite.getExecutionStartTime();
    }

    public int getFeaturesFailed() {
        return testSuite.getFeaturesFailed();
    }

    public int getFeaturesPassed() {
        return testSuite.getFeaturesPassed();
    }

    public int getFeaturesPending() {
        return testSuite.getFeaturesPending();
    }

    public ResultsSummary getResultsSummary() {
        return testSuite.getResultsSummary();
    }

    public int getScenariosFailed() {
        return testSuite.getScenariosFailed();
    }

    public int getScenariosPassed() {
        return testSuite.getScenariosPassed();
    }

    public int getScenariosPending() {
        return testSuite.getScenariosPending();
    }

    public int getStepsFailed() {
        return testSuite.getStepsFailed();
    }

    public int getStepsPassed() {
        return testSuite.getStepsPassed();
    }

    public int getStepsPending() {
        return testSuite.getStepsPending();
    }

    public int getStepsSkipped() {
        return testSuite.getStepsSkipped();
    }

    public int getStepsUndefined() {
        return testSuite.getStepsUndefined();
    }

    public int getUnavailableHandlers() {
        return testSuite.getUnavailableHandlers();
    }

    public boolean isFullyImplemented() {
        return testSuite.isFullyImplemented();
    }

    public EndState getEndState() {
        return testSuite.getEndState();
    }

    public long getTimeTaken() {
        return testSuite.getTimeTaken();
    }

    public int getTotalFeatures() {
        return testSuite.getTotalFeatures();
    }

    public int getTotalScenarios() {
        return testSuite.getTotalScenarios();
    }

    public List<FeatureToken> getFeatureList() {
        return testSuite.getFeatureList();
    }

    public ExecutionToken getExecutionToken() {
        return testSuite.getExecutionToken();
    }

    public String getExecutionHost() {
        return testSuite.getExecutionHost();
    }

    public void accept(TokenVisitor tokenVisitor) {
        testSuite.accept(tokenVisitor);
    }

    public TestSuite getTestSuite() {
        return testSuite;
    }
}
