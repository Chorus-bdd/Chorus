package org.chorusbdd.chorus.remoting.jmx;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chorusbdd.chorus.remoting.ChorusRemotingException;

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
public class DynamicMBeanProxyHandler {

    private Log log = LogFactory.getLog(getClass());

    private JMXConnector jmxConnector;
    protected MBeanServerConnection mBeanServerConnection;
    private final String host;
    private final int jmxPort;

    public DynamicMBeanProxyHandler(String host, int jmxPort) {
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
            log.error(msg);
            throw new ChorusRemotingException(msg, e);
        }
        mBeanServerConnection = result;
        return result;
    }

    public <T> T newMBeanProxy(final String mxbeanName, Class<T> mxbeanInterface) throws IOException {

        final InvocationHandler handler = new InvocationHandler() {

            public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
                Class<?>[] paramTypes = method.getParameterTypes();
                if (method.getName().equals("equals") && paramTypes.length == 1 && paramTypes[0] == Object.class) {
                    //this is the equals method being called on the proxy, we need to handle it locally
                    return args[0] == proxy;
                } else {
                    return mBeanServerConnection.invoke(new ObjectName(mxbeanName), method.getName(), args, getClassNameArray(args));
                }
            }

            private String[] getClassNameArray(Object[] args) {
                String[] classNames = new String[args.length];
                int pos=0;
                for ( Object a : args) {
                    classNames[pos++] = a.getClass().getName();
                }
                return classNames;
            }
        };
        return (T) Proxy.newProxyInstance(mxbeanInterface.getClassLoader(), new Class[]{mxbeanInterface}, handler);
    }

    public void dispose() {
        try {
            jmxConnector.close();
        } catch (IOException e) {
            //safe to ignore this exception - may get here if server process dies before JMX connection is destroyed
        }
    }



}
