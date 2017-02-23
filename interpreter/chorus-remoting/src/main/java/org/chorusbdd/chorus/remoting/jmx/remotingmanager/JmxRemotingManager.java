/**
 *  Copyright (C) 2000-2015 The Software Conservancy and Original Authors.
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
package org.chorusbdd.chorus.remoting.jmx.remotingmanager;

import org.chorusbdd.chorus.annotations.Scope;
import org.chorusbdd.chorus.executionlistener.ExecutionListener;
import org.chorusbdd.chorus.executionlistener.ExecutionListenerAdapter;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.remoting.jmx.serialization.JmxInvokerResult;
import org.chorusbdd.chorus.remoting.manager.RemotingConfigBeanFactory;
import org.chorusbdd.chorus.remoting.manager.RemotingConfigBeanValidator;
import org.chorusbdd.chorus.remoting.manager.RemotingManager;
import org.chorusbdd.chorus.remoting.manager.RemotingManagerConfig;
import org.chorusbdd.chorus.results.ExecutionToken;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.results.ScenarioToken;
import org.chorusbdd.chorus.stepinvoker.StepInvoker;
import org.chorusbdd.chorus.stepinvoker.StepMatchResult;
import org.chorusbdd.chorus.stepinvoker.StepMatcher;
import org.chorusbdd.chorus.stepinvoker.StepPendingException;
import org.chorusbdd.chorus.util.ChorusException;

import java.util.*;

import static org.chorusbdd.chorus.util.assertion.ChorusAssert.fail;

/**
 * Created by nick on 30/08/2014.
 */
public class JmxRemotingManager implements RemotingManager {

    public static final String REMOTING_PROTOCOL = "jmx";

    private ChorusLog log = ChorusLogFactory.getLog(JmxRemotingManager.class);

    /**
     * Map: configName -> proxy
     */
    private final Map<String, ChorusHandlerJmxProxy> proxies = new HashMap<>();

    private final RemotingConfigBeanValidator configValidator = new RemotingConfigBeanValidator();

    private final List<RemotingManagerConfig> remotingConfigs = new LinkedList<>();

    private Map<RemotingManagerConfig, List<StepInvoker>> remoteInvokersToUse = new HashMap<>();
    private final RemotingConfigBeanFactory remotingConfigBeanFactory = new RemotingConfigBeanFactory();

    /**
     *
     * Perform an action on a specific remote component
     * Here we try to find a matching step method on a remote handler to delegate the call to
     * We need this to support the old-style steps suffixed with 'in ${componentName}'
     *
     * The new way of doing remoting returns all the available remote steps as List<StepInvoker>
     * so they can be matched against the step action by the interpreter alongside any local steps
     * This new mechanism is used when the directive #! Remoting is used
     *
     */
    public Object performActionInRemoteComponent(String configName, Properties remotingProperties, String action) {

        RemotingManagerConfig remotingConfig = buildRemotingConfig(configName, remotingProperties);

        String componentName = remotingConfig.getConfigName();

        checkConfig(remotingConfig);

        ChorusHandlerJmxProxy proxy = getProxyForComponent(componentName, remotingConfig);

        List<StepInvoker> invokers = getRemoteStepInvokers(proxy);

        StepMatcher stepMatcher = new StepMatcher(invokers, action);
        stepMatcher.findStepMethod();

        Object result;
        StepMatchResult stepMatchResult = stepMatcher.getStepMatchResult();
        switch(stepMatchResult) {
            case STEP_FOUND:
                result = processRemoteMethod(stepMatcher.getFoundStepInvoker(), stepMatcher.getInvokerArgs());
                break;
            case STEP_NOT_FOUND:
                String message = String.format("There is no step handler available for action (%s) on component (%s)", action, componentName);
                log.error(message);
                throw new RemoteStepNotFoundException(action, componentName);
            case DUPLICATE_MATCH_ERROR:
                throw stepMatcher.getMatchException();
            default:
                throw new ChorusException("Unsupported StepMatchResult");
        }
        return result;
    }

    @Override
    public void connect(String configName, Properties remotingProperties) {
        RemotingManagerConfig remotingConfig = buildRemotingConfig(configName, remotingProperties);

        checkConfig(remotingConfig);
        ChorusHandlerJmxProxy proxy = getProxyForComponent(remotingConfig.getConfigName(), remotingConfig);
        List<StepInvoker> invokers = getRemoteStepInvokers(proxy);
        remoteInvokersToUse.put(remotingConfig, invokers);
    }

    private RemotingManagerConfig buildRemotingConfig(String configName, Properties remotingProperties) {
        return remotingConfigBeanFactory.createConfig(remotingProperties, configName).build();
    }

    @Override
    public List<StepInvoker> getStepInvokers() {
        List<StepInvoker> invokers = new LinkedList<>();
        for ( List<StepInvoker> l : remoteInvokersToUse.values()) {
            invokers.addAll(l);
        }
        return invokers;
    }

    private void checkConfig(RemotingManagerConfig remotingConfig) {
        boolean validConfig = configValidator.isValid(remotingConfig);
        if ( ! validConfig) {
            log.warn(configValidator.getErrorDescription());
            fail("Remoting config must be valid for " + remotingConfig.getConfigName());
        }
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
            proxy = new ChorusHandlerJmxProxy(componentName, remotingConfig.getHost(), remotingConfig.getPort(), remotingConfig.getConnectionAttempts(), remotingConfig.getConnectionAttemptMillis());
            proxies.put(componentName, proxy);
            remotingConfigs.add(remotingConfig);
            log.debug("Opened JMX connection to: " + componentName);
        }
        return proxy;
    }

    private Object processRemoteMethod(StepInvoker remoteStepInvoker, List<String> args) {
        Object result;
        if (remoteStepInvoker.isPending()) {
            throw new StepPendingException(remoteStepInvoker.getPendingMessage());
        }

        try {
            result = remoteStepInvoker.invoke(args);

        //let any runtime exceptions propagate
        } catch (ReflectiveOperationException e) {
            throw new ChorusException(e);
        }
        return result;
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
