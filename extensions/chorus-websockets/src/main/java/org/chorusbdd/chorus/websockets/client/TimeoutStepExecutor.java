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

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.websockets.message.ExecuteStepMessage;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

/**
 * Created by nick on 17/12/2016.
 *
 * Allow only one step at a time to be executed and time out/interrupt an executing step
 * once the timeout period allotted expired
 */
class TimeoutStepExecutor {

    private ChorusLog log = ChorusLogFactory.getLog(TimeoutStepExecutor.class);

    private static final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    private AtomicBoolean isRunningAStep = new AtomicBoolean();
    private ExecuteStepMessage currentlyExecutingStep;
    private BiConsumer<String, ExecuteStepMessage> stepFailureConsumer;

    public TimeoutStepExecutor(BiConsumer<String, ExecuteStepMessage> stepFailureConsumer) {
        this.stepFailureConsumer = stepFailureConsumer;
    }

    void runWithinPeriod(Runnable runnable, ExecuteStepMessage executeStepMessage) {
        runWithinPeriod(
            runnable,
            executeStepMessage,
            executeStepMessage.getTimeoutPeriodSeconds(),
            TimeUnit.SECONDS
        );
    }
        /**
         * Run a task on the scheduled executor so that we can try to interrupt it and time out if it fails
         */
    void runWithinPeriod(Runnable runnable, ExecuteStepMessage executeStepMessage, int timeout, TimeUnit unit) {
        if ( ! isRunningAStep.getAndSet(true)) {
            this.currentlyExecutingStep = executeStepMessage;
            Future<String> future = null;
            try {
                future = scheduledExecutorService.submit(runStepAndResetIsRunning(runnable), "OK");
                future.get(timeout, unit);
            } catch (TimeoutException e) {
                //Timed out waiting for the step to run
                //We should try to cancel and interrupt the thread which is running the step - although this isn't
                //guaranteed to succeed.
                future.cancel(true);
                log.warn("A step failed to execute within " + timeout + " " + unit + ", attempting to cancel the step");
                //Here the step server should have timed out the step and proceed already - we don't need to send a failure message
            } catch (Exception e) {
                String ms = "Exception while executing step [" + e.getMessage() + "]";
                log.error(ms, e);
                stepFailureConsumer.accept(ms, executeStepMessage);
            }
        } else {
            //server will time out this step
            String message = "Cannot execute a test step, a step is already in progress [" + currentlyExecutingStep.getStepId() + ", " + currentlyExecutingStep.getPattern() + "]";
            log.error(message);
            stepFailureConsumer.accept(message, executeStepMessage);
        }
    }

    /**
     * Wrap the runnable which executed the step, and only unset currentlyExecutingStep when it has completed
     * If the step blocks and can't be interrupted, then we don't want to start any other steps
     */
    private Runnable runStepAndResetIsRunning(Runnable runnable) {
        return () -> {
            try {
                runnable.run();
            } catch (Throwable t) {
                //we're in control of the runnable and it should catch it's own execeptions, but just in case it doesn't
                log.error("Exeception while running a step", t);
            } finally {
                isRunningAStep.getAndSet(false);
            }
        };
    }
}
