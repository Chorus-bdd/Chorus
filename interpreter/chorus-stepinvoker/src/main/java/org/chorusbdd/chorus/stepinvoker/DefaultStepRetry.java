package org.chorusbdd.chorus.stepinvoker;

import org.chorusbdd.chorus.annotations.Step;

import java.util.concurrent.TimeUnit;

/**
 * Created by GA2EBBU on 28/02/2017.
 *
 * A configurable retry on a Chorus step - if this is set to
 */
public class DefaultStepRetry implements StepRetry {

    private final long duration;
    private final long interval;

    public DefaultStepRetry(long duration, long interval) {
        this.duration = duration;
        this.interval = interval;
    }

    @Override
    public long getDuration() {
        return duration;
    }

    @Override
    public long getInterval() {
        return interval;
    }


    public static StepRetry fromStepAnnotation(Step step) {
        long durationInMillis = TimeUnit.MILLISECONDS.convert(step.retryDuration(), step.retryTimeUnit());
        return createStepRetry(durationInMillis, step.retryIntervalMillis());
    }

    public static StepRetry createStepRetry(long retryDuration, long retryInterval) {
        return StepRetry.isValidRetry(retryDuration, retryInterval) ?
                new DefaultStepRetry(retryDuration, retryInterval) :
                StepRetry.NO_RETRY;
    }

}
