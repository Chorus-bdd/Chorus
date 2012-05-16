package org.chorusbdd.chorus.tools.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created with IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 16/05/12
 * Time: 09:53
 *
 * Create a proxy listener which propagates event calls to multiple child listeners
 */
public class CompositeListener {

    /**
     * @return return a proxy listener which propagates event calls to multiple child listeners
     */
    public static <E> E getCompositeListener(final Class<E> listenerClass, final E... childListeners) {

        final InvocationHandler handler = new InvocationHandler() {

            public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
                Class<?>[] paramTypes = method.getParameterTypes();
                if (method.getName().equals("equals") && paramTypes.length == 1 && paramTypes[0] == Object.class) {
                    //this is the equals method being called on the proxy, we need to handle it locally
                    return args[0] == proxy;
                } else {
                    for (E l : childListeners) {
                        method.invoke(l, args);
                    }
                }
                return null;
            }
        };

        return (E) Proxy.newProxyInstance(listenerClass.getClassLoader(), new Class[]{listenerClass}, handler);
    }
}
