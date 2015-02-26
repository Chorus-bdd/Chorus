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
package org.chorusbdd.chorus.processes.manager.config;

import org.chorusbdd.chorus.annotations.Scope;

/**
 * Created with IntelliJ IDEA.
 * User: GA2EBBU
 * Date: 21/09/12
 * Time: 11:08
 *
 * A build for the ProcessManagerConfig
 * 
 * The process will be a java process if pathToExecutable property is not provided.
 * In this case the jre and classpath default to the interpreter's jvm and classpath
 * These can be overridden by setting the jre and classpath properties
 * 
 * jvmargs and mainclass are only applicable for java processes, they should not be set
 * if the process is a native process with pathToExecutable specified.
 *
 * ProcessesConfig represents a template config / builder from which one or more ProcessInfo, representing a runtime
 * process, can be built. Where multiple processes are launched from the same ProcessesConfig we derive a new jmx port
 * or debug port for each ProcessInfo by auto-incrementing the ports
 */
public class ProcessesConfigBuilder implements ProcessManagerConfig {

    private String configName;
    private String pathToExecutable;
    private String jre = System.getProperty("java.home");
    private String classpath = System.getProperty("java.class.path");
    private String jvmargs;
    private String mainclass;
    private String args;
    private OutputMode stdOutMode = OutputMode.FILE;
    private OutputMode stdErrMode = OutputMode.FILE;
    private int terminateWaitTime = 30;
    private String logDirectory;
    private boolean appendToLogs;
    private boolean createLogDir = true; //whether to auto create
    private int processCheckDelay = 500;
    private int readTimeoutSeconds = 10;
    private Scope processScope = Scope.SCENARIO;
    private int debugPort = -1;
    //port on which to start the service which allows the process to export handlers across the network
    private int remotingPort = -1;
    private boolean enabled = true;


    /**
     * @return an immutable config to use to start a process with ProcessManager subsystem
     */
    public ProcessManagerConfig build() {

        //using the getter methods to allow subclasses to override

        ProcessConfig nextProcess = new ProcessConfig(
            getConfigName(),
            getPathToExecutable(),
            getJre(),
            getClasspath(),
            getJvmargs(),
            getMainclass(),
            getArgs(),
            getStdOutMode(),
            getStdErrMode(),
            getRemotingPort(),
            getDebugPort(),
            getTerminateWaitTime(),
            getLogDirectory(),
            isAppendToLogs(),
            isCreateLogDir(),
            getProcessCheckDelay(),
            getReadTimeoutSeconds(),
            getProcessScope(),
            isEnabled()
        );
        return nextProcess;
    }

    public String getConfigName() {
        return configName;
    }

    public ProcessesConfigBuilder setConfigName(String name) {
        this.configName = name;
        return this;
    }

    public String getJre() {
        return jre;
    }

    public ProcessesConfigBuilder setJre(String jre) {
        this.jre = jre;
        return this;
    }

    public String getClasspath() {
        return classpath;
    }

    public ProcessesConfigBuilder setClasspath(String classpath) {
        this.classpath = classpath;;
        return this;
    }

    public String getJvmargs() {
        return jvmargs == null ? "" : jvmargs;
    }

    public ProcessesConfigBuilder setJvmargs(String jvmargs) {
        this.jvmargs = jvmargs;
        return this;
    }

    public String getMainclass() {
        return mainclass;
    }

    public ProcessesConfigBuilder setMainclass(String mainclass) {
        this.mainclass = mainclass;
        return this;
    }

    public String getPathToExecutable() {
        return pathToExecutable;
    }

    public ProcessesConfigBuilder setPathToExecutable(String pathToExecutable) {
        this.pathToExecutable = pathToExecutable;
        return this;
    }

    public String getArgs() {
        return args == null ? "" : args;
    }

    public ProcessesConfigBuilder setArgs(String args) {
        this.args = args;
        return this;
    }

    public OutputMode getStdErrMode() {
        return stdErrMode;
    }

    public ProcessesConfigBuilder setStdErrMode(OutputMode stdErrMode) {
        this.stdErrMode = stdErrMode;
        return this;
    }

    public OutputMode getStdOutMode() {
        return stdOutMode;
    }

    public ProcessesConfigBuilder setStdOutMode(OutputMode stdOutMode) {
        this.stdOutMode = stdOutMode;
        return this;
    }

    public int getRemotingPort() {
        return remotingPort;
    }

    public ProcessesConfigBuilder setRemotingPort(int remotingPort) {
        this.remotingPort = remotingPort;
        return this;
    }

    public boolean isRemotingConfigDefined() {
        return remotingPort != -1;
    }

    public int getDebugPort() {
        return debugPort;
    }

    public ProcessesConfigBuilder setDebugPort(int debugPort) {
        this.debugPort = debugPort;
        return this;
    }

    public int getTerminateWaitTime() {
        return terminateWaitTime;
    }

    public ProcessesConfigBuilder setTerminateWaitTime(int terminateWaitTime) {
        this.terminateWaitTime = terminateWaitTime;
        return this;
    }

    public String getLogDirectory() {
        return logDirectory;
    }

    public ProcessesConfigBuilder setLogDirectory(String logDirectory) {
        this.logDirectory = logDirectory;
        return this;
    }

    public boolean isAppendToLogs() {
        return appendToLogs;
    }

    public ProcessesConfigBuilder setAppendToLogs(boolean appendToLogs) {
        this.appendToLogs = appendToLogs;
        return this;
    }

    public boolean isCreateLogDir() {
        return createLogDir;
    }

    public ProcessesConfigBuilder setCreateLogDir(boolean createLogDir) {
        this.createLogDir = createLogDir;
        return this;
    }

    public int getProcessCheckDelay() {
        return processCheckDelay;
    }

    public ProcessesConfigBuilder setProcessCheckDelay(int processCheckDelay) {
        this.processCheckDelay = processCheckDelay;
        return this;
    }

    public int getReadTimeoutSeconds() {
        return readTimeoutSeconds;
    }

    public ProcessesConfigBuilder setReadTimeoutSeconds(int readTimeoutSeconds) {
        this.readTimeoutSeconds = readTimeoutSeconds;
        return this;
    }

    public Scope getProcessScope() {
        return processScope;
    }

    public ProcessesConfigBuilder setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isJavaProcess() {
        return ! isSet(pathToExecutable);
    }

    private boolean isSet(String propertyValue) {
        return propertyValue != null && propertyValue.trim().length() > 0;
    }

    public ProcessesConfigBuilder setProcessScope(Scope processScope) {
        this.processScope = processScope;
        return this;
    }

    public void incrementDebugPort(int startedCount) {
        if ( debugPort != -1) { //only if this process supports debug
            debugPort += startedCount;
        }
    }

    public void incrementRemotingPort(int startedCount) {
        if ( remotingPort != -1) { //only if this process supports remoting
            remotingPort += startedCount;
        }
    }

}
