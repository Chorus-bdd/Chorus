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
import org.chorusbdd.chorus.remoting.jmx.InvokerMapAdapter;
import org.chorusbdd.chorus.remoting.manager.RemotingConfigValidator;
import org.chorusbdd.chorus.remoting.manager.RemotingManager;
import org.chorusbdd.chorus.remoting.manager.RemotingManagerConfig;
import org.chorusbdd.chorus.stepinvoker.StepFinder;
import org.chorusbdd.chorus.stepinvoker.StepInvoker;
import org.chorusbdd.chorus.stepinvoker.StepPendingException;
import org.chorusbdd.chorus.subsystem.SubsystemAdapter;
import org.chorusbdd.chorus.util.ChorusRemotingException;
import org.chorusbdd.chorus.util.assertion.ChorusAssert;

import java.lang.reflect.InvocationTargetException;
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

    /**
     * Will delegate calls to a remote Handler exported as a JMX MBean
     */
    public Object performActionInRemoteComponent(String action, String componentName, RemotingManagerConfig remotingInfo) {

        ChorusAssert.assertTrue("Remoting config must be valid for " + componentName, new RemotingConfigValidator().checkValid(remotingInfo));

        ChorusHandlerJmxProxy proxy = getProxyForComponent(componentName, remotingInfo);
        List<Map> stepMetaData = proxy.getStepMetadata();

        List<StepInvoker> invokers = getRemoteStepInvokers(proxy, stepMetaData);

        StepFinder stepFinder = new StepFinder(invokers, action);
        stepFinder.findStepMethod();

        Object result;
        if (stepFinder.stepWasFound()) {
            result = processRemoteMethod(stepFinder.getChosenStepInvoker(), stepFinder.getInvokerArgs());
        } else {
            String message = String.format("There is no step handler available for action (%s) on component (%s)", action, componentName);
            log.error(message);
            throw new RemoteStepNotFoundException(action, componentName);
        }
        return result;
    }

    private List<StepInvoker> getRemoteStepInvokers(ChorusHandlerJmxProxy proxy, List<Map> stepMetaData) {
        List<StepInvoker> invokers = new ArrayList<>();
        InvokerMapAdapter invokerMapAdapter = new InvokerMapAdapter();
        for (Map invokerProperties : stepMetaData) {
            StepInvoker invoker = invokerMapAdapter.toRemoteStepInvoker(proxy, invokerProperties);
            invokers.add(invoker);
        }
        return invokers;
    }

    private ChorusHandlerJmxProxy getProxyForComponent(String componentName, RemotingManagerConfig remotingInfo) {
        ChorusHandlerJmxProxy proxy = proxies.get(componentName);
        if ( proxy == null) {
            proxy = new ChorusHandlerJmxProxy(componentName, remotingInfo.getHost(), remotingInfo.getPort(), remotingInfo.getConnectionAttempts(), remotingInfo.getConnectionAttemptMillis());
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
        } catch (IllegalAccessException e) {
            throw new ChorusRemotingException(e);
        } catch (InvocationTargetException e) {
            throw new ChorusRemotingException(e);
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
