/* Copyright (C) 2000-2011 The Software Conservancy as Trustee.
 * All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 *
 * Nothing in this notice shall be deemed to grant any rights to trademarks,
 * copyrights, patents, trade secrets or any other intellectual property of the
 * licensor or any contributor except as expressly stated herein. No patent
 * license is granted separate from the Software, for code that you delete from
 * the Software, or for combinations of the Software with other software or
 * hardware.
 */

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
import java.util.Set;

/**
 * Simple abstraction for accessing JMX MBeans.
 * <p/>
 * Created by: Steve Neal
 * Date: 12/10/11
 */
public class DynamicJmxProxy {

    private Log log = LogFactory.getLog(getClass());

    private JMXConnector jmxConnector;
    protected MBeanServerConnection mBeanServerConnection;
    protected ObjectName objectName;

    /**
     * Connect to the remote MBean
     *
     * @param host      the host to connect to
     * @param jmxPort   the JMX server port
     * @param mBeanName must be formatted according to the MBean spec
     * @throws ChorusRemotingException if not possible to connect
     */
    public DynamicJmxProxy(String host, int jmxPort, String mBeanName) throws ChorusRemotingException {
        try {
            String serviceURL = String.format("service:jmx:rmi:///jndi/rmi://%s:%d/jmxrmi", host, jmxPort);
            jmxConnector = JMXConnectorFactory.connect(new JMXServiceURL(serviceURL), null);
            log.debug("Connecting to JMX service URL: " + serviceURL);

            mBeanServerConnection = jmxConnector.getMBeanServerConnection();
        } catch (Exception e) {
            String msg = String.format("Failed to connect to mBean server on (%s:%s)", host, jmxPort);
            log.error(msg);
            throw new ChorusRemotingException(msg, e);
        }

        try {
            this.objectName = new ObjectName(mBeanName);
            Set<ObjectName> containsOne = mBeanServerConnection.queryNames(null, this.objectName);
            if (containsOne.size() == 1) {
                log.debug("Found MBean: " + mBeanName);
            } else {
                String msg = String.format("There is no MBean on server (%s:%d) with name (%s)", host, jmxPort, mBeanName);
                log.error(msg);
                throw new ChorusRemotingException(msg);
            }
        } catch (Exception e) {
            String msg = String.format("Failed to lookup mBean with name (%s) on server (%s:%s)", mBeanName, host, jmxPort);
            log.error(msg);
            throw new ChorusRemotingException(msg, e);
        }
    }

    public Object getAttribute(String name) throws ChorusRemotingException {
        try {
            //call the remote method
            return mBeanServerConnection.getAttribute(objectName, name);
        } catch (Exception e) {
            throw new ChorusRemotingException("Failed to call MBean method", e);
        }
    }

    /**
     * Makes the call on the remote bean using reflection to determine the signature of the method to invoke
     *
     * @throws ChorusRemotingException if not possible to complete the call
     */
    public Object invoke(String methodName, Object... params) throws ChorusRemotingException {
        try {
            //dynamically figure out the method signature
            String[] signature = new String[params.length];
            for (int i = 0; i < signature.length; i++) {
                signature[i] = params[i].getClass().getName();
            }
            //call the remote method
            return mBeanServerConnection.invoke(objectName, methodName, params, signature);
        } catch (Exception e) {
            throw new ChorusRemotingException("Failed to call MBean method", e);
        }
    }


    /**
     * Closes the connection to the MBean server
     */
    public void destroy() {
        try {
            jmxConnector.close();
        } catch (IOException e) {
            //safe to ignore this exception - may get here if server process dies before JMX connection is destroyed
        }
    }
}
