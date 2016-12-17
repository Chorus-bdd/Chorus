package org.chorusbdd.chorus.stepserver.client;

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.stepserver.message.ExecuteStepMessage;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

/**
 * Created by nick on 17/12/2016.
 */
class StepExecutor {

    private ChorusLog log = ChorusLogFactory.getLog(StepExecutor.class);

    private static final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    private AtomicBoolean isRunningAStep = new AtomicBoolean();
    private ExecuteStepMessage currentlyExecutingStep;
    private BiConsumer<String, ExecuteStepMessage> stepFailureConsumer;

    public StepExecutor(BiConsumer<String, ExecuteStepMessage> stepFailureConsumer) {
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
                future = scheduledExecutorService.submit(runnable, "OK");
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
            } finally {
                isRunningAStep.getAndSet(false);
            }
        } else {
            //server will time out this step
            String message = "Cannot execute a test step, a step is already in progress [" + currentlyExecutingStep.getStepId() + ", " + currentlyExecutingStep.getPattern() + "]";
            log.error(message);
            stepFailureConsumer.accept(message, executeStepMessage);
        }
    }
}
