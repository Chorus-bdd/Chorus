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
package org.chorusbdd.chorus.util;

import junit.framework.Assert;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
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

    @Test
    public void testICanFailImmediatelyWhileAwaiting() throws Exception {
        long timeStart = System.currentTimeMillis();
        try {
            new PolledAssertion() {
                protected void validate() {
                   throw new FailImmediatelyException("Fail Immediately");
                }
            }.check(10);
        } catch (AssertionError ae) {
        }
        assertTrue("Expect time < 1s", ((System.currentTimeMillis() - timeStart) / 1000) < 1);
    }

    @Test
    public void testICanFailImmediatelyIfFailImmediatelyIsCause() throws Exception {
        long timeStart = System.currentTimeMillis();
        try {
            new PolledAssertion() {
                protected void validate() throws InvocationTargetException {
                    FailImmediatelyException f = new FailImmediatelyException("Fail Immediately");
                    throw new InvocationTargetException(f);
                }
            }.check(10);
        } catch (AssertionError ae) {
        }
        assertTrue("Expect time < 1s", ((System.currentTimeMillis() - timeStart) / 1000) < 1);
    }
}
