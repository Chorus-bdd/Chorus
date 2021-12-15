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
package org.chorusbdd.chorus.interpreter.startup;

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.util.ChorusException;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 16/05/12
 * Time: 17:36
 * To change this template use File | Settings | File Templates.
 */
public class DynamicProxyMBeanCreator {

    private ChorusLog log = ChorusLogFactory.getLog(DynamicProxyMBeanCreator.class);

    private JMXConnector jmxConnector;
    protected MBeanServerConnection mBeanServerConnection;
    private final String host;
    private final int jmxPort;

    public DynamicProxyMBeanCreator(String host, int jmxPort) {
        this.host = host;
        this.jmxPort = jmxPort;
    }

    public MBeanServerConnection connect() {
        MBeanServerConnection result;
        try {
            String serviceURL = String.format("service:jmx:rmi:///jndi/rmi://%s:%d/jmxrmi", host, jmxPort);
            jmxConnector = JMXConnectorFactory.connect(new JMXServiceURL(serviceURL), null);
            log.debug("Connecting to JMX service URL: " + serviceURL);

            result = jmxConnector.getMBeanServerConnection();

        } catch (Exception e) {
            String msg = String.format("Failed to connect to mBean server on (%s:%s)", host, jmxPort);
            if ( log.isDebugEnabled()) {
                log.debug(msg, e);
            } else {
                log.warn(msg);
            }
            throw new ChorusException(msg, e);
        }
        mBeanServerConnection = result;
        return result;
    }

    public <T> T createMBeanProxy(final String mxbeanName, Class<T> mxbeanInterface) throws IOException {

        final InvocationHandler handler = new InvocationHandler() {

            public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
                Class<?>[] paramTypes = method.getParameterTypes();
                if (method.getName().equals("equals") && paramTypes.length == 1 && paramTypes[0] == Object.class) {
                    //this is the equals method being called on the proxy, we need to handle it locally
                    return args[0] == proxy;
                } else {
                    return mBeanServerConnection.invoke(new ObjectName(mxbeanName), method.getName(), args, getClassNameArray(method.getParameterTypes()));
                }
            }

            private String[] getClassNameArray(Class[] args) {
                String[] classNames = new String[args.length];
                int pos=0;
                for ( Class a : args) {
                    classNames[pos++] = a.getName();
                }
                return classNames;
            }
        };
        return (T) Proxy.newProxyInstance(mxbeanInterface.getClassLoader(), new Class[]{mxbeanInterface}, handler);
    }

    //@TODO arrange to close the connection? At what point?
    public void dispose() {
        try {
            jmxConnector.close();
        } catch (IOException e) {
            //safe to ignore this exception - may get here if server process dies before JMX connection is destroyed
        }
    }

}
