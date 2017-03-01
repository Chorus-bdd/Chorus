package org.chorusbdd.chorus.stepinvoker;

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Created by GA2EBBU on 28/02/2017.
 *
 * Decorate a StepInvoker as an UntilFirstPassInvoker if a StepRetry is configured
 */
public class StepRetryDecorator {

    private ChorusLog log = ChorusLogFactory.getLog(StepRetryDecorator.class);

    public StepInvoker getRetryInvoker(StepInvoker plainInvoker) {

        StepRetry retry = plainInvoker.getRetry();
        long duration = retry.getDuration();
        long interval = plainInvoker.getRetry().getInterval();

        boolean isRetryConfigured = retry.isValid();

        if ( log.isTraceEnabled() && isRetryConfigured ) {
            log.trace("Wrapping step " + plainInvoker.getStepPattern() + " with a StepRetry decorator");
        }

        return isRetryConfigured ? new UntilFirstPassInvoker(plainInvoker, duration, MILLISECONDS, interval) : plainInvoker;
    }
}

