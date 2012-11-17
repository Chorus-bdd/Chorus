package org.chorusbdd.chorus.handlers.util;

import junit.framework.Assert;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 17/11/12
 * Time: 21:30
 * To change this template use File | Settings | File Templates.
 */
public class PolledAssertionTest extends Assert {

    @Test
    public void testPolledConditionPasses() throws Exception {
        final boolean[] condition = {false};

        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                condition[0] = true;
            }
        }).start();

        new PolledAssertion() {
            @Override
            protected void validate() {
                assertTrue(condition[0]);
            }
        }.await();
    }

    @Test
    public void testPolledConditionFails() throws Exception {
        final boolean[] condition = {false};
        try {
            new PolledAssertion() {
                @Override
                protected void validate() {
                    assertTrue(condition[0]);
                }

                public int getTimeoutSeconds() {
                    return 1;
                }
            }.await();
            fail("Should have raised AssertionError");
        } catch (AssertionError e) {}
    }
}
