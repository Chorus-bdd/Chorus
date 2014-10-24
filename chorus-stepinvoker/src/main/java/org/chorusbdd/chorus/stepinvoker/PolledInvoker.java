/**
 *  Copyright (C) 2000-2014 The Software Conservancy and Original Authors.
 *  All rights reserved.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to
 *  deal in the Software without restriction, including without limitation the
 *  rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 *  sell copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 *  IN THE SOFTWARE.
 *
 *  Nothing in this notice shall be deemed to grant any rights to trademarks,
 *  copyrights, patents, trade secrets or any other intellectual property of the
 *  licensor or any contributor except as expressly stated herein. No patent
 *  license is granted separate from the Software, for code that you delete from
 *  the Software, or for combinations of the Software with other software or
 *  hardware.
 */
package org.chorusbdd.chorus.stepinvoker;

import org.chorusbdd.chorus.util.PolledAssertion;

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

    private StepInvoker wrappedInvoker;

    public PolledInvoker(StepInvoker wrappedInvoker) {
        this.wrappedInvoker = wrappedInvoker;
    }

    /**
     * Invoke the method
     */
    public Object invoke(final Object... args) {
        final AtomicReference resultRef = new AtomicReference();

        PolledAssertion p = new PolledAssertion() {
            protected void validate() throws Exception {
                Object r = wrappedInvoker.invoke(args);
                resultRef.set(r);
            }

            protected int getPollPeriodMillis() {
                return getPollFrequency();
            }
        };

        TimeUnit timeUnit = getTimeUnit();
        int count = getCount();
        doTest(p, timeUnit, count);

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

    /**
     * Chorus needs to extract values from the matched pattern and pass them as parameters when invoking the step
     * @return an array of parameter types the length of which should equal the number of capture groups in the step pattern
     */
    public Class[] getParameterTypes() {
        return wrappedInvoker.getParameterTypes();
    }

    public String getId() {
        return wrappedInvoker.getId();
    }

    protected abstract int getCount();

    protected abstract TimeUnit getTimeUnit();

    protected abstract int getPollFrequency();

    protected abstract void doTest(PolledAssertion p, TimeUnit timeUnit, int count);

    public String getTechnicalDescription() {
        return wrappedInvoker.getTechnicalDescription();
    }

    public String toString() {
        return "Polled:" + wrappedInvoker.toString();
    }

}
