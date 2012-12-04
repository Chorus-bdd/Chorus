package org.chorusbdd.chorus.handlers.util;

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
    protected abstract void validate();

    /**
     * Wait for the assertions to pass for the duration of the timeout period
     *
     * Validation will be attempted and errors handled silently until the timeout period expires after which assertion
     * errors will be propagated and will cause test failure
     */
    public void await() {
        await(getTimeoutSeconds());
    }

    /**
     * Wait for the assertions to pass for the specified time limit
     *
     * Validation will be attempted and errors handled silently until the timeout period expires after which assertion
     * errors will be propagated and will cause test failure
     */
    public void await(float seconds) {
        int pollPeriodMillis = getPollPeriodMillis();
        int maxAttempts = (int)((1000 * seconds) / pollPeriodMillis);
        boolean success = false;
        for ( int check = 1; check < maxAttempts ; check ++) {  //try maxAttempts - 1 times snaffling any errors
            try {
                validate();
                //no assertion errors? condition passes, we can continue
                success = true;
                break;
            } catch (AssertionError r) {
                //ignore assertion failures up until the last check
            }
            doSleep(pollPeriodMillis);
        }

        if ( ! success ) {
            validate(); //this time allow any assertion errors to propagate
        }
    }

    /**
     * check that the assertions pass for the whole duration of the timeout period
     */
    public void check() {
        check(getTimeoutSeconds());
    }

    /**
     * check that the assertions pass for the whole duration of the period specified
     */
    public void check(float seconds) {
        int pollPeriodMillis = getPollPeriodMillis();
        int maxAttempts = (int)((1000 * seconds) / pollPeriodMillis);
        maxAttempts = Math.max(maxAttempts, 1); //always check at least once
        for ( int check = 0; check < maxAttempts ; check ++) {
            validate();
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
}
