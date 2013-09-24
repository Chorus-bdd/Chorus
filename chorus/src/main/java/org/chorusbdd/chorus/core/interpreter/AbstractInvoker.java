package org.chorusbdd.chorus.core.interpreter;

import org.chorusbdd.chorus.core.interpreter.invoker.StepMethodInvoker;

import java.lang.reflect.Method;

/**
 * User: nick
 * Date: 20/09/13
 * Time: 18:10
 */
public abstract class AbstractInvoker implements StepMethodInvoker {

    protected Method method;

    public AbstractInvoker(Method method) {
        this.method = method;
    }

    /**
     * Returns the name of the method represented by this {@code Method}
     * object, as a {@code String}.
     */
    public String getName() {
        return method.getName();
    }

    public String toString() {
        return getClass().getSimpleName() + ":" + getName();
    }
}
