/**
 *  Copyright (C) 2000-2013 The Software Conservancy and Original Authors.
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
package org.chorusbdd.chorus.handlers.util;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 15/11/12
 * Time: 17:37
 *
 * PolledAssertion provides an easy way to wait for conditions to be satisfied, or to check
 * conditions hold for the duration of a preset period.
 *
 * A subclass must be provided which implements the validate() method to check one or more conditions.
 * The implementation should throw an AssertionError if conditions fail.
 * This validate() method will be repeatedly polled
 *
 * There are two main usage patterns for PolledAssertion - await() and check():
 * polledAssertion.await(5);
 * - wait for up to five seconds while polling for the conditions to pass
 *
 * polledAssertion.check(10);
 * - check that the conditions pass for the duration of a ten second period
 *
 * The await feature can be very useful to eliminate sleeps in features.
 *
 * The check feature is useful when checking that an event does not occur within a given time frame
 * eg. if I am not expecting to receive a message and I have a message counter which starts at
 * zero messages. I may check for 5 seconds that my message count remains zero.
 *
 * e.g. wait for up to 5 seconds for a message to arrive:
 *
 * @Step("a message was received with ID=(.*)")
 * public void checkMessageWasReceived(final String id) {
 *   new PolledAssertion() {
 *      protected void validate() {
 *          Message p = messageCache.getMessage(id);
 *          assertNotNull("The message exists", p);
 *      }
 *   }.await(5);
 * }
 */
public abstract class PolledAssertion {

    /**
     * @return timeout period in seconds
     */
    protected int getTimeoutSeconds() {
        return 10;
    }

    /**
     * @return millisecond period over which condition will be periodically evaluated
     */
    protected int getPollPeriodMillis() {
        return 100;
    }

    /**
     * Subclass should implement this method to throw an AssertionError if test conditions are not satisfied
     *
     * @throws AssertionError if condition is not satisfied
     */
    protected abstract void validate() throws Exception;

    /**
     * Wait for the assertions to pass for the duration of the timeout period
     *
     * Validation will be attempted and errors handled silently until the timeout period expires after which assertion
     * errors will be propagated and will cause test failure
     */
    public void await() {
        await(getTimeoutSeconds());
    }

    public void await(float seconds) {
        await(TimeUnit.MILLISECONDS, (int)(seconds * 1000));
    }
        
    /**
     * Wait for the assertions to pass for the specified time limit
     *
     * Validation will be attempted and errors handled silently until the timeout period expires after which assertion
     * errors will be propagated and will cause test failure
     */
    public void await(TimeUnit unit, int count) {
        int pollPeriodMillis = getPollPeriodMillis();
        int maxAttempts = (int)(unit.toMillis(count) / pollPeriodMillis);
        boolean success = false;
        for ( int check = 1; check < maxAttempts ; check ++) {  //try maxAttempts - 1 times snaffling any errors
            try {
                validate();
                //no assertion errors? condition passes, we can continue
                success = true;
                break;
            } catch (Throwable r) {
                //ignore failures up until the last check
            }
            doSleep(pollPeriodMillis);
        }

        if ( ! success ) {
            try {
                validate(); //this time allow any assertion errors to propagate
            } catch (Throwable e) {
                propagateAsError(e);
            }
        }
    }

    /**
     * check that the assertions pass for the whole duration of the timeout period
     */
    public void check() {
        check(getTimeoutSeconds());
    }

    public void check(float seconds) {
        check(TimeUnit.MILLISECONDS, (int) (seconds * 1000));    
    }
    
    /**
     * check that the assertions pass for the whole duration of the period specified
     */
    public void check(TimeUnit timeUnit, int count) {
        int pollPeriodMillis = getPollPeriodMillis();
        int maxAttempts = (int)(timeUnit.toMillis(count) / pollPeriodMillis);
        maxAttempts = Math.max(maxAttempts, 1); //always check at least once
        for ( int check = 0; check < maxAttempts ; check ++) {
            try {
                validate();
            } catch (Throwable t) {
                propagateAsError(t);
            }
            doSleep(pollPeriodMillis);
        }
    }

    private void doSleep(int pollPeriodMillis) {
        try {
            Thread.sleep(pollPeriodMillis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //If e is an InvocationTargetException (from a PolledInvoker) we always need to unwrap and propagate the cause
    //if an Error propagate as is
    //Otherwise wrap with a PolledAssertionError and throw 
    private void propagateAsError(Throwable t) {
        if ( t instanceof InvocationTargetException) {
            t = t.getCause();   
        }
        if ( Error.class.isAssignableFrom(t.getClass())) {
            throw (Error)t;    
        }
        throw new PolledAssertionError(t.getMessage(), t);
    }

    public static class PolledAssertionError extends AssertionError {
        public PolledAssertionError(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
