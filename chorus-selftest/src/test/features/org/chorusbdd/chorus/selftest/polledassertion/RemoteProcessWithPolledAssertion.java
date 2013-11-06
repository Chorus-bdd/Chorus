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
package org.chorusbdd.chorus.selftest.polledassertion;

import org.chorusbdd.chorus.annotations.*;
import org.chorusbdd.chorus.remoting.jmx.ChorusHandlerJmxExporter;
import org.chorusbdd.chorus.util.assertion.ChorusAssert;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * User: nick
 * Date: 25/09/13
 * Time: 09:07
 */
public class RemoteProcessWithPolledAssertion {
    
    
    public static void main(String[] args) throws InterruptedException {
        new ChorusHandlerJmxExporter(new MyHandler()).export();
        Thread.sleep(10000);
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
        
        @Step("test condition eventually passes")
        @PassesWithin(length = 1)
        public void testCondition() {
            pollCount++;
            assertTrue("Trigger was set", trigger);
            assertTrue("polled several times", pollCount > 1);
        }
        
        @Step(".* test condition fails with AssertionError")
        @PassesWithin(length = 200, timeUnit = TimeUnit.MILLISECONDS, pollMode = PollMode.PASS_THROUGHOUT_PERIOD)
        public void testFails() {
            ChorusAssert.fail("Failed condition");
        }

        @Step(".* test condition fails with Exception")
        @PassesWithin(length = 200, timeUnit = TimeUnit.MILLISECONDS, pollMode = PollMode.PASS_THROUGHOUT_PERIOD)
        public void testFailsWithException() throws Exception {
            throw new Exception("My Exception Message");
        }

        @Step(".* test condition fails with RuntimeException")
        @PassesWithin(length = 200, timeUnit = TimeUnit.MILLISECONDS, pollMode = PollMode.PASS_THROUGHOUT_PERIOD)
        public void testFailsWithRuntimeException() throws Exception {
            throw new RuntimeException("My Runtime Exception Message");
        }
        
        
    }
}
