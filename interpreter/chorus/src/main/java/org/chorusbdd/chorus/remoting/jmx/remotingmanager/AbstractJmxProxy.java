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
package org.chorusbdd.chorus.remoting.jmx.remotingmanager;

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.util.ChorusException;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Simple abstraction for accessing JMX MBeans.
 * Created by: Steve Neal
 * Date: 12/10/11
 */
public class AbstractJmxProxy {

    private ChorusLog log = ChorusLogFactory.getLog(AbstractJmxProxy.class);

    private JMXConnector jmxConnector;
    protected MBeanServerConnection mBeanServerConnection;
    protected ObjectName objectName;

    /**
     * Connect to the remote MBean
     *  @param host      the host to connect to
     * @param jmxPort   the JMX server port
     * @param mBeanName must be formatted according to the MBean spec
     * @param userName user name if connection requires authentication, may be null if no authentication required
     * @param password password if connection requires authentication, may be null                
     * @param connectionAttempts number of times to try to connect before giving up
     * @param millisBetweenRetries how log to wait between each connection attempt
     */
    public AbstractJmxProxy(String host, int jmxPort, String mBeanName, String userName, String password, int connectionAttempts, long millisBetweenRetries) {
        
        Map environment = createEnvironmentMap(userName, password);
        
        Exception connectException = null;
        int attempt = 1;
        try {
            String serviceURL = String.format("service:jmx:rmi:///jndi/rmi://%s:%d/jmxrmi", host, jmxPort);
            log.debug("Connecting to JMX service URL: " + serviceURL);

            while( jmxConnector == null && shouldAttemptConnection(connectionAttempts, attempt)) {
                attempt++;
                connectException = null;
                try {
                    jmxConnector = JMXConnectorFactory.connect(new JMXServiceURL(serviceURL), environment);
                } catch (Exception e) {
                    connectException = e;
                }
                if ( jmxConnector == null) {
                    log.debug("Failed to connect to service at " + serviceURL + " on attempt " +
                            attempt + " of " + connectionAttempts);
                    if ( shouldAttemptConnection(connectionAttempts, attempt)) {
                        log.debug("Will attempt another connection in " + millisBetweenRetries + " millis");
                        Thread.sleep(millisBetweenRetries);
                    }
                }
            }

            if ( jmxConnector == null ) {
                throw new Exception("Failed to connect to JMX service at " + serviceURL, connectException);
            }

            mBeanServerConnection = jmxConnector.getMBeanServerConnection();
        } catch (Exception e) {
            String msg = String.format("Failed to connect to mBean server on (%s:%s)", host, jmxPort);
            log.error(msg);
            throw new ChorusException(msg, connectException == null ? e : connectException);
        }

        try {
            this.objectName = new ObjectName(mBeanName);
            boolean found = false;
            while( ! found && shouldAttemptConnection(connectionAttempts, attempt)) {
                attempt++;
                Set<ObjectName> containsOne = mBeanServerConnection.queryNames(null, this.objectName);
                found = !containsOne.isEmpty();
                if ( ! found ) {
                    log.debug("No JMX Exporter MBean found on connection attempt " + attempt + " of " + connectionAttempts);
                    if ( shouldAttemptConnection(connectionAttempts, attempt)) {
                        log.debug("Will attempt to connect again in " + millisBetweenRetries + " millis");
                        Thread.sleep(millisBetweenRetries);
                    }
                }
            }

            if (found) {
                log.debug("Found MBean: " + mBeanName);
            } else {
                String msg = String.format("There is no MBean on server (%s:%d) with name (%s)", host, jmxPort, mBeanName);
                log.error(msg);
                throw new ChorusException(msg);
            }
        } catch (Exception e) {
            String msg = String.format("Failed to lookup mBean with name (%s) on server (%s:%s)", mBeanName, host, jmxPort);
            log.error(msg);
            throw new ChorusException(msg, e);
        }
    }

    /**
     * @return a Map containing environment properties if required, or null
     */
    private Map createEnvironmentMap(String userName, String password) {
        Map result = null;
        if ( userName != null && password != null) {
            result = new HashMap();
            result.put(JMXConnector.CREDENTIALS, new String[] { userName, password });
        }
        return result;
    }

    private boolean shouldAttemptConnection(int connectionAttempts, int attempt) {
        return attempt <= connectionAttempts;
    }

    public Object getAttribute(String name) {
        try {
            //call the remote method
            return mBeanServerConnection.getAttribute(objectName, name);
        } catch (Exception e) {
            throw new ChorusException("Failed to call MBean method", e);
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
