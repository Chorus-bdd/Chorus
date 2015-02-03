package org.chorusbdd.chorus.handlers.remoting;

import org.chorusbdd.chorus.processes.manager.ProcessManager;
import org.chorusbdd.chorus.processes.manager.config.ProcessManagerConfig;

import java.util.Map;

/**
 * Created by nick on 06/10/2014.
 *
 * Generate a RemotingConfig from a local process which was started by ProcessManager
 */
class LocalProcessRemotingConfigs {

    private ProcessManager processManager;
    private Map<String, RemotingConfigBuilder> remotingConfigMap;

    public LocalProcessRemotingConfigs(ProcessManager processManager, Map<String, RemotingConfigBuilder> remotingConfigMap) {
        this.processManager = processManager;
        this.remotingConfigMap = remotingConfigMap;
    }


    /**
     * If processName was a process started by ProcessesHandler/ProcessManager, then we may be able to find the remoting setup
     * from the process config
     */
    RemotingConfigBuilder getConfigForProcessManagerProcess(String processName) {
        RemotingConfigBuilder result = null;
        ProcessManagerConfig processInfo = processManager.getProcessConfig(processName);
        if ( processInfo != null && processInfo.isRemotingConfigDefined() ) {
            result = getConfigForLocalProcess(processInfo, processName);
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
    private RemotingConfigBuilder getConfigForLocalProcess(ProcessManagerConfig processInfo, String processName) {

        //there is no remoting config for this process config name, so try to create one based on process properties
        RemotingConfigBuilder remotingConfigBuilder = remotingConfigMap.get(processName);
        if (remotingConfigBuilder == null) {
            remotingConfigBuilder = new RemotingConfigBuilder();
            remotingConfigBuilder.setHost("localhost");
            remotingConfigBuilder.setPort(processInfo.getRemotingPort());
            remotingConfigBuilder.setConfigName(processName);
        }
        return remotingConfigBuilder;
    }
}
