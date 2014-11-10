package org.chorusbdd.chorus.handlers.remoting;

import org.chorusbdd.chorus.processes.manager.ProcessManager;
import org.chorusbdd.chorus.processes.manager.config.ProcessManagerConfig;

import java.util.Map;

/**
 * Created by nick on 06/10/2014.
 */
class LocalProcessRemotingConfigs {

    private ProcessManager processManager;
    private Map<String, RemotingConfig> remotingConfigMap;

    public LocalProcessRemotingConfigs(ProcessManager processManager, Map<String, RemotingConfig> remotingConfigMap) {
        this.processManager = processManager;
        this.remotingConfigMap = remotingConfigMap;
    }


    /**
     * If processName was a process started by ProcessesHandler/ProcessManager, then we may be able to find the remoting setup
     * from the process config
     */
    RemotingConfig getConfigForProcessManagerProcess(String processName) {
        RemotingConfig result = null;
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
    private RemotingConfig getConfigForLocalProcess(ProcessManagerConfig processInfo, String processName) {

        //there is no remoting config for this process config name, so try to create one based on process properties
        RemotingConfig remotingConfig = remotingConfigMap.get(processName);
        if (remotingConfig == null) {
            remotingConfig = new RemotingConfig();
            remotingConfig.setHost("localhost");
            remotingConfig.setPort(processInfo.getRemotingPort());
            remotingConfig.setConfigName(processName);
        }
        return remotingConfig;
    }
}
