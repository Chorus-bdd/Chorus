/**
 *  Copyright (C) 2000-2015 The Software Conservancy and Original Authors.
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
 * An immutable runtime config for a process
 */
public class ProcessConfig implements ProcessManagerConfig {

    private final String groupName;
    private final String pathToExecutable;
    private final String jre;
    private final String classpath;
    private final String jvmargs;
    private final String mainclass;
    private final String args;
    private final OutputMode stdOutMode;
    private final OutputMode stdErrMode;
    private final int remotingPort;
    private final int debugPort;
    private final int terminateWaitTime;
    private final String logDirectory;
    private final boolean appendToLogs;
    private final boolean createLogDir; //whether to auto create
    private final int processCheckDelay;
    private final int readTimeoutSeconds;
    private final Scope processScope;
    private boolean enabled;

    public ProcessConfig(String groupName, String pathToExecutable, String jre, String classpath, String jvmargs, String mainclass,
                         String args, OutputMode stdOutMode, OutputMode stdErrMode, int remotingPort, int debugPort, int terminateWaitTime,
                         String logDirectory, boolean appendToLogs, boolean createLogDir, int processCheckDelay,
                         int readTimeoutSeconds, Scope processScope, boolean enabled) {
        this.groupName = groupName;
        this.pathToExecutable = pathToExecutable;
        this.jre = jre;
        this.classpath = classpath;
        this.jvmargs = jvmargs;
        this.mainclass = mainclass;
        this.args = args;
        this.stdOutMode = stdOutMode;
        this.stdErrMode = stdErrMode;
        this.remotingPort = remotingPort;
        this.debugPort = debugPort;
        this.terminateWaitTime = terminateWaitTime;
        this.logDirectory = logDirectory;
        this.appendToLogs = appendToLogs;
        this.createLogDir = createLogDir;
        this.processCheckDelay = processCheckDelay;
        this.readTimeoutSeconds = readTimeoutSeconds;
        this.processScope = processScope;
        this.enabled = enabled;
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

    public int getRemotingPort() {
        return remotingPort;
    }

    public boolean isRemotingConfigDefined() {
        return remotingPort != -1;
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

    public int getReadTimeoutSeconds() {
        return readTimeoutSeconds;
    }

    public Scope getProcessScope() {
        return processScope;
    }

    public boolean isJavaProcess() {
        return ! isSet(pathToExecutable);
    }

    public boolean isEnabled() { return enabled; }

    private boolean isSet(String propertyValue) {
        return propertyValue != null && propertyValue.trim().length() > 0;
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
                ", remotingPort=" + remotingPort +
                ", debugPort=" + debugPort +
                ", terminateWaitTime=" + terminateWaitTime +
                ", logDirectory='" + logDirectory + '\'' +
                ", appendToLogs=" + appendToLogs +
                ", createLogDir=" + createLogDir +
                ", processCheckDelay=" + processCheckDelay +
                ", readTimeoutSeconds=" + readTimeoutSeconds +
                ", processScope=" + processScope +
                '}';
    }

}
