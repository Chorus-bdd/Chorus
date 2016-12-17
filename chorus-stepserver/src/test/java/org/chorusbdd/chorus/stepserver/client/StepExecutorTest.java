package org.chorusbdd.chorus.stepserver.client;

import org.chorusbdd.chorus.stepserver.message.ExecuteStepMessage;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Created by nick on 17/12/2016.
 */
public class StepExecutorTest {

    @Test
    public void testAStepExecutionCanBeInterrupted() throws Exception {

        CountDownLatch countDownLatch = new CountDownLatch(1);

        StepExecutor stepExecutor = new StepExecutor((a,b) -> {});
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