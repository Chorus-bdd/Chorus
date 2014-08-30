/**
 *  Copyright (C) 2000-2013 The Software Conservancy and Original Authors.
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

import org.chorusbdd.chorus.annotations.ChorusResource;
import org.chorusbdd.chorus.annotations.Destroy;
import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.core.interpreter.StepPendingException;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.handlers.util.HandlerUtils;
import org.chorusbdd.chorus.handlers.util.config.loader.JDBCConfigLoader;
import org.chorusbdd.chorus.handlers.util.config.loader.PropertiesConfigLoader;
import org.chorusbdd.chorus.remoting.jmx.ChorusHandlerJmxProxy;
import org.chorusbdd.chorus.util.ChorusException;
import org.chorusbdd.chorus.util.ChorusRemotingException;
import org.chorusbdd.chorus.util.RegexpUtils;
import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;

import javax.management.RuntimeMBeanException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * This handler can be used to invoke steps on components running remotely across the network.
 * <p/>
 * The single Step method will match any regexp that ends with "(in|from) [mbean name]". 
 * 
 * In order to work, this handler must have metadata available for the mbean names that it will be connecting to. 
 * 
 * This metadata usually will be loaded from a chorus properties file see the Chorus wiki for more details of property file 
 * configuration and the various remoting properties.
 * 
 * An example configuration to connect to a component called 'MyRemoteComponent' using the jmx protocol is given below
 * This component is running on myserver.mydomain on port 18800 
 * 
 * remoting.MyRemoteComponent.connection=jmx:myserver.mydomain:18800
 *
 * It is also possible to load remoting config properties from the database. The database connection properties and the SQL used to load
 * the metadata are loaded from a properties file named in system property: -D org.chorusbdd.chorus.jmxexporter.db.properties=[file].

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

    //A remoting protocol is defined by each remoting configuration and so it's possible to have 
    //more than one type of remoting used in a scenario, if different remote connections use different protocols.
    //We need a RemotingManager to run the remote steps for each protocol supported
    private Map<String, RemotingManager> remotingManagerByProtocol = new HashMap<String, RemotingManager>();

    private Map<String, RemotingConfig> remotingConfigMap;

    // If set, will cause the mBean metadata to be loaded using JDBC properties in the named properties file
    public static final String REMOTING_HANDLER_DB_PROPERTIES = "org.chorusbdd.chorus.remoting.db";

    public RemotingHandler() {
        createRemotingManagers();
    }

    //This is done on creation, which is fine since at present RemotingHandler is scenario scoped and we do want
    //to create new remoting managers on the start of each scenario
    //Since none of the remoting handlers so far are heavyweight to create, there's no need for a lazy create here
    private void createRemotingManagers() {
        remotingManagerByProtocol.put("jmx", new JmxRemotingManager());
    }

    /**
     * Will delegate calls to a remote Handler exported as a JMX MBean
     */
    @Step("(.*) (?:in|from) ([a-zA-Z0-9_-]+)$")
    public Object performActionInRemoteComponent(String action, String componentName) throws Exception {
        RemotingConfig remotingConfig = getRemotingConfigs(componentName);
        if (remotingConfig == null) {
            throw new ChorusException("Failed to find MBean configuration for component: " + componentName);
        }
        
        RemotingManager remotingManager = getRemotingManager(remotingConfig.getProtocol());
        Object stepResult = remotingManager.performActionInRemoteComponent(action, componentName, remotingConfig);
        return stepResult;
    }

    private RemotingManager getRemotingManager(String protocol) {
        RemotingManager remotingManager = remotingManagerByProtocol.get(protocol);
        if ( remotingManager == null) {
            throw new ChorusException("Cannot process remote step for unsupported remoting protocol " + protocol);
        }
        return remotingManager;
    }

    /**
     * Called at end of scenario - closes all MBean connections
     */
    @Destroy
    public void destroy() {
        for ( Map.Entry<String,RemotingManager> m : remotingManagerByProtocol.entrySet()) {
            try {
                m.getValue().destroy();
            } catch (Throwable t) {
                log.error("Failed while destroying remoting manager for protocol " + m.getKey(), t);    
            }
        }
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
                "remoting",
                featureToken,
                featureDir,
                featureFile
            );
            remotingConfigMap = l.loadConfigs();
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
        remotingConfigMap = new JDBCConfigLoader(p, "Remoting", new RemotingConfigBuilder(), featureToken, featureDir, featureFile).loadConfigs();
    }
}
