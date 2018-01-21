package org.chorusbdd.chorus.stepinvoker;

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;

import java.util.List;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.chorusbdd.chorus.stepinvoker.ResultWithRetryCount.createResult;

/**
 * Created by Nick E on 28/02/2017.
 *
 * Invoke a stepInvoker using an UntilFirstPassInvoker if the StepRetry settings require it
 */
public class StepRetryDecorator {

    private ChorusLog log = ChorusLogFactory.getLog(StepRetryDecorator.class);

    private StepInvoker foundStepInvoker;

    public StepRetryDecorator(StepInvoker foundStepInvoker) {
        this.foundStepInvoker = foundStepInvoker;
    }

    public ResultWithRetryCount invoke(List<String> args) throws Exception {
        ResultWithRetryCount result;
        StepRetry retry = foundStepInvoker.getRetry();
        if ( retry.isValid()) {
            result = invokeWithRetry(args, retry);
        } else {
            result = createResult(foundStepInvoker.invoke(args), 0);
        }
        return result;
    }

    private ResultWithRetryCount invokeWithRetry(List<String> args, StepRetry retry) {
        Object result;
        if ( log.isTraceEnabled()) {
            log.trace("Wrapping step " + foundStepInvoker.getStepPattern() + " with a StepRetry decorator");
        }

        long duration = retry.getDuration();
        long interval = retry.getInterval();
        
        UntilFirstPassInvoker i = new UntilFirstPassInvoker(foundStepInvoker, duration, MILLISECONDS, interval);
        result = i.invoke(args);
        
        return createResult(result, i.getRetryAttempts());
    }
}

