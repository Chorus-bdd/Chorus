package org.chorusbdd.chorus.stepserver.client;

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;

import java.util.concurrent.*;

/**
 * Created by nick on 17/12/2016.
 */
class StepExecutor {

    private ChorusLog log = ChorusLogFactory.getLog(StepExecutor.class);

    private static final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    void doActionWithinPeriodOrLogFailure(Runnable runnable, int timeout) {
        doActionWithinPeriodOrLogFailure(runnable, timeout, TimeUnit.SECONDS);
    }
        /**
         * Run a task on the scheduled executor so that we can try to interrupt it and time out if it fails
         */
    void doActionWithinPeriodOrLogFailure(Runnable runnable, int timeout, TimeUnit unit) {
        Future<String> future = scheduledExecutorService.submit(runnable, "OK");
        try {
            String result = future.get(timeout, unit);
        } catch (TimeoutException e) {
            future.cancel(true);
            log.warn("A step failed to execute within " + timeout + " " + unit + ", attempting to cancel the step");
        } catch (Exception e) {
            log.error("An error occured while executing a step", e);
        }
    }
}
