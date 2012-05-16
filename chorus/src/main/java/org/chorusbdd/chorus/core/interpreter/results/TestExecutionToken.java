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

    private static final ThreadLocal<SimpleDateFormat> formatThreadLocal = new ThreadLocal<SimpleDateFormat>() {
        public SimpleDateFormat initialValue() {
            return new SimpleDateFormat("HH:mm:ss");
        }
    };

    private long executionStartTime;
    private ResultsSummary resultsSummary = new ResultsSummary();

    public TestExecutionToken() {
        this(System.currentTimeMillis());
    }

    public TestExecutionToken(long executionStartTime) {
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

    public ResultsSummary getResultsSummary() {
        return resultsSummary;
    }

    public TestExecutionToken deepCopy() {
        TestExecutionToken t = new TestExecutionToken(executionStartTime);
        t.resultsSummary = resultsSummary.deepCopy();
        return t;
    }

    public String toString() {
        return "Tests at " + formatThreadLocal.get().format(new Date(executionStartTime));
    }


    //a working equals and hashcode is required by ChorusViewer
    //if this is running remotely, we may receive a new deserialized instance each time and cannot rely on reference
    //equality

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestExecutionToken that = (TestExecutionToken) o;

        if (executionStartTime != that.executionStartTime) return false;

        return true;
    }

    public int hashCode() {
        return (int) (executionStartTime ^ (executionStartTime >>> 32));
    }

    /**
     * @return true, not meaningful for the TestExecutionToken in the same manner as the other token types
     */
    public boolean isFullyImplemented() {
        return resultsSummary.isFullyImplemented();
    }

    public boolean isPassed() {
        return resultsSummary.isPassed();
    }
}
