package org.chorusbdd.chorus.handlers.util;

/**
 * Created with IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 15/11/12
 * Time: 17:37
 *
 * An easy way for a Handler step method to wait for conditions to be satisfied
 * by providing Assertions which are polled until they pass or the timeout period
 * expires. This can be very useful to eliminate sleeps in features.
 *
 * e.g.
 *
 * @Step("a price was received with ID=(.*) BID=(.*) and ASK=(.*)")
 * public void checkPrices(final String id, final double bid, final double ask) {
 *   new PolledAssertion() {
 *      protected void validate() {
 *          p = messageCache.getPrices(id);
 *          assertNotNull("The price record exists", p);
 *          assertEquals("BID is equal", bid, p.get("BID"));
 *          assertEquals("ASK is equal", ask, p.get("ASK"));
 *      }
 *   }.await();
 * }
 */
public abstract class PolledAssertion {

    private float timeoutSeconds = 10;     //as float to support 0.5 seconds
    private int pollPeriodMillis = 100;

    public PolledAssertion() {}

    public PolledAssertion(float timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    public PolledAssertion(float timeoutSeconds, int pollPeriodMillis) {
        this.timeoutSeconds = timeoutSeconds;
        this.pollPeriodMillis = pollPeriodMillis;
    }

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
     * @return timeout as a float which may be a fraction of a second
     */
    protected float getTimeout() {
        return getTimeoutSeconds();
    }

    /**
     * Subclass should implement this method to throw an AssertionError if test conditions are not satisfied
     *
     * Validation will be attempted and errors handled silently until the timeout period expires after which assertion
     * errors will be propagated and will cause test failure
     *
     * @throws AssertionError if condition is not satisfied
     */
    protected abstract void validate();

    public void await() {
        int pollPeriodMillis = getPollPeriodMillis();
        int maxAttempts = (int)((1000 * getTimeout()) / pollPeriodMillis);
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

    private void doSleep(int pollPeriodMillis) {
        try {
            Thread.sleep(pollPeriodMillis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
