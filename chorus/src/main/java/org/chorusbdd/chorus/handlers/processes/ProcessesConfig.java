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
package org.chorusbdd.chorus.handlers.processes;

import org.chorusbdd.chorus.annotations.Scope;
import org.chorusbdd.chorus.core.interpreter.subsystem.processes.OutputMode;
import org.chorusbdd.chorus.core.interpreter.subsystem.processes.ProcessManagerConfig;
import org.chorusbdd.chorus.handlerconfig.HandlerConfig;

/**
 * Created with IntelliJ IDEA.
 * User: GA2EBBU
 * Date: 21/09/12
 * Time: 11:08
 *
 * A Process config
 * 
 * The process will be a java process if pathToExecutable property is not provided.
 * In tbis case the jre and classpath default to the interpreter's jvm and classpath
 * These can be overridden by setting the jre and classpath properties
 * 
 * jvmargs and mainclass are only applicable for java processes, they should not be set
 * if the process is a native process with pathToExecutable specified.
 *
 * ProcessesConfig represents a template config / builder from which one or more ProcessInfo, representing a runtime
 * process, can be built. Where multiple processes are launched from the same ProcessesConfig we derive a new jmx port
 * or debug port for each ProcessInfo by auto-incrementing the ports
 */
public class ProcessesConfig implements ProcessManagerConfig {

    private String configName;
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

    //when we start a process based on this config we keep count of this so we can auto increment
    //debug and jmx ports for any other similar processes started under different names/aliases
    private int instancesStarted = 0;

    public ProcessManagerConfig buildProcessConfig() {

        //using the getter methods to allow subclasses to override

        int nextJmxPort = getJmxPort() != -1 ? getJmxPort() + instancesStarted : -1;
        int nextDebugPort = getDebugPort() != -1 ? getDebugPort() + instancesStarted : -1;

        RuntimeProcessConfig nextProcess = new RuntimeProcessConfig(
            getConfigName(),
            getPathToExecutable(),
            getJre(),
            getClasspath(),
            getJvmargs(),
            getMainclass(),
            getArgs(),
            getStdOutMode(),
            getStdErrMode(),
            nextJmxPort,
            nextDebugPort,
            getTerminateWaitTime(),
            getLogDirectory(),
            isAppendToLogs(),
            isCreateLogDir(),
            getProcessCheckDelay(),
            getReadAheadBufferSize(),
            getReadTimeoutSeconds(),
            getProcessScope(),
            getProcessConfigName()
        );
        instancesStarted++;
        return nextProcess;
    }

    public void resetInstancesStarted() {
        instancesStarted = 0;
    }

    public String getConfigName() {
        return configName;
    }

    public ProcessesConfig setConfigName(String name) {
        this.configName = name;
        return this;
    }

    public String getJre() {
        return jre;
    }

    public ProcessesConfig setJre(String jre) {
        this.jre = jre;
        return this;
    }

    public String getClasspath() {
        return classpath;
    }

    public ProcessesConfig setClasspath(String classpath) {
        this.classpath = classpath;;
        return this;
    }

    public String getJvmargs() {
        return jvmargs == null ? "" : jvmargs;
    }

    public ProcessesConfig setJvmargs(String jvmargs) {
        this.jvmargs = jvmargs;
        return this;
    }

    public String getMainclass() {
        return mainclass;
    }

    public ProcessesConfig setMainclass(String mainclass) {
        this.mainclass = mainclass;
        return this;
    }

    public String getPathToExecutable() {
        return pathToExecutable;
    }

    public ProcessesConfig setPathToExecutable(String pathToExecutable) {
        this.pathToExecutable = pathToExecutable;
        return this;
    }

    public String getArgs() {
        return args == null ? "" : args;
    }

    public ProcessesConfig setArgs(String args) {
        this.args = args;
        return this;
    }

    public OutputMode getStdErrMode() {
        return stdErrMode;
    }

