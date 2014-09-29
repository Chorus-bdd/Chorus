package org.chorusbdd.chorus.processes.processmanager;

import org.chorusbdd.chorus.annotations.Scope;
import org.chorusbdd.chorus.core.interpreter.subsystem.processes.OutputMode;
import org.chorusbdd.chorus.core.interpreter.subsystem.processes.ProcessManagerConfig;

/**
 * Represents the configuration of a running process
 * This is derived from a ProcessesConfiguration (the process config template)
 */
class ProcessInfo implements ProcessManagerConfig {

    private final String processName;
    private final ProcessManagerConfig processManagerConfig;
    private ChorusProcess process;

    ProcessInfo(String processName, ProcessManagerConfig processManagerConfig) {
        this.processName = processName;
        this.processManagerConfig = processManagerConfig;
    }

    public void setProcess(ChorusProcess process) {
        this.process = process;
    }

    public ChorusProcess getProcess() {
        return process;
    }

    public String getProcessName() {
        return processName;
    }

    public String getConfigName() {
        return processManagerConfig.getConfigName();
    }

    public String getJre() {
        return processManagerConfig.getJre();
    }

    public String getClasspath() {
        return processManagerConfig.getClasspath();
    }

    public String getJvmargs() {
        return processManagerConfig.getJvmargs();
    }

    public String getMainclass() {
        return processManagerConfig.getMainclass();
    }

    public String getPathToExecutable() {
        return processManagerConfig.getPathToExecutable();
    }

    public String getArgs() {
        return processManagerConfig.getArgs();
    }

    public OutputMode getStdErrMode() {
        return processManagerConfig.getStdErrMode();
    }

    public OutputMode getStdOutMode() {
        return processManagerConfig.getStdOutMode();
    }

    public int getJmxPort() {
        return processManagerConfig.getJmxPort();
    }

    public boolean isRemotingConfigDefined() {
        return processManagerConfig.isRemotingConfigDefined();
    }

    public int getDebugPort() {
        return processManagerConfig.getDebugPort();
    }

    public int getTerminateWaitTime() {
        return processManagerConfig.getTerminateWaitTime();
    }

    public String getLogDirectory() {
        return processManagerConfig.getLogDirectory();
    }

    public boolean isAppendToLogs() {
        return processManagerConfig.isAppendToLogs();
    }

    public boolean isCreateLogDir() {
        return processManagerConfig.isCreateLogDir();
    }

    public int getProcessCheckDelay() {
        return processManagerConfig.getProcessCheckDelay();
    }

    public int getReadAheadBufferSize() {
        return processManagerConfig.getReadAheadBufferSize();
    }

    public int getReadTimeoutSeconds() {
        return processManagerConfig.getReadTimeoutSeconds();
    }

    public Scope getProcessScope() {
        return processManagerConfig.getProcessScope();
    }

    public String getProcessConfigName() {
        return processManagerConfig.getProcessConfigName();
    }

    public boolean isJavaProcess() {
        return processManagerConfig.isJavaProcess();
    }

    @Override
    public String toString() {
        return "ProcessInfo{" +
                "processName='" + processName + '\'' +
                ", processManagerConfig=" + processManagerConfig +
                '}';
    }
}
