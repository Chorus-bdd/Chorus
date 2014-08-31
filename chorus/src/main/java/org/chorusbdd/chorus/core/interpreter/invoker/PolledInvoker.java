/**
 *  Copyright (C) 2000-2013 The Software Conservancy and Original Authors.
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
package org.chorusbdd.chorus.core.interpreter.invoker;

import org.chorusbdd.chorus.core.interpreter.AbstractStepMethodInvoker;
import org.chorusbdd.chorus.handlers.util.PolledAssertion;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * User: nick
 * Date: 20/09/13
 * Time: 18:37
 */
public abstract class PolledInvoker extends AbstractStepMethodInvoker {
    public PolledInvoker(Object classInstance, Method method) {
        super(classInstance, method);
    }

    /**
     * Invoke the method
     */
    public Object invoke(final Object... args) throws IllegalAccessException, InvocationTargetException {
        final AtomicReference resultRef = new AtomicReference();

        PolledAssertion p = new PolledAssertion() {
            protected void validate() throws Exception {
                Object r = method.invoke(getClassInstance(), args);
                resultRef.set(r);
            }

            protected int getPollPeriodMillis() {
                return getPollFrequency();
            }
        };

        TimeUnit timeUnit = getTimeUnit();
        int count = getCount();
        doTest(p, timeUnit, count);

        Object result = handleResultIfReturnTypeVoid(method, resultRef.get());
        return result;
    }

    protected abstract int getCount();

    protected abstract TimeUnit getTimeUnit();

    protected abstract int getPollFrequency();

    protected abstract void doTest(PolledAssertion p, TimeUnit timeUnit, int count);
}
