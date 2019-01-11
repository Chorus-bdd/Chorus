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

    public ResultWithRetryCount invoke(final String stepTokenId, List<String> args) throws Exception {
        ResultWithRetryCount result;
        StepRetry retry = foundStepInvoker.getRetry();
        if ( retry.isValid()) {
            result = invokeWithRetry(stepTokenId, args, retry);
        } else {
            result = createResult(foundStepInvoker.invoke(stepTokenId, args), 0);
        }
        return result;
    }

    private ResultWithRetryCount invokeWithRetry(final String stepTokenId, List<String> args, StepRetry retry) {
        Object result;
        if ( log.isTraceEnabled()) {
            log.trace("Wrapping step " + foundStepInvoker.getStepPattern() + " with a StepRetry decorator");
        }

        long duration = retry.getDuration();
        long interval = retry.getInterval();
        
        UntilFirstPassInvoker i = new UntilFirstPassInvoker(foundStepInvoker, duration, MILLISECONDS, interval);
        result = i.invoke(stepTokenId, args);
        
        return createResult(result, i.getRetryAttempts());
    }
}

