/**
 *  Copyright (C) 2000-2014 The Software Conservancy and Original Authors.
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
import org.chorusbdd.chorus.handlerconfig.ConfigurableHandler;
import org.chorusbdd.chorus.handlerconfig.loader.PropertiesFileAndDbConfigLoader;
import org.chorusbdd.chorus.handlers.utils.HandlerPatterns;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.processes.manager.ProcessManager;
import org.chorusbdd.chorus.remoting.manager.RemotingManager;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.stepinvoker.StepInvoker;
import org.chorusbdd.chorus.stepinvoker.StepInvokerProvider;
import org.chorusbdd.chorus.util.ChorusException;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
@Handler(value = "Remoting", scope = Scope.FEATURE)
@SuppressWarnings("UnusedDeclaration")
public class RemotingHandler implements ConfigurableHandler<RemotingConfig>, StepInvokerProvider {

    private static ChorusLog log = ChorusLogFactory.getLog(RemotingHandler.class);

    @ChorusResource("feature.dir")
    private File featureDir;

    @ChorusResource("feature.file")
    private File featureFile;

    @ChorusResource("feature.token")
    private FeatureToken featureToken;

    @ChorusResource("subsystem.processManager")
    private ProcessManager processManager;

    @ChorusResource("subsystem.remotingManager")
    private RemotingManager remotingManager;
    
    private Map<String, RemotingConfig> remotingConfigMap;

    private LocalProcessRemotingConfigs localProcessRemotingConfigs;

    private List<StepInvoker> remoteInvokersToUse = new LinkedList<StepInvoker>();

    /**
     * Will delegate calls to a remote Handler exported as a JMX MBean
     */
    @Step("(.*) (?:in|from) " + HandlerPatterns.processNamePattern + "$")
    public Object performActionInRemoteComponent(String action, String componentName) throws Exception {  
        RemotingConfig remotingConfig = getRemotingConfigForComponent(componentName);
        return remotingManager.performActionInRemoteComponent(action, remotingConfig.buildRemotingManagerConfig());
    }

    //A Directive which can be used to start one or more processes using the config name
    @Step("Remoting use " + HandlerPatterns.processNameListPattern)
    public void startProcessDirective(String processNameList) throws Exception {
        List<String> componentNames = HandlerPatterns.getProcessNames(processNameList);
        remoteInvokersToUse.clear();
        for ( String p : componentNames) {
            RemotingConfig remotingConfig = getRemotingConfigForComponent(p);
            remoteInvokersToUse.addAll(remotingManager.getStepInvokers(remotingConfig));
        }
    }

    @Initialize(scope = Scope.FEATURE)
    public void initialize() {
        PropertiesFileAndDbConfigLoader<RemotingConfig> configLoader = new PropertiesFileAndDbConfigLoader<RemotingConfig>(
            new RemotingConfigFactory(),
            "Remoting",
            "remoting",
            featureToken
        );
        remotingConfigMap = configLoader.loadConfigs();
        localProcessRemotingConfigs = new LocalProcessRemotingConfigs(processManager, remotingConfigMap);
    }

    /**
     * Called at end of scenario - closes all MBean connections
     * 
     * n.b. it is not (yet) possible to scope a remote connection to feature, so connections will all be closed on scenario end
     */
    @Destroy(scope = Scope.SCENARIO)
    public void destroy() {
        try {
            remotingManager.closeAllConnections(Scope.SCENARIO);
        } catch (Throwable t) {
            log.error("Failed while destroying jmx remoting manager");   
        }
    }

    private RemotingConfig getRemotingConfigForComponent(String name) {
        RemotingConfig remotingConfig = remotingConfigMap.get(name);
        if (remotingConfig == null) {
            //perhaps this was a process started locally by process manager
            remotingConfig = localProcessRemotingConfigs.getConfigForProcessManagerProcess(name);
        }

        if (remotingConfig == null) {
            throw new ChorusException("Failed to find remoting configuration for component: " + name);
        }
        return remotingConfig;
    }

    public void addConfiguration(RemotingConfig handlerConfig) {
        remotingConfigMap.put(handlerConfig.getConfigName(), handlerConfig);
    }

    /**
     * List of remote invokers for components remoting handler is currently 'using'
     */
    public List<StepInvoker> getStepInvokers() {
        return remoteInvokersToUse;
    }
}
