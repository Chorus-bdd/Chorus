/**
 *  Copyright (C) 2000-2012 The Software Conservancy and Original Authors.
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
