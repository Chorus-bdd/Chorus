/**
 *  Copyright (C) 2000-2013 The Software Conservancy and Original Authors.
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
package org.chorusbdd.chorus.processes.processmanager;

import org.chorusbdd.chorus.annotations.Scope;
import org.chorusbdd.chorus.handlerutils.config.AbstractHandlerConfig;
import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;

import java.io.File;

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
 * ProcessesConfig represents a template config from which one or more ProcessInfo, representing a runtime
 * process, can be built
 *
 * Where multiple processes are launched from the same ProcessesConfig we derive a new jmx port
 * or debug port for each ProcessInfo by auto-incrementing the ports
 */
public class ProcessesConfig extends AbstractHandlerConfig {

    private static ChorusLog log = ChorusLogFactory.getLog(ProcessesConfig.class);

    private int initialJmxPort = -1;
    private int initialDebugPort = -1;

    //a process info to associate with a running process
    //the first will take a
    private ProcessInfo processInfo = new ProcessInfo();

    public void setInitialJmxPort(int initialJmxPort) {
        this.initialJmxPort = initialJmxPort;
    }

    public void setInitialDebugPort(int initialDebugPort) {
        this.initialDebugPort = initialDebugPort;
    }

    public ProcessInfo nextProcess(String processName) {
        ProcessInfo nextProcess = (ProcessInfo) processInfo.clone();
        nextProcess.setProcessName(processName);
        this.processInfo.incrementDebugPort();
        this.processInfo.incrementJmxPort();
        return nextProcess;
    }

    public void reset() {
        this.processInfo.setJmxPort(initialJmxPort);
        this.processInfo.setDebugPort(initialDebugPort);
    }

    public boolean isValid() {
        boolean valid = true;
        if ( ! isSet(processInfo.getGroupName())) {
            valid = logInvalidConfig("config groupName was null or empty");
        } 
        
        if ( processInfo.isJavaProcess() ) {
            //some properties are mandatory for java processes
            String jre = processInfo.getJre();
            if ( jre == null || ! new File(jre).isDirectory() ) {
                valid = logInvalidConfig("jre property is null or jre path does not exist");
            } else if ( ! isSet(processInfo.getClasspath()) ) {
                valid = logInvalidConfig("classpath was null");
            } else if ( ! isSet(processInfo.getMainclass()) ) {
                valid = logInvalidConfig("main class was null or empty");
            }
        } else {
            //some properties should not be used for non-java processes
            valid = checkPropertiesForNativeProcess();
        }
        return valid;
    }

    private boolean checkPropertiesForNativeProcess() {
        boolean valid = true;
        if (isSet(processInfo.getMainclass())) {
            valid = logInvalidConfig("Cannot the mainclass property for non-java process configured with pathToExecutable");        
        } else if (isSet(processInfo.getJvmargs()) ) {
            valid = logInvalidConfig("Cannot set jvmargs property for non-java process configured with pathToExecutable");
        }
        return valid;  
    }

    private boolean isSet(String propertyValue) {
        return propertyValue != null && propertyValue.trim().length() > 0;
    }

    public String getValidationRuleDescription() {
        return "groupName, jre, classpath and mainclass must be set for java processes";
    }
    
    protected ChorusLog getLog() {
        return log;
    }

    ////////////////////////////////////////////////////
    // Delegate to ProcessInfo for getters and setters

    public String getGroupName() {
        return processInfo.getGroupName();
    }

    public void setGroupName(String name) {
        processInfo.setGroupName(name);
    }

    public String getJre() {
        return processInfo.getJre();
    }

    public void setJre(String jre) {
        processInfo.setJre(jre);
    }

    public String getClasspath() {
        return processInfo.getClasspath();
    }

    public void setClasspath(String classpath) {
        processInfo.setClasspath(classpath);
    }

    public String getJvmargs() {
        return processInfo.getJvmargs();
    }

