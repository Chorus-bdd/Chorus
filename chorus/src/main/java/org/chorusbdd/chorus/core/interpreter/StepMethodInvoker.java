package org.chorusbdd.chorus.core.interpreter;

import java.lang.reflect.InvocationTargetException;

/**
 * User: nick
 * Date: 20/09/13
 * Time: 09:09
 */
public interface StepMethodInvoker {

    /**
     * Invoke the method
     */
    Object invoke(Object obj, Object... args) throws IllegalAccessException, InvocationTargetException;

    /**
     * @return the name of the method to invoke
     */
    String getName();
}
