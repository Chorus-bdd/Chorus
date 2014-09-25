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

import org.chorusbdd.chorus.annotations.*;
import org.chorusbdd.chorus.handlerconfig.loader.JDBCConfigLoader;
import org.chorusbdd.chorus.handlerconfig.loader.PropertiesConfigLoader;
import org.chorusbdd.chorus.processes.processmanager.ProcessInfo;
import org.chorusbdd.chorus.processes.processmanager.ProcessManager;
import org.chorusbdd.chorus.remoting.jmx.remotingmanager.*;
import org.chorusbdd.chorus.remoting.manager.RemotingManager;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.util.ChorusException;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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

    private Map<String, RemotingConfig> remotingConfigMap;
    
    private RemotingManager jmxRemotingManager = new JmxRemotingManager();

    // If set, will cause the mBean metadata to be loaded using JDBC properties in the named properties file
    public static final String REMOTING_HANDLER_DB_PROPERTIES = "org.chorusbdd.chorus.remoting.db";

    @Initialize(scope = Scope.SCENARIO)
    public void initialize() {
        loadRemotingConfigs();    
    }
    
    /**
     * Will delegate calls to a remote Handler exported as a JMX MBean
     */
    @Step("(.*) (?:in|from) ([a-zA-Z0-9_-]+)$")
    public Object performActionInRemoteComponent(String action, String componentName) throws Exception {  
        RemotingConfig remotingConfig = getRemotingConfigForComponent(componentName);
        return jmxRemotingManager.performActionInRemoteComponent(action, componentName, remotingConfig.buildRemotingInfo());
    }

    /**
     * Called at end of scenario - closes all MBean connections
     */
    @Destroy
    public void destroy() {
        try {
            jmxRemotingManager.destroy();
        } catch (Throwable t) {
            log.error("Failed while destroying jmx remoting manager");   
        }
    }


    private RemotingConfig getRemotingConfigForComponent(String name) {
        RemotingConfig remotingConfig = remotingConfigMap.get(name);
        if (remotingConfig == null) {
            //perhaps this was a process started locally by process manager
            remotingConfig = getConfigForProcessManagerProcess(name);
        }

        if (remotingConfig == null) {
            throw new ChorusException("Failed to find remoting configuration for component: " + name);
        }
        return remotingConfig;
    }

    /**
     * If processName was a process started by ProcessesHandler/ProcessManager, then we may be able to find the remoting setup
     * from the process config
     */
    private RemotingConfig getConfigForProcessManagerProcess(String processName) {
        RemotingConfig result = null;
        ProcessInfo processInfo = ProcessManager.getInstance().getProcessInfo(processName);
        if ( processInfo != null && processInfo.isRemotingConfigDefined() ) {
            result = getConfigForLocalProcess(processInfo);
        }
        return result;
    }

    /**
     * Find the process config name on which the running process was based
     * (multiple processes may be started under alias names generating several ProcessInfo from the same template config)
     *
     * is there a matching remoting config with that config name? If there is, then use that
     * otherwise take defaults by creating a new remoting config
     */
    private RemotingConfig getConfigForLocalProcess(ProcessInfo processInfo) {
        String processConfigName = processInfo.getProcessConfigName();

        RemotingConfig remotingConfig = remotingConfigMap.get(processConfigName);
        if (remotingConfig == null) {
            remotingConfig = new RemotingConfig();
            remotingConfig.setHost("localhost");
            remotingConfig.setPort(processInfo.getJmxPort());
        }
        return remotingConfig;
    }

    /**
     * @throws Exception
     */
    protected void loadRemotingConfigs() {
        //check to see if the system property has been set to specify a DB to load the configuration from
        String mBeansDb = System.getProperty(REMOTING_HANDLER_DB_PROPERTIES);

        //if the db system property has been set then use it
        if (mBeansDb != null) {
            loadRemotingConfigsFromDb(mBeansDb);
        } else {
            PropertiesConfigLoader<RemotingConfig> l = new PropertiesConfigLoader<RemotingConfig>(
                new RemotingConfigFactory(),
                "Remoting",
                "remoting",
                featureToken,
                featureDir,
                featureFile
            );
            remotingConfigMap = l.loadConfigs();
        }
    }

    private void loadRemotingConfigsFromDb(String jdbcPropertiesFilePath) {
        //use the file path to load the jdbc connection properties
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(jdbcPropertiesFilePath);
            Properties p = new Properties();
            p.load(fis);
            loadRemotingConfigsFromDb(p);
        } catch (IOException ioe) {
            throw new ChorusException("Failed to load remoting db properties", ioe);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    throw new ChorusException("Failed to close file input stream while loading remoting db properties",e);    
                }
            }
        }
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
        remotingConfigMap = new JDBCConfigLoader(p, "Remoting", new RemotingConfigFactory(), featureToken, featureDir, featureFile).loadConfigs();
    }
}
