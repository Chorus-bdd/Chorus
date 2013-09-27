package org.chorusbdd.chorus.selftest.polledassertion;

import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.PassesFor;
import org.chorusbdd.chorus.annotations.PassesWithin;
import org.chorusbdd.chorus.annotations.Step;
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
        @PassesFor(length = 200, timeUnit = TimeUnit.MILLISECONDS)
        public void testFails() {
            ChorusAssert.fail("Failed condition");
        }

        @Step(".* test condition fails with Exception")
        @PassesFor(length = 200, timeUnit = TimeUnit.MILLISECONDS)
        public void testFailsWithException() throws Exception {
            throw new Exception("My Exception Message");
        }

        @Step(".* test condition fails with RuntimeException")
        @PassesFor(length = 200, timeUnit = TimeUnit.MILLISECONDS)
        public void testFailsWithRuntimeException() throws Exception {
            throw new RuntimeException("My Runtime Exception Message");
        }
        
        
    }
}
