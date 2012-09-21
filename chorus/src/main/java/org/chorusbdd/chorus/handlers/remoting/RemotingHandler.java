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
package org.chorusbdd.chorus.handlers.remoting;

import org.chorusbdd.chorus.ChorusException;
import org.chorusbdd.chorus.annotations.ChorusResource;
import org.chorusbdd.chorus.annotations.Destroy;
import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.core.interpreter.StepPendingException;
import org.chorusbdd.chorus.core.interpreter.results.FeatureToken;
import org.chorusbdd.chorus.handlers.util.HandlerUtils;
import org.chorusbdd.chorus.handlers.util.PropertiesConfigLoader;
import org.chorusbdd.chorus.remoting.ChorusRemotingException;
import org.chorusbdd.chorus.remoting.jmx.ChorusHandlerJmxProxy;
import org.chorusbdd.chorus.util.RegexpUtils;
import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;

import javax.management.RuntimeMBeanException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * This handler can be used to make calls over RMI to JMX MBeans.
 * <p/>
 * The single Step method will match any regexp that ends with "on [mbean name]". In order to work, this handler must
 * have metadata available for the mbean names that it will be connecting to. This metadata will be loaded, by default
 * from a standard feature configuration file, each line formatted:mbean-name=host:port.
 * </p>
 * Alternatively, the properties file can be specified using a system property: -D org.chorusbdd.chorus.jmxexporter.mbean.properties
 * </p>
 * It is also possible to load the data from a database. The database connection properties and the SQL used to load
 * the metadata are loaded from a properties file named in system property: -D org.chorusbdd.chorus.jmxexporter.db.properties=[file].
 * <p/>
 * Created by: Steve Neal
 * Date: 12/10/11
 */
@Handler("Remoting")
@SuppressWarnings("UnusedDeclaration")
public class RemotingHandler {

    private static ChorusLog log = ChorusLogFactory.getLog(RemotingHandler.class);

    @ChorusResource("feature.dir")
    private File featureDir;

    @ChorusResource("feature.file")
    private File featureFile;

    @ChorusResource("feature.token")
    private FeatureToken featureToken;

    /**
     * Map: mBeanName -> proxy
     */
    private final Map<String, ChorusHandlerJmxProxy> proxies = new HashMap<String, ChorusHandlerJmxProxy>();

    private Map<String, RemotingConfig> remotingConfigMap;

    // System property values to override default properties loading behaviour

    // If set, will cause the mBean metadata to be loaded from properties in the named properties file
    public static final String REMOTING_HANDLER_MBEANS_PROPERTIES = "org.chorusbdd.chorus.remoting.properties";

    // If set, will cause the mBean metadata to be loaded using JDBC properties in the named properties file
    public static final String REMOTING_HANDLER_DB_PROPERTIES = "org.chorusbdd.chorus.remoting.db";

    /**
     * Will delegate calls to a remote Handler exported as a JMX MBean
     */
    @Step("(.*) in ([a-zA-Z0-9_-]+)$")
    public Object performActionInRemoteComponent(String action, String componentName) throws Exception {
        ChorusHandlerJmxProxy proxy = getProxyForComponent(componentName);
        Map<String, String[]> stepMetaData = proxy.getStepMetadata();

        //details of the selected method
        String methodUidToCall = null;
        Object[] methodArgsToPass = null;
        String methodUidToCallPendingMessage = null;

        for (Map.Entry<String, String[]> entry : stepMetaData.entrySet()) {
            String methodUid = entry.getKey();
            String regex = entry.getValue()[0];
            String pending = entry.getValue()[1];

            //identify the types in the methodUid
            String[] methodUidParts = methodUid.split("::");
            Class[] types = new Class[methodUidParts.length - 1];
            for (int i = 0; i < types.length; i++) {
                String typeName = methodUidParts[i + 1];
                try {
                    types[i] = HandlerUtils.forName(typeName);
                } catch (ClassNotFoundException e) {
                    log.error("Could not locate class for: " + typeName, e);
                }
            }

            //see if this method will do
            Object[] args = RegexpUtils.extractGroupsAndCheckMethodParams(regex, action, types);
            if (args != null) {
                if (methodUidToCall == null) {
                    methodUidToCall = methodUid;
                    methodUidToCallPendingMessage = pending;
                    methodArgsToPass = args;
                } else {
                    log.info(String.format("Ambiguous method (%s) found for step (%s) on (%s) will use first method found (%s)",
                            methodUid,
                            action,
                            componentName,
                            methodUidToCall));
                }
            }
        }

        if (methodUidToCall != null) {
            Object result;
            if (methodUidToCallPendingMessage != null) {
                throw new StepPendingException(methodUidToCallPendingMessage);
            }
            try {
                result = proxy.invokeStep(methodUidToCall, methodArgsToPass);
            } catch (RuntimeMBeanException mbe) {
                //here if an exception was thrown from the remote Step method
                RuntimeException targetException = mbe.getTargetException();
                if (targetException instanceof ChorusRemotingException) {
                    throw targetException;
                } else {
                    throw new ChorusRemotingException(targetException);
                }
            } catch (Exception e) {
                throw new ChorusRemotingException(e);
            }
            return result;
        } else {
            String message = String.format("There is no handler available for action (%s) on MBean (%s)", action, componentName);
            log.error(message);
            throw new RuntimeException(message);
        }
    }

