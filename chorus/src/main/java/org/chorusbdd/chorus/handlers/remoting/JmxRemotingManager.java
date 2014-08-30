package org.chorusbdd.chorus.handlers.remoting;

import org.chorusbdd.chorus.core.interpreter.StepPendingException;
import org.chorusbdd.chorus.handlers.util.HandlerUtils;
import org.chorusbdd.chorus.remoting.jmx.ChorusHandlerJmxProxy;
import org.chorusbdd.chorus.util.ChorusException;
import org.chorusbdd.chorus.util.ChorusRemotingException;
import org.chorusbdd.chorus.util.RegexpUtils;
import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;

import javax.management.RuntimeMBeanException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nick on 30/08/2014.
 */
public class JmxRemotingManager implements RemotingManager {

    private static ChorusLog log = ChorusLogFactory.getLog(JmxRemotingManager.class);

    /**
     * Map: mBeanName -> proxy
     */
    private final Map<String, ChorusHandlerJmxProxy> proxies = new HashMap<String, ChorusHandlerJmxProxy>();

    /**
     * Will delegate calls to a remote Handler exported as a JMX MBean
     */
    public Object performActionInRemoteComponent(String action, String componentName, RemotingConfig remotingConfig) {
        ChorusHandlerJmxProxy proxy = getProxyForComponent(componentName, remotingConfig);
        Map<String, String[]> stepMetaData = proxy.getStepMetadata();
        
        RemoteMethodFinder remoteMethodFinder = new RemoteMethodFinder(action, componentName, stepMetaData).invoke();

        Object result;
        if (remoteMethodFinder.methodWasFound()) {
            result = processRemoteMethod(proxy, remoteMethodFinder);
        } else {
            String message = String.format("There is no step handler available for action (%s) on component (%s)", action, componentName);
            log.error(message);
            throw new RemoteStepNotFoundException(action, componentName);
        }
        return result;
    }

    private Object processRemoteMethod(ChorusHandlerJmxProxy proxy, RemoteMethodFinder remoteMethodFinder) {
        Object result;
        if (remoteMethodFinder.isStepPending()) {
            throw new StepPendingException(remoteMethodFinder.getMethodUidToCallPendingMessage());
        }
        result = invokeRemoteStep(proxy, remoteMethodFinder);
        return result;
    }

    private Object invokeRemoteStep(ChorusHandlerJmxProxy proxy, RemoteMethodFinder remoteMethodFinder) {
        Object result;
        try {
            result = proxy.invokeStep(remoteMethodFinder.getMethodUidToCall(), remoteMethodFinder.getMethodArgsToPass());
        } catch (RuntimeMBeanException mbe) {
            //here if an exception was thrown by the remote Step method
            RuntimeException targetException = mbe.getTargetException();
            if (targetException instanceof ChorusRemotingException) {
                //the exception thrown by the remote Step method was converted to a ChorusRemotingException by the chorus step exporter 
                //this is how we handle remote exceptions which might otherwise come from library classes we don't have locally
                throw targetException;
            } else {
                throw new ChorusRemotingException(targetException);
            }
        } catch (Exception e) {
            throw new ChorusRemotingException(e);
        }
        return result;
    }

    /**
     * Called at end of scenario - closes all MBean connections
     */
    public void destroy() {
        for (Map.Entry<String, ChorusHandlerJmxProxy> entry : proxies.entrySet()) {
            String name = entry.getKey();
            ChorusHandlerJmxProxy jmxProxy = entry.getValue();
            jmxProxy.destroy();
            log.debug("Closed JMX connection to: " + name);
        }
    }

    private ChorusHandlerJmxProxy getProxyForComponent(String name, RemotingConfig remotingConfig) {
        ChorusHandlerJmxProxy proxy = proxies.get(name);
        if (proxy == null) {
            proxy = new ChorusHandlerJmxProxy(remotingConfig.getHost(), remotingConfig.getPort(), remotingConfig.getConnectionAttempts(), remotingConfig.getConnectionAttemptMillis());
            proxies.put(name, proxy);
            log.debug("Opened JMX connection to: " + name);
        }
        return proxy;
    }

    /**
     * Find the correct remote method and warn if there are multiple matches
     */
    private static class RemoteMethodFinder {
        private String action;
        private String componentName;
        private Map<String, String[]> stepMetaData;
        private String methodUidToCall;
        private Object[] methodArgsToPass;
        private String methodUidToCallPendingMessage;

        public RemoteMethodFinder(String action, String componentName, Map<String, String[]> stepMetaData) {
            this.action = action;
            this.componentName = componentName;
            this.stepMetaData = stepMetaData;
        }

        public String getMethodUidToCall() {
            return methodUidToCall;
        }
        
        public boolean methodWasFound() {
            return methodUidToCall != null;
        }

        public Object[] getMethodArgsToPass() {
            return methodArgsToPass;
        }
        
        public boolean isStepPending() {
            return methodUidToCallPendingMessage != null;
        }

        public String getMethodUidToCallPendingMessage() {
            return methodUidToCallPendingMessage;
        }

        public RemoteMethodFinder invoke() {
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
            return this;
        }
    }
}
