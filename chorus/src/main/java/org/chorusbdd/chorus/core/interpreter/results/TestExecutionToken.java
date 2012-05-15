package org.chorusbdd.chorus.core.interpreter.results;

import java.text.SimpleDateFormat;
import java.util.Date;

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

    private static final ThreadLocal<SimpleDateFormat> formatThreadLocal = new ThreadLocal<SimpleDateFormat>() {
        public SimpleDateFormat initialValue() {
            return new SimpleDateFormat("HH:mm:ss");
        }
    };

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
}
