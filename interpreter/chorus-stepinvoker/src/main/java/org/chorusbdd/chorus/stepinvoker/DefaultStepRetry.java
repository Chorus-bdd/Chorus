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
package org.chorusbdd.chorus.stepinvoker;

import org.chorusbdd.chorus.annotations.Step;

import java.util.concurrent.TimeUnit;

/**
 * Created by Nick E on 28/02/2017.
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
