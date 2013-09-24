package org.chorusbdd.chorus.core.interpreter.invoker;

import org.chorusbdd.chorus.core.interpreter.AbstractInvoker;
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
public abstract class PolledInvoker extends AbstractInvoker {
    public PolledInvoker(Method method) {
        super(method);
    }

    /**
     * Invoke the method
     */
    public Object invoke(final Object obj, final Object... args) throws IllegalAccessException, InvocationTargetException {
        final AtomicReference result = new AtomicReference();

        PolledAssertion p = new PolledAssertion() {
            protected void validate() throws Exception {
                Object r = method.invoke(obj, args);
                result.set(r);
            }

            protected int getPollPeriodMillis() {
                return getPollFrequency();
            }
        };

        TimeUnit timeUnit = getTimeUnit();
        int count = getCount();
        doTest(p, timeUnit, count);
        return result.get();       
    }

    protected abstract int getCount();

    protected abstract TimeUnit getTimeUnit();

    protected abstract int getPollFrequency();

    protected abstract void doTest(PolledAssertion p, TimeUnit timeUnit, int count);
}