    public void setJvmargs(String jvmargs) {
        processInfo.setJvmargs(jvmargs);
    }

    public String getMainclass() {
        return processInfo.getMainclass();
    }

    public void setMainclass(String mainclass) {
        processInfo.setMainclass(mainclass);
    }

    public String getPathToExecutable() {
        return processInfo.getPathToExecutable();
    }

    public void setPathToExecutable(String pathToExecutable) {
        processInfo.setPathToExecutable(pathToExecutable);
    }

    public String getArgs() {
        return processInfo.getArgs();
    }

    public void setArgs(String args) {
        processInfo.setArgs(args);
    }

    public OutputMode getStdErrMode() {
        return processInfo.getStdErrMode();
    }

    public void setStdErrMode(OutputMode stdErrMode) {
        processInfo.setStdErrMode(stdErrMode);
    }

    public OutputMode getStdOutMode() {
        return processInfo.getStdOutMode();
    }

    public void setStdOutMode(OutputMode stdOutMode) {
        processInfo.setStdOutMode(stdOutMode);
    }

    public int getJmxPort() {
        return processInfo.getJmxPort();
    }

    public void incrementJmxPort() {
        processInfo.incrementJmxPort();
    }

    public void setJmxPort(int jmxPort) {
        processInfo.setJmxPort(jmxPort);
    }

    public boolean isRemotingConfigDefined() {
        return processInfo.isRemotingConfigDefined();
    }

    public int getDebugPort() {
        return processInfo.getDebugPort();
    }

    public void setDebugPort(int debugPort) {
        processInfo.setDebugPort(debugPort);
    }

    public void incrementDebugPort() {
        processInfo.incrementDebugPort();
    }

    public int getTerminateWaitTime() {
        return processInfo.getTerminateWaitTime();
    }

    public void setTerminateWaitTime(int terminateWaitTime) {
        processInfo.setTerminateWaitTime(terminateWaitTime);
    }

    public String getLogDirectory() {
        return processInfo.getLogDirectory();
    }

    public void setLogDirectory(String logDirectory) {
        processInfo.setLogDirectory(logDirectory);
    }

    public boolean isAppendToLogs() {
        return processInfo.isAppendToLogs();
    }

    public void setAppendToLogs(boolean appendToLogs) {
        processInfo.setAppendToLogs(appendToLogs);
    }

    public boolean isCreateLogDir() {
        return processInfo.isCreateLogDir();
    }

    public void setCreateLogDir(boolean createLogDir) {
        processInfo.setCreateLogDir(createLogDir);
    }

    public int getProcessCheckDelay() {
        return processInfo.getProcessCheckDelay();
    }

    public void setProcessCheckDelay(int processCheckDelay) {
        processInfo.setProcessCheckDelay(processCheckDelay);
    }

    public int getReadAheadBufferSize() {
        return processInfo.getReadAheadBufferSize();
    }

    public void setReadAheadBufferSize(int readAheadBufferSize) {
        processInfo.setReadAheadBufferSize(readAheadBufferSize);
    }

    public int getReadTimeoutSeconds() {
        return processInfo.getReadTimeoutSeconds();
    }

    public void setReadTimeoutSeconds(int readTimeoutSeconds) {
        processInfo.setReadTimeoutSeconds(readTimeoutSeconds);
    }

    public Scope getProcessScope() {
        return processInfo.getProcessScope();
    }

    public void setProcessScope(Scope processScope) {
        processInfo.setProcessScope(processScope);
    }

    public String getProcessConfigName() {
        return processInfo.getProcessConfigName();
    }

    public void setProcessConfigName(String processConfigName) {
        processInfo.setProcessConfigName(processConfigName);
    }

    public boolean isJavaProcess() {
        return processInfo.isJavaProcess();
    }

    @Override
    public String toString() {
        return "ProcessesConfig{" +
                  processInfo +
                '}';
    }
}
