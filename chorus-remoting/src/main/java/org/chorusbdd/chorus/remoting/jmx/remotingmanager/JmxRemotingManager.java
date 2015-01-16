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
package org.chorusbdd.chorus.remoting.jmx.remotingmanager;

import org.chorusbdd.chorus.annotations.Scope;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.remoting.jmx.serialization.JmxInvokerResult;
import org.chorusbdd.chorus.remoting.manager.RemotingConfigBeanValidator;
import org.chorusbdd.chorus.remoting.manager.RemotingManager;
import org.chorusbdd.chorus.remoting.manager.RemotingManagerConfig;
import org.chorusbdd.chorus.stepinvoker.*;
import org.chorusbdd.chorus.subsystem.SubsystemAdapter;
import org.chorusbdd.chorus.util.ChorusException;
import org.chorusbdd.chorus.util.assertion.ChorusAssert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nick on 30/08/2014.
 */
public class JmxRemotingManager extends SubsystemAdapter implements RemotingManager {

    public static final String REMOTING_PROTOCOL = "jmx";

    private static ChorusLog log = ChorusLogFactory.getLog(JmxRemotingManager.class);

    /**
     * Map: mBeanName -> proxy
     */
    private final Map<String, ChorusHandlerJmxProxy> proxies = new HashMap<String, ChorusHandlerJmxProxy>();

    private final RemotingConfigBeanValidator configValidator = new RemotingConfigBeanValidator();

    /**
     * Will delegate calls to a remote Handler exported as a JMX MBean
     */
    public Object performActionInRemoteComponent(String action, RemotingManagerConfig remotingConfig) {
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

    public List<StepInvoker> getStepInvokers(RemotingManagerConfig remotingConfig) {
        checkConfig(remotingConfig);
        ChorusHandlerJmxProxy proxy = getProxyForComponent(remotingConfig.getConfigName(), remotingConfig);
        return getRemoteStepInvokers(proxy);
    }
    
    private void checkConfig(RemotingManagerConfig remotingConfig) {
        boolean validConfig = configValidator.isValid(remotingConfig);
        ChorusAssert.assertTrue("Remoting config must be valid for " + remotingConfig.getConfigName(), validConfig);
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

    /**
     * Called at end of scenario - closes all MBean connections
     */
    public void closeAllConnections(Scope handlerScope) {
        for (Map.Entry<String, ChorusHandlerJmxProxy> entry : proxies.entrySet()) {
            String name = entry.getKey();
            ChorusHandlerJmxProxy jmxProxy = entry.getValue();
            jmxProxy.destroy();
            log.debug("Closed JMX connection to: " + name);
        }
        proxies.clear();
    }


}
