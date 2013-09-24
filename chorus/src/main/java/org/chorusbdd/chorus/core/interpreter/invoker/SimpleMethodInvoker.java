package org.chorusbdd.chorus.core.interpreter.invoker;

import org.chorusbdd.chorus.core.interpreter.AbstractInvoker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
* User: nick
* Date: 24/09/13
* Time: 18:46
*/
class SimpleMethodInvoker extends AbstractInvoker {
    
    public SimpleMethodInvoker(Method method) {
        super(method);
        this.method = method;
    }

    public Object invoke(Object obj, Object... args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        return method.invoke(obj, args);
    }

}
