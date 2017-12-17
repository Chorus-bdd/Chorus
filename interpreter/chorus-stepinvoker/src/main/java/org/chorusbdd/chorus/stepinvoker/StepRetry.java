package org.chorusbdd.chorus.stepinvoker;

/**
 * Created by Nick E on 28/02/2017.
 */
public interface StepRetry {

    StepRetry NO_RETRY = new DefaultStepRetry(0,0);

    static boolean isValidRetry(long retryDuration, long retryInterval) {
        return retryDuration > 0 && retryInterval > 0 && retryInterval < retryDuration;
    }

    default boolean isValid() {
        return isValidRetry(getDuration(), getInterval());
    }

    /**
     * Total length of time over which to retry a failing step
     *
     * @return 0 if there is no retry, or a value in milliseconds >= 1
     */
    long getDuration();


    /**
     * Defines the interval between retries
     *
     * @return 0 if there is no retry, or a value in milliseconds >= 1 and <= duration
     */
    long getInterval();
}
