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
package org.chorusbdd.chorus.websockets.client;

import org.chorusbdd.chorus.websockets.message.ExecuteStepMessage;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Created by nick on 17/12/2016.
 */
public class TimeoutStepExecutorTest {

    @Test
    public void testAStepExecutionCanBeInterrupted() throws Exception {

        CountDownLatch countDownLatch = new CountDownLatch(1);

        TimeoutStepExecutor stepExecutor = new TimeoutStepExecutor((a, b) -> {});
        stepExecutor.runWithinPeriod(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                countDownLatch.countDown();
            }
        }, new ExecuteStepMessage(), 10, TimeUnit.MILLISECONDS);

        boolean ok = countDownLatch.await(1, TimeUnit.SECONDS);
        if ( ! ok) {
            fail("Not interrupted");
        }
    }

}