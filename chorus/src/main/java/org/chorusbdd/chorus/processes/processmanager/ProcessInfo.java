package org.chorusbdd.chorus.processes.processmanager;

import org.chorusbdd.chorus.annotations.Scope;

/**
 * Represents the configuration of a running process
 * This is derived from a ProcessesConfiguration (the process config template)
 */
public class ProcessInfo implements Cloneable {

    private String groupName;
    private String processName;
    private String pathToExecutable;
    private String jre = System.getProperty("java.home");
    private String classpath = System.getProperty("java.class.path");
    private String jvmargs;
    private String mainclass;
    private String args;
    private OutputMode stdOutMode = OutputMode.INLINE;
    private OutputMode stdErrMode = OutputMode.INLINE;
    private int jmxPort = -1;
    private int debugPort = -1;
    private int terminateWaitTime = 30;
    private String logDirectory;
    private boolean appendToLogs;
    private boolean createLogDir = true; //whether to auto create
    private int processCheckDelay = 500;
    private int readAheadBufferSize = 65536; //read ahead process output in CAPTURED mode
    private int readTimeoutSeconds = 10;
    private Scope processScope = Scope.SCENARIO;
    private String processConfigName;
    private ChorusProcess process;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String name) {
        this.groupName = name;
    }

    public String getJre() {
        return jre;
    }

    public void setJre(String jre) {
        this.jre = jre;
    }

    public String getClasspath() {
        return classpath;
    }

    public void setClasspath(String classpath) {
        this.classpath = classpath;
    }

    public String getJvmargs() {
        return jvmargs == null ? "" : jvmargs;
    }

    public void setJvmargs(String jvmargs) {
        this.jvmargs = jvmargs;
    }

    public String getMainclass() {
        return mainclass;
    }

    public void setMainclass(String mainclass) {
        this.mainclass = mainclass;
    }

    public String getPathToExecutable() {
        return pathToExecutable;
    }

    public void setPathToExecutable(String pathToExecutable) {
        this.pathToExecutable = pathToExecutable;
    }

    public String getArgs() {
        return args == null ? "" : args;
    }

    public void setArgs(String args) {
        this.args = args;
    }

    public OutputMode getStdErrMode() {
        return stdErrMode;
    }

    public void setStdErrMode(OutputMode stdErrMode) {
        this.stdErrMode = stdErrMode;
    }

    public OutputMode getStdOutMode() {
        return stdOutMode;
    }

    public void setStdOutMode(OutputMode stdOutMode) {
        this.stdOutMode = stdOutMode;
    }

    public int getJmxPort() {
        return jmxPort;
    }

    public void incrementJmxPort() {
        if ( jmxPort != -1) {
            jmxPort++;
        }
    }

    public void setJmxPort(int jmxPort) {
        this.jmxPort = jmxPort;
    }

    public boolean isRemotingConfigDefined() {
        return jmxPort != -1;
    }

    public int getDebugPort() {
        return debugPort;
    }

    public void setDebugPort(int debugPort) {
        this.debugPort = debugPort;
    }

    public void incrementDebugPort() {
        if ( debugPort != -1 ) {
            debugPort++;
        }
    }

    public int getTerminateWaitTime() {
        return terminateWaitTime;
    }

    public void setTerminateWaitTime(int terminateWaitTime) {
        this.terminateWaitTime = terminateWaitTime;
    }

    public String getLogDirectory() {
        return logDirectory;
    }

    public void setLogDirectory(String logDirectory) {
        this.logDirectory = logDirectory;
    }

    public boolean isAppendToLogs() {
        return appendToLogs;
    }

    public void setAppendToLogs(boolean appendToLogs) {
        this.appendToLogs = appendToLogs;
    }

    public boolean isCreateLogDir() {
        return createLogDir;
    }

    public void setCreateLogDir(boolean createLogDir) {
        this.createLogDir = createLogDir;
    }

    public int getProcessCheckDelay() {
        return processCheckDelay;
    }

    public void setProcessCheckDelay(int processCheckDelay) {
        this.processCheckDelay = processCheckDelay;
    }

    public int getReadAheadBufferSize() {
        return readAheadBufferSize;
    }

    public void setReadAheadBufferSize(int readAheadBufferSize) {
        this.readAheadBufferSize = readAheadBufferSize;
    }

    public int getReadTimeoutSeconds() {
        return readTimeoutSeconds;
    }

    public void setReadTimeoutSeconds(int readTimeoutSeconds) {
        this.readTimeoutSeconds = readTimeoutSeconds;
    }

    public Scope getProcessScope() {
        return processScope;
    }

    public void setProcessScope(Scope processScope) {
        this.processScope = processScope;
    }

    public String getProcessConfigName() {
        return processConfigName;
    }

    public void setProcessConfigName(final String processConfigName) {
        this.processConfigName = processConfigName;
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

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public void setProcess(ChorusProcess process) {
        this.process = process;
    }

    public ChorusProcess getProcess() {
        return process;
    }

    @Override
    public String toString() {
        return "ProcessesInfo{" +
                getPropertiesAsString() +
                '}';
    }

    public String getPropertiesAsString() {
        return "groupName='" + groupName + '\'' +
                ", pathToExecutable='" + pathToExecutable + '\'' +
                ", jre='" + jre + '\'' +
                ", classpath='" + classpath + '\'' +
                ", jvmargs='" + jvmargs + '\'' +
                ", mainclass='" + mainclass + '\'' +
                ", args='" + args + '\'' +
                ", stdOutMode=" + stdOutMode + '\'' +
                ", stdErrMode=" + stdErrMode + '\'' +
                ", jmxPort=" + jmxPort + '\'' +
                ", debugPort=" + debugPort + '\'' +
                ", terminateWaitTime=" + terminateWaitTime + '\'' +
                ", logDirectory='" + logDirectory + '\'' +
                ", appendToLogs=" + appendToLogs + '\'' +
                ", createLogDir=" + createLogDir + '\'' +
                ", processCheckDelay=" + processCheckDelay + '\'';
    }

    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            // This should never happen
            throw new RuntimeException(e);
        }
    }

}
