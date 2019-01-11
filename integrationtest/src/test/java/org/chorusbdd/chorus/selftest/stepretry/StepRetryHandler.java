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
import org.chorusbdd.chorus.util.FailImmediatelyException;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 14/06/12
 * Time: 09:21
 */
@Handler("Step Retry")
public class StepRetryHandler {

    private volatile int timeCount = 0;
    
    private int passesWithinPollCount;
    private int passesForPollCount;
    
    private long longMethodStartTime;


    @Step("Chorus is working properly")
    public void isWorkingProperly() {
    }

    @Step("I increment a value with a timer task")
    public void incrementWithTimer() {
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            public void run() {
                timeCount++;    
            }
        }, 300);
    }

    @Step("I increment a value")
    public void increment() {
        timeCount++;
    }
    
    @Step(value = "the value is (\\d) within default period", retryDuration = 10)
    public void passesWithinDefaultSecond(int expectCount) {
        assertEquals("Expect " + expectCount + " but was " + timeCount, expectCount, timeCount);    
    }

    @Step(value = "the value is (\\d) within 2 seconds", retryDuration = 2)
    public void passesWithinTwoSeconds(int expectCount) {
        passesWithinPollCount++;
        assertEquals("Expect " + expectCount + " but was " + timeCount, expectCount, timeCount);
        
        //after 300ms the timer task should set the value, first poll should fail second should pass
        assertTrue("Expect to have been polled at least 2 times but was " + passesWithinPollCount, passesWithinPollCount >= 2);
    }

    @Step(value = "the value is not (\\d) within 0.2 seconds so this step should fail", retryDuration = 200, retryTimeUnit = TimeUnit.MILLISECONDS, retryIntervalMillis = 50)
    public void passesWithinPointTwoSeconds(int expectCount) {
        assertEquals("Expect " + expectCount, expectCount, timeCount);
    }

    @Step(value = "I call a 1 second to run step method with passes within 1 second annotation", retryDuration = 1)
    public void callATenSecondRunningMethod() {
        longMethodStartTime = System.currentTimeMillis();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        
        //this will cause the test to pass if the step method is polled again
        //we actually expect it to fail and check for this failure in the output, since after the first run we will 
        //already be over the allotted 1 second time limit and so the step should not get called again
        if ( System.currentTimeMillis() - longMethodStartTime < 1100) {
            throw new AssertionError("Whoops");            
        }
    }

    @Step("the next step runs 1 second later")
    public void checkRunTimeForLongMethod() {
        assertTrue(System.currentTimeMillis() - longMethodStartTime < 1500);  //allow up to 500 ms extra
    }

    private AtomicLong passesWithinStartTime = new AtomicLong();

    @Step(value = ".*call a passes within step method it can be terminated immediately by FailImmediatelyException", retryDuration = 360)
    public void testFailImmediately() {
        passesWithinStartTime.compareAndSet(0, System.currentTimeMillis());
        long zeroWhenFailingImmediately = (System.currentTimeMillis() - passesWithinStartTime.get()) / 1000;
        throw new FailImmediatelyException("Fail this step immediately - time elapsed " + zeroWhenFailingImmediately + " seconds");
    }
    
}
