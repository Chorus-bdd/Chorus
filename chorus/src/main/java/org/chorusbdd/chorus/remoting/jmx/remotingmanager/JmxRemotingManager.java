package org.chorusbdd.chorus.remoting.jmx.remotingmanager;

import org.chorusbdd.chorus.core.interpreter.StepMatcher;
import org.chorusbdd.chorus.core.interpreter.StepPendingException;
import org.chorusbdd.chorus.core.interpreter.invoker.StepInvoker;
import org.chorusbdd.chorus.processes.processmanager.ProcessInfo;
import org.chorusbdd.chorus.processes.processmanager.ProcessManager;
import org.chorusbdd.chorus.util.HandlerUtils;
import org.chorusbdd.chorus.util.ChorusException;
import org.chorusbdd.chorus.util.ChorusRemotingException;
import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;

import java.lang.reflect.InvocationTargetException;
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
    public Object performActionInRemoteComponent(String action, String componentName, Map<String, RemotingConfig> remotingConfigMap) {
        ChorusHandlerJmxProxy proxy = getProxyForComponent(componentName, remotingConfigMap);
        Map<String, String[]> stepMetaData = proxy.getStepMetadata();
        
        RemoteStepFinder remoteStepFinder = new RemoteStepFinder(action, componentName, stepMetaData, proxy).findRemoteStepInvoker();

        Object result;
        if (remoteStepFinder.stepWasFound()) {
            result = processRemoteMethod(remoteStepFinder.getFoundStepInvoker(), remoteStepFinder.getFoundArgs());
        } else {
            String message = String.format("There is no step handler available for action (%s) on component (%s)", action, componentName);
            log.error(message);
            throw new RemoteStepNotFoundException(action, componentName);
        }
        return result;
    }

    private Object processRemoteMethod(StepInvoker remoteStepInvoker, Object[] args) {
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
    public void destroy() {
        for (Map.Entry<String, ChorusHandlerJmxProxy> entry : proxies.entrySet()) {
            String name = entry.getKey();
            ChorusHandlerJmxProxy jmxProxy = entry.getValue();
            jmxProxy.destroy();
            log.debug("Closed JMX connection to: " + name);
        }
    }

    private ChorusHandlerJmxProxy getProxyForComponent(String name, Map<String, RemotingConfig> remotingConfigMap) {
        ChorusHandlerJmxProxy proxy = proxies.get(name);
        if (proxy == null) {
            RemotingConfig remotingConfig = remotingConfigMap.get(name);
            if (remotingConfig == null) {
                //perhaps this was a process started locally by process manager
                remotingConfig = getConfigForProcessManagerProcess(name, remotingConfigMap);
            }

            if ( remotingConfig != null) {
                proxy = new ChorusHandlerJmxProxy(remotingConfig.getHost(), remotingConfig.getPort(), remotingConfig.getConnectionAttempts(), remotingConfig.getConnectionAttemptMillis());
                proxies.put(name, proxy);
                log.debug("Opened JMX connection to: " + name);
            } else {
                throw new ChorusException("Failed to find remoting configuration for component: " + name);
            }
        }
        return proxy;
    }

    /**
     * If processName was a process started by ProcessesHandler/ProcessManager, then we may be able to find the remoting setup
     * from the process config
     */
    private RemotingConfig getConfigForProcessManagerProcess(String processName, Map<String, RemotingConfig> remotingConfigMap) {
        RemotingConfig result = null;
        ProcessInfo processInfo = ProcessManager.getInstance().getProcessInfo(processName);
        if ( processInfo != null && processInfo.isRemotingConfigDefined() ) {
            result = getConfigForLocalProcess(remotingConfigMap, processInfo);
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
    private RemotingConfig getConfigForLocalProcess(Map<String, RemotingConfig> remotingConfigMap, ProcessInfo processInfo) {
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
     * Find the correct remote method and warn if there are multiple matches
     */
    private static class RemoteStepFinder {
        private String action;
        private String componentName;
        private Map<String, String[]> stepMetaData;
        private ChorusHandlerJmxProxy proxy;
        private StepInvoker foundStepInvoker;
        private Object[] foundArgs;

        public RemoteStepFinder(String action, String componentName, Map<String, String[]> stepMetaData, ChorusHandlerJmxProxy proxy) {
            this.action = action;
            this.componentName = componentName;
            this.stepMetaData = stepMetaData;
            this.proxy = proxy;
        }

        public boolean stepWasFound() {
            return foundStepInvoker != null;
        }

        public StepInvoker getFoundStepInvoker() {
            return foundStepInvoker;
        }

        public Object[] getFoundArgs() {
            return foundArgs;
        }

        public RemoteStepFinder findRemoteStepInvoker() {
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
    
                //at present we just use the remoteStepInvoker to allow the extractGroups to work but should refactor
                //to actually invoke the remote method with it
                StepInvoker stepInvoker = new RemoteStepInvoker(regex, types, proxy, methodUid, pending);
                Object[] args = StepMatcher.extractGroupsAndCheckMethodParams(stepInvoker, action);
                if (args != null) {
                    if (foundStepInvoker == null) {
                        foundStepInvoker = stepInvoker;
                        foundArgs = args;
                    } else {
                        log.info(String.format("Ambiguous method (%s) found for step (%s) on (%s) will use first method found (%s)",
                                methodUid,
                                action,
                                componentName,
                                foundStepInvoker));
                    }
                }
            }
            return this;
        }
    }
}
