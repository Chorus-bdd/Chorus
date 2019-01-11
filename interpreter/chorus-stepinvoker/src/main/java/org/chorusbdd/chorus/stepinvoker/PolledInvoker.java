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

import org.chorusbdd.chorus.util.PolledAssertion;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

/**
 * User: nick
 * Date: 20/09/13
 * Time: 18:37
 *
 * Wrap another StepInvoker so that we can poll it repeatedly either
 * - until the step passes
 * - to check the step passes for a preset duration
 *
 * This implements the requirements for @PassesWithin annotated handler steps
 */
public abstract class PolledInvoker implements StepInvoker {

    private final StepInvoker wrappedInvoker;
    private final long length;
    private final TimeUnit timeUnit;
    private final long pollFrequency;
    private int retryAttempts;

    public PolledInvoker(StepInvoker wrappedInvoker, long length, TimeUnit timeUnit, long pollFrequency) {
        this.wrappedInvoker = wrappedInvoker;
        this.length = length;
        this.timeUnit = timeUnit;
        this.pollFrequency = pollFrequency;
    }

    /**
     * Invoke the method
     * @param stepTokenId
     * @param args
     */
    public Object invoke(final String stepTokenId, final List<String> args) {
        final AtomicReference resultRef = new AtomicReference();

        PolledAssertion p = new PolledAssertion() {
            protected void validate() throws Exception {
                Object r = wrappedInvoker.invoke(stepTokenId, args);
                resultRef.set(r);
            }

            protected int getPollPeriodMillis() {
                return (int)pollFrequency;
            }
        };

        retryAttempts = doTest(p, timeUnit, length);

        Object result = resultRef.get();
        return result;
    }

    public Pattern getStepPattern() {
        return wrappedInvoker.getStepPattern();
    }

    /**
     * @return true if this step is 'pending' (a placeholder for future implementation) and should not be invoked
     */
    public boolean isPending() {
        return wrappedInvoker.isPending();
    }

    public String getPendingMessage() {
        return wrappedInvoker.getPendingMessage();
    }

    public String getId() {
        return wrappedInvoker.getId();
    }

    /**
     * @return the number of times the test condition was retried, if the initial test failed
     */
    protected abstract int doTest(PolledAssertion p, TimeUnit timeUnit, long length);

    public String getTechnicalDescription() {
        return wrappedInvoker.getTechnicalDescription();
    }

    public String toString() {
        return "Polled:" + wrappedInvoker.toString();
    }

    public StepRetry getRetry() { return wrappedInvoker.getRetry(); };

    public int getRetryAttempts() {
        return retryAttempts;
    }
    
    public boolean isDeprecated() { return wrappedInvoker.isDeprecated(); }
    
    public String getCategory() { return wrappedInvoker.getCategory(); }
}
