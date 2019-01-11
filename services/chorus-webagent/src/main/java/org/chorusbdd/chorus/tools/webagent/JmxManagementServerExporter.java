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
package org.chorusbdd.chorus.tools.webagent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.rmi.registry.LocateRegistry;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * User: nick
 * Date: 24/12/12
 * Time: 14:09
 *
 * Create and export a jmx management server with unencrypted sockets connection
 *
 * See http://docs.oracle.com/javase/6/docs/technotes/guides/management/agent.html
 */
public class JmxManagementServerExporter {

    private static final Log log = LogFactory.getLog(JmxManagementServerExporter.class);

    private static Set<Integer> registriesCreated = Collections.synchronizedSet(new HashSet<Integer>());
    private int port;
    private boolean usePlatformMBeanServer;
    private JMXConnectorServer jmxConnectorServer;
    private MBeanServer mBeanServer;

    public JmxManagementServerExporter(int port, boolean usePlatformMBeanServer) {
        this.port = port;
        this.usePlatformMBeanServer = usePlatformMBeanServer;
    }

    public void startServer() throws Exception {

        // Ensure cryptographically strong random number generator used
        // to choose the object number - see java.rmi.server.ObjID
        //
        //System.setProperty("java.rmi.server.randomIDs", "true");

        // Start an RMI registry on port 3000.
        //
        if ( ! registriesCreated.contains(port)) {
            log.info("Creating RMI registry on port " + port);
            LocateRegistry.createRegistry(port);
            registriesCreated.add(port);
        } else {
            //there's no way to shut it dnwn? So if we run a sequence of tests we clean up by unexporting the
            //listener object, and have to reuse the registry instance
            log.info("RMI registry was already running on port " + port);
        }

        // Retrieve the PlatformMBeanServer.
        //
        log.info(usePlatformMBeanServer ? "Using Platform MBean Server" : "Creating the MBean server");
        mBeanServer = usePlatformMBeanServer ?
            ManagementFactory.getPlatformMBeanServer() :
            MBeanServerFactory.createMBeanServer();

        // Environment map.
        //
        log.info("Initialize the environment map");
        HashMap<String,Object> env = new HashMap<String,Object>();

        // Provide SSL-based RMI socket factories.
        //
        // The protocol and cipher suites to be enabled will be the ones
        // defined by the default JSSE implementation and only server
        // authentication will be required.
        //
        //SslRMIClientSocketFactory csf = new SslRMIClientSocketFactory();
        //SslRMIServerSocketFactory ssf = new SslRMIServerSocketFactory();
        //env.put(RMIConnectorServer.RMI_CLIENT_SOCKET_FACTORY_ATTRIBUTE, csf);
        //env.put(RMIConnectorServer.RMI_SERVER_SOCKET_FACTORY_ATTRIBUTE, ssf);

        // Provide the password file used by the connector server to
        // perform user authentication. The password file is a properties
        // based text file specifying username/password pairs.
        //
        //env.put("jmx.remote.x.password.file", "password.properties");

        // Provide the access level file used by the connector server to
        // perform user authorization. The access level file is a properties
        // based text file specifying username/access level pairs where
        // access level is either "readonly" or "readwrite" access to the
        // MBeanServer operations.
        //
        //env.put("jmx.remote.x.access.file", "access.properties");

        // Create an RMI connector server.
        //
        // As specified in the JMXServiceURL the RMIServer stub will be
        // registered in the RMI registry running in the local host on
        // port 3000 with the name "jmxrmi". This is the same name the
        // out-of-the-box management agent uses to register the RMIServer
        // stub too.
        //
        log.info("Create an RMI connector server");
        JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://:" + port + "/jmxrmi");
        jmxConnectorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, env, mBeanServer);

        // Start the RMI connector server.
        //
        log.info("Start the JMX connector server on port " + port);
        jmxConnectorServer.start();
    }

    public void stopServer() throws IOException {
        log.info("Stopping the JMX connector server on port " + port);
       jmxConnectorServer.stop();
    }

    public MBeanServer getmBeanServer() {
        return mBeanServer;
    }
}
