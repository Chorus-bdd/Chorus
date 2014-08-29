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
package org.chorusbdd.chorus.handlers.processes;

import org.chorusbdd.chorus.annotations.Scope;
import org.chorusbdd.chorus.handlers.util.config.AbstractHandlerConfig;
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
 */
public class ProcessesConfig extends AbstractHandlerConfig implements Cloneable {

    private static ChorusLog log = ChorusLogFactory.getLog(ProcessesConfig.class);

    private String groupName;
    private String pathToExecutable;
    private String jre = System.getProperty("java.home");
    private String classpath = System.getProperty("java.class.path");
    private String jvmargs;
    private String mainclass;
    private String args;
    private OutputMode stdOutMode = OutputMode.INLINE;
    private OutputMode stdErrMode = OutputMode.INLINE;
    private int initialJmxPort = -1;
    private int initialDebugPort = -1;
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
    private String propertyTemplateName;

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

    public void setJmxPort(int jmxPort) {
        if (initialJmxPort == -1) {
            initialJmxPort = jmxPort;
        }
        this.jmxPort = jmxPort;
    }

    public void resetJmxPort() {
        jmxPort = initialJmxPort;
    }

    public void incrementJmxPort() {
        jmxPort++;
    }

    public int getDebugPort() {
        return debugPort;
    }

    public void setDebugPort(int debugPort) {
        if (initialDebugPort == -1) {
            initialDebugPort = debugPort;
        }
        this.debugPort = debugPort;
    }

    public void resetDebugPort() {
        debugPort = initialDebugPort;
    }

    public void incrementDebugPort() {
        debugPort++;
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

    public String getPropertyTemplateName() {
        return propertyTemplateName;
    }

    public void setPropertyTemplateName(final String propertyTemplateName) {
        this.propertyTemplateName = propertyTemplateName;
    }

    public boolean isValid() {
        boolean valid = true;
        if ( ! isSet(groupName)) {
            valid = logInvalidConfig("config groupName was null or empty");
        } 
        
        if ( isJavaProcess() ) {
            //some properties are mandatory for java processes
            if ( jre == null || ! new File(jre).isDirectory() ) {
                valid = logInvalidConfig("jre property is null or jre path does not exist");
            } else if ( ! isSet(classpath) ) {
                valid = logInvalidConfig("classpath was null");
            } else if ( ! isSet(mainclass) ) {
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
        if (isSet(mainclass)) {
            valid = logInvalidConfig("Cannot the mainclass property for non-java process configured with pathToExecutable");        
        } else if (isSet(jvmargs) ) {
            valid = logInvalidConfig("Cannot set jvmargs property for non-java process configured with pathToExecutable");
        }
        return valid;  
    }

    public boolean isJavaProcess() {
        return ! isSet(pathToExecutable);
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
    
    @Override
    public String toString() {
        return "ProcessesConfig{" +
                "groupName='" + groupName + '\'' +
                ", pathToExecutable='" + pathToExecutable + '\'' +
                ", jre='" + jre + '\'' +
                ", classpath='" + classpath + '\'' +
                ", jvmargs='" + jvmargs + '\'' +
                ", mainclass='" + mainclass + '\'' +
                ", args='" + args + '\'' +
                ", stdOutMode=" + stdOutMode  + '\'' +
                ", stdErrMode=" + stdErrMode   + '\'' + 
                ", jmxPort=" + jmxPort  + '\'' +
                ", debugPort=" + debugPort  + '\'' +
                ", terminateWaitTime=" + terminateWaitTime  + '\'' +
                ", logDirectory='" + logDirectory + '\'' +
                ", appendToLogs=" + appendToLogs  + '\'' +
                ", createLogDir=" + createLogDir  + '\'' +
                ", processCheckDelay=" + processCheckDelay  + '\'' +
                '}';
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
