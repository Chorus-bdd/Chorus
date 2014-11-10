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
package org.chorusbdd.chorus.processes.manager.process;

import org.chorusbdd.chorus.annotations.Scope;
import org.chorusbdd.chorus.processes.manager.config.OutputMode;
import org.chorusbdd.chorus.processes.manager.config.ProcessManagerConfig;
import org.chorusbdd.chorus.processes.manager.config.StartMode;
import org.chorusbdd.chorus.processes.manager.process.ChorusProcess;

/**
 * Represents a running process
 *
 * The process has a name and a configuration
 */
public class NamedProcess implements ProcessManagerConfig {

    private final String processName;
    private final ProcessManagerConfig processManagerConfig;
    private ChorusProcess process;

    public NamedProcess(String processName, ProcessManagerConfig processManagerConfig) {
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

    public int getRemotingPort() {
        return processManagerConfig.getRemotingPort();
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

    public int getReadTimeoutSeconds() {
        return processManagerConfig.getReadTimeoutSeconds();
    }

    public Scope getProcessScope() {
        return processManagerConfig.getProcessScope();
    }

    public StartMode getStartMode() { return processManagerConfig.getStartMode(); }

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
