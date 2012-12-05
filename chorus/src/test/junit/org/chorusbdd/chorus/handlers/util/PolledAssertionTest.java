package org.chorusbdd.chorus.handlers.util;

import junit.framework.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicLong;

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
            }.await(1);
            fail("Should have raised AssertionError");
        } catch (AssertionError e) {}
    }

    @Test
    public void testCheckedConditionPasses() throws Exception {
        final boolean[] condition = {true};
        final AtomicLong count = new AtomicLong();

        new PolledAssertion() {
            protected void validate() {
                count.incrementAndGet();
                assertTrue(condition[0]);
            }
        }.check(0.3f);
        assertTrue("Expect count > 2", count.get() > 2);
    }

    @Test
    public void testCheckedConditionFails() throws Exception {
        final boolean[] condition = {false};
        final AtomicLong count = new AtomicLong();

        try {
            new PolledAssertion() {
                protected void validate() {
                    count.incrementAndGet();
                    assertTrue(condition[0]);
                }
            }.check(0.3f);
        } catch (AssertionError ae) {
        }
        assertTrue("Expect count == 1", count.get() == 1);
    }
}
