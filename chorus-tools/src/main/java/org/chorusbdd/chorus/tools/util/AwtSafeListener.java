package org.chorusbdd.chorus.tools.util;

import javax.swing.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created with IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 15/05/12
 * Time: 16:24

 * Create a proxy implementation of a listener interface which propagates event handling asynchronously to a Swing
 * listener. This ensures that all events handled are passed to the Swing AWT thread to call the Swing interface
 * implementation.
 */
public class AwtSafeListener {

    /**
     * @return return an AWT safe listener which wraps a swing listener and ensures events are propagated on the awt thread
     *
     * this requires events and all their parameters to be immutable, or inconsistent state problems may occur if
     * events are subsequently altered.
     *
     * In addition, due to the asynchronous calling of the AWT listener it only works with interfaces where the return
     * type of all methods is null.
     */
    public static <E> E getAwtInvokeLaterListener(final E listener, final Class<E> listenerClass) {

        final InvocationHandler handler = new InvocationHandler() {

            public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
                Class<?>[] paramTypes = method.getParameterTypes();
                if (method.getName().equals("equals") && paramTypes.length == 1 && paramTypes[0] == Object.class) {
                    //this is the equals method being called on the proxy, we need to handle it locally
                    return args[0] == proxy;
                } else {
                    SwingUtilities.invokeLater(
                        new Runnable() {
                            public void run() {
                                try {
                                    method.invoke(listener, args);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    );
                    return null;
                }
            }
        };

        return (E) Proxy.newProxyInstance(listenerClass.getClassLoader(), new Class[]{listenerClass}, handler);
    }

    /**
     * @return return an AWT safe listener which wraps a swing listener and ensures events are propagated on the awt thread
     *
     * This requires events and all their parameters to be immutable, or inconsistent state problems may occur if
     * events are subsequently altered.
     */
    public static <E> E getAwtInvokeAndWaitListener(final E listener, final Class<E> listenerClass) {

        final InvocationHandler handler = new InvocationHandler() {

            public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
                Class<?>[] paramTypes = method.getParameterTypes();
                if (method.getName().equals("equals") && paramTypes.length == 1 && paramTypes[0] == Object.class) {
                    //this is the equals method being called on the proxy, we need to handle it locally
                    return args[0] == proxy;
                } else {

                    class MethodRunnable implements Runnable {
                        volatile Object result;
                        public void run() {
                            try {
                                result = method.invoke(listener, args);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    MethodRunnable r = new MethodRunnable();
                    SwingUtilities.invokeAndWait(r);
                    return r.result;
                }
            }
        };

        return (E) Proxy.newProxyInstance(listenerClass.getClassLoader(), new Class[]{listenerClass}, handler);
    }
}
