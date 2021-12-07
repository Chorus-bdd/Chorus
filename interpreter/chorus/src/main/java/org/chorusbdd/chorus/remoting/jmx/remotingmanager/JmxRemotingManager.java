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

import org.chorusbdd.chorus.annotations.ExecutionPriority;
import org.chorusbdd.chorus.annotations.Scope;
import org.chorusbdd.chorus.executionlistener.ExecutionListener;
import org.chorusbdd.chorus.executionlistener.ExecutionListenerAdapter;
import org.chorusbdd.chorus.handlerconfig.ConfigurableManager;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.remoting.jmx.serialization.JmxInvokerResult;
import org.chorusbdd.chorus.remoting.manager.RemotingConfigBean;
import org.chorusbdd.chorus.remoting.manager.RemotingManager;
import org.chorusbdd.chorus.remoting.manager.RemotingManagerConfig;
import org.chorusbdd.chorus.results.ExecutionToken;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.results.ScenarioToken;
import org.chorusbdd.chorus.stepinvoker.StepInvoker;

import java.util.*;

/**
 * Created by nick on 30/08/2014.
 */
public class JmxRemotingManager extends ConfigurableManager<RemotingConfigBean> implements RemotingManager {

    public static final String REMOTING_PROTOCOL = "jmx";

    private ChorusLog log = ChorusLogFactory.getLog(JmxRemotingManager.class);

    /**
     * Map: configName -> proxy
     */
    private final Map<String, ChorusHandlerJmxProxy> proxies = new HashMap<>();
    
    private final List<RemotingManagerConfig> remotingConfigs = new LinkedList<>();

    private Map<RemotingManagerConfig, List<StepInvoker>> remoteInvokersToUse = new HashMap<>();

    public JmxRemotingManager() {
        super(RemotingConfigBean.class);
    }

    @Override
    public void connect(String configName, Properties remotingProperties) {
        RemotingManagerConfig remotingConfig = getConfig(configName, remotingProperties, "remoting");

        ChorusHandlerJmxProxy proxy = getProxyForComponent(remotingConfig.getConfigName(), remotingConfig);
        List<StepInvoker> invokers = getRemoteStepInvokers(proxy);
        remoteInvokersToUse.put(remotingConfig, invokers);
    }

    @Override
    public List<StepInvoker> getStepInvokers() {
        List<StepInvoker> invokers = new LinkedList<>();
        for ( List<StepInvoker> l : remoteInvokersToUse.values()) {
            invokers.addAll(l);
        }
        return invokers;
    }

    private List<StepInvoker> getRemoteStepInvokers(ChorusHandlerJmxProxy proxy) {
        List<StepInvoker> invokers = new ArrayList<>();
        for (JmxInvokerResult r : proxy.getStepMetadata()) {
            StepInvoker invoker = RemoteStepInvoker.createRemoteStepInvoker(r, proxy);
            invokers.add(invoker);
        }
        return invokers;
    }

    private ChorusHandlerJmxProxy getProxyForComponent(String componentName, RemotingManagerConfig remotingConfig) {
        ChorusHandlerJmxProxy proxy = proxies.get(componentName);
        if ( proxy == null) {
            proxy = new ChorusHandlerJmxProxy(
                componentName,
                remotingConfig.getHost(),
                remotingConfig.getPort(),
                remotingConfig.getUserName(),
                remotingConfig.getPassword(),
                remotingConfig.getConnectionAttempts(),
                remotingConfig.getConnectionAttemptMillis()
            );
            proxies.put(componentName, proxy);
            remotingConfigs.add(remotingConfig);
            log.debug("Opened JMX connection to: " + componentName);
        }
        return proxy;
    }

    private void closeConnection(RemotingManagerConfig c) {
        ChorusHandlerJmxProxy proxy = proxies.remove(c.getConfigName());
        if ( proxy != null) {
            proxy.destroy();
            log.debug("Closed JMX connection to: " + c.getConfigName());
        }
        remotingConfigs.remove(c);
        remoteInvokersToUse.remove(c);
    }

    /**
     * Close all connections
     */
    public void closeAllConnections() {
        for(RemotingManagerConfig c : new ArrayList<>(remotingConfigs)) {
            closeConnection(c);
        }
    }


    @Override
    public ExecutionListener getExecutionListener() {
        return new RemotingManagerExecutionListener();
    }

    @ExecutionPriority(ExecutionPriority.REMOTING_MANAGER_PRIORITY)
    private class RemotingManagerExecutionListener extends ExecutionListenerAdapter {


        public void featureCompleted(ExecutionToken testExecutionToken, FeatureToken feature) {
            try {
                closeAllConnections();
            } catch (Throwable t) {
                log.error("Failed during destroyFeature jmx remoting manager", t);
            }
        }

        public void scenarioCompleted(ExecutionToken testExecutionToken, ScenarioToken scenario) {
            try {
                disposeForScope(Scope.SCENARIO);
            } catch (Throwable t) {
                log.error("Failed during destroyScenario() jmx remoting manager", t);
            }
        }

        private void disposeForScope(Scope scope) {

            for ( RemotingManagerConfig c : new ArrayList<>(remotingConfigs)) {
                if ( c.getScope() == scope) {
                    closeConnection(c);
                }
            }
        }
    }
}
