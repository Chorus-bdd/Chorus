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
     * @return 0 if there is no retry, or a value in milliseconds is greater or equal to 1
     */
    long getDuration();


    /**
     * Defines the interval between retries
     *
     * @return 0 if there is no retry, or a value in milliseconds greater than or equal to 1 and less than or equal to duration
     */
    long getInterval();
}
