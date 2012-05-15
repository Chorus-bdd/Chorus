package org.chorusbdd.chorus.core.interpreter.results;

/**
 * Created with IntelliJ IDEA.
 * User: GA2EBBU
 * Date: 15/05/12
 * Time: 15:15
 *
 * When the interpreter starts it creates a token which represents that unique execution of the
 * interpreter. This token is passed to each callback of a ChorusInterpreterExecutionListener to
 * identify which test execution triggered the callback.
 *
 * This will enable two things -->
 * - parallelisation of test execution, with output separated by execution token
 * - collation of results across multiple test executions, in a swing test runner, for example
 *
 * @TODO - add more information, eg. classpath, params to the interpreter?
 * Might be nice to be able to check these in the results post testing
 */
public class TestExecutionToken implements ResultToken {

    private long executionStartTime;

    public TestExecutionToken() {
        this(System.currentTimeMillis());
    }

    public TestExecutionToken(long executionStartTime) {
        this.executionStartTime = executionStartTime;
    }

    public TestExecutionToken deepCopy() {
        return new TestExecutionToken(executionStartTime);
    }
}
