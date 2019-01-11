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
package org.chorusbdd.chorus.selftest.polledassertion;

import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.PassesWithin;
import org.chorusbdd.chorus.annotations.PollMode;
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
@Handler("Polled Assertion")
public class PolledAssertionHandler {

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
    
    @Step("the value is (\\d) within default period")
    @PassesWithin()
    public void passesWithinDefaultSecond(int expectCount) {
        assertEquals("Expect " + expectCount + " but was " + timeCount, expectCount, timeCount);    
    }

    @Step("the value is (\\d) within 2 seconds")
    @PassesWithin(length = 2)
    public void passesWithinTwoSeconds(int expectCount) {
        passesWithinPollCount++;
        assertEquals("Expect " + expectCount + " but was " + timeCount, expectCount, timeCount);
        
        //after 300ms the timer task should set the value, first poll should fail second should pass
        assertTrue("Expect to have been polled at least 2 times", passesWithinPollCount >= 2 && passesWithinPollCount < 5);
    }

    @Step("the value is not (\\d) within 0.2 seconds so this step should fail")
    @PassesWithin(length = 200, timeUnit = TimeUnit.MILLISECONDS, pollFrequencyInMilliseconds = 50)
    public void passesWithinPointTwoSeconds(int expectCount) {
        assertEquals("Expect " + expectCount, expectCount, timeCount);
    }

    @Step("the value is (\\d) for half a second")
    @PassesWithin(length = 500, timeUnit = TimeUnit.MILLISECONDS, pollFrequencyInMilliseconds = 50, pollMode = PollMode.PASS_THROUGHOUT_PERIOD)
    public void passesForHalfASecond(int expectCount) {
        passesForPollCount++;
        assertEquals("Expect " + expectCount, expectCount, timeCount);
    }

    @Step("the check method was polled (\\d+) times")
    public void checkPolledMultipleTimes(int times) {
        assertEquals("Expect to have been polled at least " + times + " times but was " + passesForPollCount, times, passesForPollCount);
    }
    
    @Step("I call a 1 second to run step method with passes within 1 second annotation")
    @PassesWithin(length=1, timeUnit = TimeUnit.SECONDS, pollFrequencyInMilliseconds = 100)
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

    @Step("I call a 1 second to run step method with passes throughout 1 second annotation")
    @PassesWithin(length=1, timeUnit = TimeUnit.SECONDS, pollMode = PollMode.PASS_THROUGHOUT_PERIOD, pollFrequencyInMilliseconds = 100)
    public void callATenSecondRunningMethodWithPassThroughout() {
        longMethodStartTime = System.currentTimeMillis();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
    }
    
    @Step("the next step runs 1 second later")
    public void checkRunTimeForLongMethod() {
        assertTrue(System.currentTimeMillis() - longMethodStartTime < 1500);  //allow up to 500 ms extra
    }


    private AtomicLong passesWithinStartTime = new AtomicLong();

    @Step(".*call a passes within step method it can be terminated immediately by FailImmediatelyException")
    @PassesWithin(length=360, timeUnit = TimeUnit.SECONDS)
    public void testFailImmediately() {
        passesWithinStartTime.compareAndSet(0, System.currentTimeMillis());
        long zeroWhenFailingImmediately = (System.currentTimeMillis() - passesWithinStartTime.get()) / 1000;
        throw new FailImmediatelyException("Fail this step immediately - time elapsed " + zeroWhenFailingImmediately + " seconds");
    }
    
}