    public ProcessesConfig setStdErrMode(OutputMode stdErrMode) {
        this.stdErrMode = stdErrMode;
        return this;
    }

    public OutputMode getStdOutMode() {
        return stdOutMode;
    }

    public ProcessesConfig setStdOutMode(OutputMode stdOutMode) {
        this.stdOutMode = stdOutMode;
        return this;
    }

    public int getJmxPort() {
        return jmxPort;
    }

    public ProcessesConfig setJmxPort(int jmxPort) {
        this.jmxPort = jmxPort;
        return this;
    }

    public boolean isRemotingConfigDefined() {
        return jmxPort != -1;
    }

    public int getDebugPort() {
        return debugPort;
    }

    public ProcessesConfig setDebugPort(int debugPort) {
        this.debugPort = debugPort;
        return this;
    }

    public int getTerminateWaitTime() {
        return terminateWaitTime;
    }

    public ProcessesConfig setTerminateWaitTime(int terminateWaitTime) {
        this.terminateWaitTime = terminateWaitTime;
        return this;
    }

    public String getLogDirectory() {
        return logDirectory;
    }

    public ProcessesConfig setLogDirectory(String logDirectory) {
        this.logDirectory = logDirectory;
        return this;
    }

    public boolean isAppendToLogs() {
        return appendToLogs;
    }

    public ProcessesConfig setAppendToLogs(boolean appendToLogs) {
        this.appendToLogs = appendToLogs;
        return this;
    }

    public boolean isCreateLogDir() {
        return createLogDir;
    }

    public ProcessesConfig setCreateLogDir(boolean createLogDir) {
        this.createLogDir = createLogDir;
        return this;
    }

    public int getProcessCheckDelay() {
        return processCheckDelay;
    }

    public ProcessesConfig setProcessCheckDelay(int processCheckDelay) {
        this.processCheckDelay = processCheckDelay;
        return this;
    }

    public int getReadAheadBufferSize() {
        return readAheadBufferSize;
    }

    public ProcessesConfig setReadAheadBufferSize(int readAheadBufferSize) {
        this.readAheadBufferSize = readAheadBufferSize;
        return this;
    }

    public int getReadTimeoutSeconds() {
        return readTimeoutSeconds;
    }

    public ProcessesConfig setReadTimeoutSeconds(int readTimeoutSeconds) {
        this.readTimeoutSeconds = readTimeoutSeconds;
        return this;
    }

    public Scope getProcessScope() {
        return processScope;
    }

    public ProcessesConfig setProcessScope(Scope processScope) {
        this.processScope = processScope;
        return this;
    }

    public String getProcessConfigName() {
        return processConfigName;
    }

    public ProcessesConfig setProcessConfigName(final String processConfigName) {
        this.processConfigName = processConfigName;
        return this;
    }

    public boolean isJavaProcess() {
        return ! isSet(pathToExecutable);
    }

    private boolean isSet(String propertyValue) {
        return propertyValue != null && propertyValue.trim().length() > 0;
    }

    @Override
    public String toString() {
        return "ProcessesConfig{" +
                "configName='" + configName + '\'' +
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
                ", instancesStarted=" + instancesStarted +
                '}';
    }

    /**
     * An immutable runtime config for a process
     */
    private class RuntimeProcessConfig implements ProcessManagerConfig {

        private final String groupName;
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

        RuntimeProcessConfig(String groupName, String pathToExecutable, String jre, String classpath, String jvmargs, String mainclass,
                    String args, OutputMode stdOutMode, OutputMode stdErrMode, int jmxPort, int debugPort, int terminateWaitTime,
                    String logDirectory, boolean appendToLogs, boolean createLogDir, int processCheckDelay, int readAheadBufferSize,
                    int readTimeoutSeconds, Scope processScope, String processConfigName) {
            this.groupName = groupName;
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

        public String getConfigName() {
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

        @Override
        public String toString() {
            return "RuntimeProcessConfig{" +
                    "groupName='" + groupName + '\'' +
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
}
