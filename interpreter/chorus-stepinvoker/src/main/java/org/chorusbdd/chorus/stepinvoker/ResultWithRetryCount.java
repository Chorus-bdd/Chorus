package org.chorusbdd.chorus.stepinvoker;

/**
 * Created by nickebbutt on 21/01/2018.
 */
public class ResultWithRetryCount {

    private final Object result;
    private final int retryAttempts;

    private ResultWithRetryCount(Object result, int retryAttempts) {
        this.result = result;
        this.retryAttempts = retryAttempts;
    }

    public Object getResult() {
        return result;
    }

    public int getRetryAttempts() {
        return retryAttempts;
    }

    static ResultWithRetryCount createResult(Object result, int retryAttempts) {
        return new ResultWithRetryCount(result, retryAttempts);
    }

}
