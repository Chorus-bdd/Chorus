package org.chorusbdd.chorus.handlerutils;

/**
 * Created by nick on 30/07/2014.
 *
 * This exception can be thrown during a PolledAssertion or a step using the @PassesWithin annotation to break out of the
 * polling and fail the test immediately
 *
 * This can be useful to avoid waiting for the polling period to expire, if it becomes clear that the condition
 * being tested can never be satisfied.
 *
 * e.g.
 *
 * @PassesWithin(length=60, timeUnit=TimeUnit.SECONDS)
 * @Step("wait for a message to be received")
 * public void testMessageReceived() {
*      if ( transport.failedToConnect() ) {
 *         //this test can now never pass, so break out immediately rather than wait for the rest of the 60 seconds
*          throw new FailImmediatelyException("Failed to connect to messaging transport");
*      }
 *
*      assertTrue("at least one message should be received", messageCount > 0);
 * }
 *
 */
public class FailImmediatelyException extends RuntimeException {

    public FailImmediatelyException(String description) {
        super(description);
    }
}
