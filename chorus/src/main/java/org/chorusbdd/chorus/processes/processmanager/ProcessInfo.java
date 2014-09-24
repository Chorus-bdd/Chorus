package org.chorusbdd.chorus.processes.processmanager;

import org.chorusbdd.chorus.annotations.Scope;

/**
 * Represents the configuration of a running process
 * This is derived from a ProcessesConfiguration (the process config template)
 */
public class ProcessInfo {

    private final String groupName;
    private final String processName;
    private final String pathToExecutable;
    private final String jre;
    private final String classpath;
    private final String jvmargs;
    private final String mainclass;
    private final String args;
    private final OutputMode stdOutMode;
    private final OutputMode stdErrMode;
    private final int jmxPort;
    private final int debugPort;
    private final int terminateWaitTime;
    private final String logDirectory;
    private final boolean appendToLogs;
    private final boolean createLogDir; //whether to auto create
    private final int processCheckDelay;
    private final int readAheadBufferSize; //read ahead process output in CAPTURED mode
    private final int readTimeoutSeconds;
    private final Scope processScope;
    private final String processConfigName;
    private ChorusProcess process;

    public ProcessInfo(String groupName, String processName, String pathToExecutable, String jre, String classpath, String jvmargs, String mainclass,
                       String args, OutputMode stdOutMode, OutputMode stdErrMode, int jmxPort, int debugPort, int terminateWaitTime,
                       String logDirectory, boolean appendToLogs, boolean createLogDir, int processCheckDelay, int readAheadBufferSize,
                       int readTimeoutSeconds, Scope processScope, String processConfigName) {
        this.groupName = groupName;
        this.processName = processName;
        this.pathToExecutable = pathToExecutable;
        this.jre = jre;
        this.classpath = classpath;
        this.jvmargs = jvmargs;
        this.mainclass = mainclass;
        this.args = args;
        this.stdOutMode = stdOutMode;
        this.stdErrMode = stdErrMode;
        this.jmxPort = jmxPort;
        this.debugPort = debugPort;
        this.terminateWaitTime = terminateWaitTime;
        this.logDirectory = logDirectory;
        this.appendToLogs = appendToLogs;
        this.createLogDir = createLogDir;
        this.processCheckDelay = processCheckDelay;
        this.readAheadBufferSize = readAheadBufferSize;
        this.readTimeoutSeconds = readTimeoutSeconds;
        this.processScope = processScope;
        this.processConfigName = processConfigName;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getJre() {
        return jre;
    }

    public String getClasspath() {
        return classpath;
    }

    public String getJvmargs() {
        return jvmargs == null ? "" : jvmargs;
    }

    public String getMainclass() {
        return mainclass;
    }

    public String getPathToExecutable() {
        return pathToExecutable;
    }

    public String getArgs() {
        return args == null ? "" : args;
    }

    public OutputMode getStdErrMode() {
        return stdErrMode;
    }

    public OutputMode getStdOutMode() {
        return stdOutMode;
    }

    public int getJmxPort() {
        return jmxPort;
    }

    public boolean isRemotingConfigDefined() {
        return jmxPort != -1;
    }

    public int getDebugPort() {
        return debugPort;
    }

    public int getTerminateWaitTime() {
        return terminateWaitTime;
    }

    public String getLogDirectory() {
        return logDirectory;
    }

    public boolean isAppendToLogs() {
        return appendToLogs;
    }

    public boolean isCreateLogDir() {
        return createLogDir;
    }

    public int getProcessCheckDelay() {
        return processCheckDelay;
    }

    public int getReadAheadBufferSize() {
        return readAheadBufferSize;
    }

    public int getReadTimeoutSeconds() {
        return readTimeoutSeconds;
    }

    public Scope getProcessScope() {
        return processScope;
    }

    public String getProcessConfigName() {
        return processConfigName;
    }

    public boolean isJavaProcess() {
        return ! isSet(pathToExecutable);
    }

    private boolean isSet(String propertyValue) {
        return propertyValue != null && propertyValue.trim().length() > 0;
    }

    /**
     * the name of the running process, which may differ from the name of the config template if launched
     * with the 'I launch a myTemplate process named xyz'
     */
    public String getProcessName() {
        return processName;
    }

    public void setProcess(ChorusProcess process) {
        this.process = process;
    }

    public ChorusProcess getProcess() {
        return process;
    }

    @Override
    public String toString() {
        return "ProcessInfo{" +
                "groupName='" + groupName + '\'' +
                ", processName='" + processName + '\'' +
                ", pathToExecutable='" + pathToExecutable + '\'' +
                ", jre='" + jre + '\'' +
                ", classpath='" + classpath + '\'' +
                ", jvmargs='" + jvmargs + '\'' +
                ", mainclass='" + mainclass + '\'' +
                ", args='" + args + '\'' +
                ", stdOutMode=" + stdOutMode +
                ", stdErrMode=" + stdErrMode +
                ", jmxPort=" + jmxPort +
                ", debugPort=" + debugPort +
                ", terminateWaitTime=" + terminateWaitTime +
                ", logDirectory='" + logDirectory + '\'' +
                ", appendToLogs=" + appendToLogs +
                ", createLogDir=" + createLogDir +
                ", processCheckDelay=" + processCheckDelay +
                ", readAheadBufferSize=" + readAheadBufferSize +
                ", readTimeoutSeconds=" + readTimeoutSeconds +
                ", processScope=" + processScope +
                ", processConfigName='" + processConfigName + '\'' +
                '}';
    }

}