    /**
     * Called at end of scenario - closes all MBean connections
     */
    @Destroy
    public void destroy() {
        for (Map.Entry<String, ChorusHandlerJmxProxy> entry : proxies.entrySet()) {
            String name = entry.getKey();
            ChorusHandlerJmxProxy jmxProxy = entry.getValue();
            jmxProxy.destroy();
            log.debug("Closed JMX connection to: " + name);
        }
    }

    private ChorusHandlerJmxProxy getProxyForComponent(String name) throws Exception {
        ChorusHandlerJmxProxy proxy = proxies.get(name);
        if (proxy == null) {
            RemotingConfig remotingConfig = getRemotingConfigs(name);
            if (remotingConfig == null) {
                throw new ChorusException("Failed to find MBean configuration for component: " + name);
            } else {
                proxy = new ChorusHandlerJmxProxy(remotingConfig.getHost(), remotingConfig.getPort(), remotingConfig.getConnectionRetryAttempts(), remotingConfig.getConnectionRetryMillis());
                proxies.put(name, proxy);
                log.debug("Opened JMX connection to: " + name);
            }
        }
        return proxy;
    }

    private RemotingConfig getRemotingConfigs(String componentName) throws Exception {
        if (remotingConfigMap == null) {
            loadRemotingConfigs();
        }
        return remotingConfigMap.get(componentName);
    }

    /**
     * @throws Exception
     */
    protected void loadRemotingConfigs() throws Exception {
        //check to see if the system property has been set to specify a DB to load the configuration from
        String mBeansDb = System.getProperty(REMOTING_HANDLER_DB_PROPERTIES);

        //if the db system property has been set then use it
        if (mBeansDb != null) {
            loadRemotingConfigsFromDb(mBeansDb);
        } else {
            PropertiesConfigLoader<RemotingConfig> l = new PropertiesConfigLoader<RemotingConfig>(
                new RemotingConfigBuilder(),
                "Remoting",
                "-remoting",
                featureToken,
                featureDir,
                featureFile
            );
            remotingConfigMap = l.loadRemotingConfigs();
        }
    }

    private void loadRemotingConfigsFromDb(String jdbcPropertiesFilePath) throws IOException {
        //use the file path to load the jdbc connection properties
        FileInputStream fis = new FileInputStream(jdbcPropertiesFilePath);
        Properties p = new Properties();
        p.load(fis);
        fis.close();
        loadRemotingConfigsFromDb(p);
    }

    /**
     * Can be used by subclasses to load the MBean configuration from a database. Properties are passed to perform a
     * query on a database, the results of which must return a row for each MBean with columns titled: mBeanName, host
     * and port.
     *
     * @param p the database connection properties, must include:
     * <ul>
     *     <li>jdbc.driver</li>
     *     <li>jdbc.url</li>
     *     <li>jdbc.user</li>
     *     <li>jdbc.password</li>
     *     <li>jdbc.sql</li>
     * </ul>
     */
    protected void loadRemotingConfigsFromDb(Properties p) {
        remotingConfigMap = new JDBCRemotingConfigLoader(p).loadRemotingConfigs();
    }
}
