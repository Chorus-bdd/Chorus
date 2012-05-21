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

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 15/05/12
 * Time: 15:15
 *
 * When the interpreter starts it creates a token which represents that unique execution of the
 * interpreter. This token is passed to each callback of a ChorusInterpreterExecutionListener to
 * identify which test execution triggered the callback.
 *
 * The execution token also wraps a ResultsSummary - this gives an up to date summary of current
 * test results
 *
 * n.b. this class does not actually contain any features/scenarios/step tokens - this is because
 * it is sent with each callback on the ChorusExecutionListener, and may be serialized and sent at each stage
 * It would be too heavyweight to send the entire tree of features/scenarios/steps each time, but a summary of
 * current results is beneficial. A full list of features is send on completion.
 *
 * This will enable two things -->
 * - parallelisation of test execution, with output separated by execution token
 * - collation of results across multiple test executions, in a swing test runner, for example
 *
 * @TODO - add more information, eg. classpath, params to the interpreter?
 * Might be nice to be able to check these in the results post testing
 */
public class TestExecutionToken implements ResultToken {

    private static final long serialVersionUID = 1;

    private static final ThreadLocal<SimpleDateFormat> formatThreadLocal = new ThreadLocal<SimpleDateFormat>() {
        public SimpleDateFormat initialValue() {
            return new SimpleDateFormat("HH:mm:ss");
        }
    };

    private final String testSuiteName;
    private final long executionStartTime;
    private ResultsSummary resultsSummary = new ResultsSummary();

    public TestExecutionToken(String testSuiteName) {
        this(testSuiteName, System.currentTimeMillis());
    }

    public TestExecutionToken(String testSuiteName, long executionStartTime) {
        this.testSuiteName = testSuiteName;
        this.executionStartTime = executionStartTime;
    }

    public int getScenariosPassed() {
        return resultsSummary.getScenariosPassed();
    }

    public void incrementScenariosFailed() {
        resultsSummary.incrementScenariosFailed();
    }

    public int getUnavailableHandlers() {
        return resultsSummary.getUnavailableHandlers();
    }

    public void incrementStepsFailed() {
        resultsSummary.incrementStepsFailed();
    }

    public int getStepsSkipped() {
        return resultsSummary.getStepsSkipped();
    }

    public int getScenariosFailed() {
        return resultsSummary.getScenariosFailed();
    }

    public void incrementStepsSkipped() {
        resultsSummary.incrementStepsSkipped();
    }

    public void incrementStepsUndefined() {
        resultsSummary.incrementStepsUndefined();
    }

    public void incrementUnavailableHandlers() {
        resultsSummary.incrementUnavailableHandlers();
    }

    public int getStepsPending() {
        return resultsSummary.getStepsPending();
    }

    public int getStepsFailed() {
        return resultsSummary.getStepsFailed();
    }

    public void incrementScenariosPassed() {
        resultsSummary.incrementScenariosPassed();
    }

    public int getStepsPassed() {
        return resultsSummary.getStepsPassed();
    }

    public void incrementStepsPending() {
        resultsSummary.incrementStepsPending();
    }

    public int getStepsUndefined() {
        return resultsSummary.getStepsUndefined();
    }

    public void incrementStepsPassed() {
        resultsSummary.incrementStepsPassed();
    }

    /**
     * @return this is useful, for cases where we simplify results into three categories - passed, failed, and anything else
     */
    public int getUndefinedPendingOrSkipped() {
        return resultsSummary.getUndefinedPendingOrSkipped();
    }

    public ResultsSummary getResultsSummary() {
        return resultsSummary;
    }

    public String getTestSuiteName() {
        return testSuiteName;
    }

    public TestExecutionToken deepCopy() {
        TestExecutionToken t = new TestExecutionToken(testSuiteName, executionStartTime);
        t.resultsSummary = resultsSummary.deepCopy();
        return t;
    }

    public String toString() {
        return ( "".equals(testSuiteName) ? "" : testSuiteName +
                " (") + formatThreadLocal.get().format(new Date(executionStartTime))  + ")";
    }


    //a working equals and hashcode is required by ChorusViewer
    //if this is running remotely, we may receive a new deserialized instance each time and cannot rely on reference
    //equality
    //equals and hashcode should not include results, since these are mutable and comparisons would break

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestExecutionToken that = (TestExecutionToken) o;

        if (executionStartTime != that.executionStartTime) return false;
        if (testSuiteName != null ? !testSuiteName.equals(that.testSuiteName) : that.testSuiteName != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = testSuiteName != null ? testSuiteName.hashCode() : 0;
        result = 31 * result + (int) (executionStartTime ^ (executionStartTime >>> 32));
        return result;
    }


    /**
     * @return true, if all scenarios and steps were implemented and not pending
     */
    public boolean isFullyImplemented() {
        return resultsSummary.isFullyImplemented();
    }

    /**
     * @return true, if there were no step failures (but there may have been steps undefined, pending or skipped)
     */
    public boolean isPassed() {
        return resultsSummary.isPassed();
    }

    /**
     * @return true, if no step failures, and no undefined or pending steps
     */
    public boolean isPassedAndFullyImplemented() {
        return isPassed() && isFullyImplemented();
    }
}
