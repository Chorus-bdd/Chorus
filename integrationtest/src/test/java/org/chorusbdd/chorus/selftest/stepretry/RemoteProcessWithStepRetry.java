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
package org.chorusbdd.chorus.selftest.stepretry;

import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.remoting.jmx.ChorusHandlerJmxExporter;
import org.chorusbdd.chorus.util.FailImmediatelyException;
import org.chorusbdd.chorus.util.assertion.ChorusAssert;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.assertTrue;

/**
 * User: nick
 * Date: 25/09/13
 * Time: 09:07
 */
public class RemoteProcessWithStepRetry {
    
    
    public static void main(String[] args) throws InterruptedException {
        new ChorusHandlerJmxExporter(new MyHandler()).export();
        Thread.sleep(100000);
    }
    
    @Handler("My Handler")
    public static class MyHandler {
        
        private volatile boolean trigger;
        private int pollCount;
        
        @Step("I start a timer")
        public void start() {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    trigger = true;
                }
            }, 500);    
        }
        
        @Step(value = "test condition eventually passes", retryDuration = 1)
        public void testCondition() {
            pollCount++;
            assertTrue("Trigger was set", trigger);
            assertTrue("polled several times", pollCount > 1);
        }
        
        @Step(value = ".* test condition fails with AssertionError", retryDuration = 200000, retryTimeUnit = TimeUnit.MILLISECONDS, retryIntervalMillis = 50)
        public void testFails() {
            ChorusAssert.fail("Failed condition");
        }

        @Step(value = ".* test condition fails with Exception", retryDuration = 200)
        public void testFailsWithException() throws Exception {
            throw new Exception("My Exception Message");
        }

        @Step(value = ".* test condition fails with RuntimeException", retryDuration = 200)
        public void testFailsWithRuntimeException() throws Exception {
            throw new RuntimeException("My Runtime Exception Message");
        }

        private AtomicLong passesWithinStartTime = new AtomicLong();

        @Step(value = ".*call a passes within step method remotely it can be terminated immediately by FailImmediatelyException", retryDuration = 360)
        public void testFailImmediately() {
            passesWithinStartTime.compareAndSet(0, System.currentTimeMillis());
            long zeroWhenFailingImmediately = (System.currentTimeMillis() - passesWithinStartTime.get()) / 1000;
            throw new FailImmediatelyException("Fail this step immediately - time elapsed " + zeroWhenFailingImmediately + " seconds");
        }
        
        
    }
}
