/**
 * MIT License
 *
 * Copyright (c) 2019 Chorus BDD Organisation.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.chorusbdd.chorus.processes.manager.config;

import org.chorusbdd.chorus.annotations.Scope;
import org.chorusbdd.chorus.handlerconfig.configproperty.ConfigProperty;
import org.chorusbdd.chorus.handlerconfig.configproperty.ConfigValidator;
import org.chorusbdd.chorus.handlerconfig.configproperty.ConfigValidatorException;

import java.io.File;
import java.util.Properties;

/**
 * An immutable runtime config for a process
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
public class ProcessConfigBean implements ProcessManagerConfig {
    
    private static final String JAVA_HOME = System.getProperty("java.home");
    private static final String JAVA_CLASS_PATH = System.getProperty("java.class.path");

    public static final String SCOPE_PROPERTY = "scope";
    public static final String REMOTING_PORT_PROPERTY = "remotingPort";
    public static final String PATH_TO_EXECUTABLE_PROPERTY = "pathToExecutable";
    public static final String JRE_PROPERTY = "jre";
    public static final String CLASSPATH_PROPERTY = "classpath";
    public static final String JVMARGS_PROPERTY = "jvmargs";
    public static final String MAINCLASS_PROPERTY = "mainclass";
    public static final String ARGS_PROPERTY = "args";
    public static final String STD_OUT_MODE_PROPERTY = "stdOutMode";
    public static final String STD_ERR_MODE_PROPERTY = "stdErrMode";
    public static final String LOGGING_PROPERTY = "logging";
    public static final String DEBUG_PORT_PROPERTY = "debugPort";
    public static final String TERMINATE_WAIT_TIME_PROPERTY = "terminateWaitTime";
    public static final String LOG_DIRECTORY_PROPERTY = "logDirectory";
    public static final String APPEND_TO_LOGS_PROPERTY = "appendToLogs";
    public static final String CREATE_LOG_DIR_PROPERTY = "createLogDir";
    public static final String PROCESS_CHECK_DELAY_PROPERTY = "processCheckDelay";
    public static final String READ_TIMEOUT_SECONDS_PROPERTY = "readTimeoutSeconds";
    public static final String ENABLED_PROPERTY = "enabled";

    private String configName;
    private String pathToExecutable;
    private String jre;
    private String classpath;
    private String jvmargs;
    private String mainclass;
    private String args;
    private OutputMode stdOutMode;
    private OutputMode stdErrMode;
    private int remotingPort;
    private int debugPort;
    private int terminateWaitTime;
    private String logDirectory;
    private boolean appendToLogs;
    private boolean createLogDir; //whether to auto create
    private int processCheckDelay;
    private int readTimeoutSeconds;
    private Scope processScope;
    private boolean enabled;

    /**
     * Invoked by reflection
     */
    public ProcessConfigBean() {}
    
    public ProcessConfigBean(String configName, String pathToExecutable, String jre, String classpath, String jvmargs, String mainclass,
                             String args, OutputMode stdOutMode, OutputMode stdErrMode, int remotingPort, int debugPort, int terminateWaitTime,
                             String logDirectory, boolean appendToLogs, boolean createLogDir, int processCheckDelay,
                             int readTimeoutSeconds, Scope processScope, boolean enabled) {
        this.configName = configName;
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

    @Override
    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    @Override
    public String getMainclass() {
        return mainclass;
    }

    @ConfigProperty(
        name= MAINCLASS_PROPERTY,
        description="The class containing the main method which starts up your component (java processes only)",
        mandatory = false,
        order = 10
    )
    public void setMainclass(String mainclass) {
        this.mainclass = mainclass;
    }

    @Override
    public String getClasspath() {
        return classpath == null ? JAVA_CLASS_PATH : classpath;
    }

    @ConfigProperty(
        name= CLASSPATH_PROPERTY,
        description="The classpath to use when executing a Java process. If not set, the Chorus interpreter's classpath will be used",
        mandatory = false,
        order = 20
    )
    public void setClasspath(String classpath) {
        this.classpath = classpath;
    }

    @Override
    public String getJre() {
        return jre == null ? JAVA_HOME : jre;
    }

    @ConfigProperty(
        name= JRE_PROPERTY,
        description="Path to the JRE to be used when executing a Java process. If not set, the Chorus interpreter's JVM will be used",
        mandatory = false,
        order = 30
    )
    public void setJre(String jre) {
        this.jre = jre;
    }

    @Override
    public String getJvmargs() {
        return jvmargs == null ? "" : jvmargs;
    }

    @ConfigProperty(
        name= JVMARGS_PROPERTY,
        description="System properties (-D switches) to use when executing a Java process",
        mandatory = false,
        order = 40
    )
    public void setJvmargs(String jvmargs) {
        this.jvmargs = jvmargs;
    }

    @Override
    public String getArgs() {
        return args == null ? "" : args;
    }

    @ConfigProperty(
        name= ARGS_PROPERTY,
        description="Arguments to pass to the process",
        mandatory = false,
        order = 50
    )
    public void setArgs(String args) {
        this.args = args;
    }

    @Override
    public String getPathToExecutable() {
        return pathToExecutable;
    }

    @ConfigProperty(
        name= PATH_TO_EXECUTABLE_PROPERTY,
        description="Path to a native executable process or script, the path may be absolute or relative to the feature directory. This property and the mainclass property are mutually exclusive - you should either one but not both",
        mandatory = false,
        order = 60
    )
    public void setPathToExecutable(String pathToExecutable) {
        this.pathToExecutable = pathToExecutable;
    }

    @Override
    public OutputMode getStdOutMode() {
        return stdOutMode == null ? OutputMode.FILE : stdOutMode;
    }

    @ConfigProperty(
        name= STD_OUT_MODE_PROPERTY,
        description="What do to with standard output stream from started process, one of INLINE (combine with interpreter stdout), FILE (write output to file). Other values are deprecated",
        validationPattern = "(?i)FILE|INLINE|CAPTURED|CAPTUREDWITHLOG",
        mandatory = false, //logging prop may be set instead
        order = 70
    )
    public void setStdOutMode(OutputMode stdOutMode) {
        this.stdOutMode = stdOutMode;
    }

    @Override
    public OutputMode getStdErrMode() {
        return stdErrMode == null ? OutputMode.FILE : stdErrMode;
    }

    @ConfigProperty(
        name= STD_ERR_MODE_PROPERTY,
        description="What do to with standard error stream from started process, one of INLINE (combine with interpreter stderr) or FILE (write output to file). Other values are deprecated",
        validationPattern = "(?i)FILE|INLINE|CAPTURED|CAPTUREDWITHLOG",
        mandatory = false, //logging prop may be set instead
        order = 80
    )
    public void setStdErrMode(OutputMode stdErrMode) {
        this.stdErrMode = stdErrMode;
    }


    @ConfigProperty(
        name= LOGGING_PROPERTY,
        description="If this property is set true, it will switch stdOutMode and stdErrorMode to FILE. If false then both will be INLINE. Leave it unset if you wish to set the stdOutMode and stdErrorMode individually",
        validationPattern = "true|false",
        mandatory = false,    //may set stdErr or stdOut mode directly instead
        order = 90
    )
    public void setLogging(boolean isLoggingEnabled) {
        //we still support logging property as an alternative to stdOutMode and stdErrMode
        //if true, both process std out and error go to a file, if false inline
        OutputMode m = isLoggingEnabled ? OutputMode.FILE : OutputMode.INLINE;
        setStdErrMode(m);
        setStdOutMode(m);
    }
    
    @Override
    public int getRemotingPort() {
        return remotingPort;
    }

    @ConfigProperty(
        name= REMOTING_PORT_PROPERTY,
        description="Port on which to start the JMX remoting service. This is required when you want to use Chorus' Remoting features to connect to the process being started using JMX. Setting this property will add java system properties to turn on the JMX platform service. (java processes only), -1 to disable",
        validationPattern = "(-?)\\d+",
        defaultValue = "-1",
        order = 100
    )
    public void setRemotingPort(int remotingPort) {
        this.remotingPort = remotingPort;
    }

    @Override
    public int getDebugPort() {
        return debugPort;
    }

    @ConfigProperty(
        name= DEBUG_PORT_PROPERTY,
        description="Enable the debugger when starting the jvm and set it up to listen for connections on the port specified (java processes only), -1 to disable",
        validationPattern = "(-?)\\d+",
        defaultValue = "-1",
        order = 110
    )
    public void setDebugPort(int debugPort) {
        this.debugPort = debugPort;
    }

    @Override
    public int getTerminateWaitTime() {
        return terminateWaitTime;
    }

    @ConfigProperty(
        name= TERMINATE_WAIT_TIME_PROPERTY,
        description="Maximum time to wait for a process to terminate in seconds",
        validationPattern = "\\d+",
        defaultValue = "30",
        order = 120
    )
    public void setTerminateWaitTime(int terminateWaitTime) {
        this.terminateWaitTime = terminateWaitTime;
    }

    @Override
    public String getLogDirectory() {
        return logDirectory;
    }

    @ConfigProperty(
        name= LOG_DIRECTORY_PROPERTY,
        description="If you turn logging on, use this property to set the log directory. If not specified a logs directory will be created in the same directory as the feature file. May be an absolute path or a path relative to the working directory",
        mandatory = false,
        order = 130
    )
    public void setLogDirectory(String logDirectory) {
        this.logDirectory = logDirectory;
    }

    @Override
    public boolean isAppendToLogs() {
        return appendToLogs;
    }

    @ConfigProperty(
        name= APPEND_TO_LOGS_PROPERTY,
        description="Whether to append to or overwrite log files",
        defaultValue = "false",
        validationPattern = "true|false",
        order = 140
    )
    public void setAppendToLogs(boolean appendToLogs) {
        this.appendToLogs = appendToLogs;
    }

    @Override
    public boolean isCreateLogDir() {
        return createLogDir;
    }

    @ConfigProperty(
        name= CREATE_LOG_DIR_PROPERTY,
        description="Whether to auto-create the log directory if it does not exist",
        defaultValue = "true",
        validationPattern = "true|false",
        order = 150
    )
    public void setCreateLogDir(boolean createLogDir) {
        this.createLogDir = createLogDir;
    }

    @Override
    public int getProcessCheckDelay() {
        return processCheckDelay;
    }

    @ConfigProperty(
        name= PROCESS_CHECK_DELAY_PROPERTY,
        description="Milliseconds after which to check started process is still running or fail the start process step. Longer values add better detection of immediate process start failures but incur an increased delay before subsequent steps run",
        defaultValue = "500",
        validationPattern = "(-?)\\d+",
        order = 160
    )
    public void setProcessCheckDelay(int processCheckDelay) {
        this.processCheckDelay = processCheckDelay;
    }

    @Override
    public int getReadTimeoutSeconds() {
        return readTimeoutSeconds;
    }

    @ConfigProperty(
        name= READ_TIMEOUT_SECONDS_PROPERTY,
        description="When matching a pattern against process output set the max time to wait for a match",
        defaultValue = "10",
        validationPattern = "\\d+",
        order = 170
    )
    public void setReadTimeoutSeconds(int readTimeoutSeconds) {
        this.readTimeoutSeconds = readTimeoutSeconds;
    }

    @Override
    public Scope getProcessScope() {
        return processScope;
    }

    @ConfigProperty(
        name= SCOPE_PROPERTY,
        description="Whether the process should be shut down at the end of the scenario or the end of the feature." +
                " this will be set automatically to FEATURE for processes started during 'Feature-Start:' if not provided, otherwise Scenario",
        defaultValue = "SCENARIO",
        mandatory = false,
        order = 180
    )
    public void setProcessScope(Scope processScope) {
        this.processScope = processScope;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @ConfigProperty(
        name= ENABLED_PROPERTY,
        description="This property can be set to true to disable process start up when running in certain profiles",
        defaultValue = "true",
        validationPattern = "true|false",
        order = 190
    )
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isJavaProcess() {
        return ! isSet(pathToExecutable);
    }
    
    private boolean isSet(String propertyValue) {
        return propertyValue != null && propertyValue.trim().length() > 0;
    }

    public boolean isRemotingConfigDefined() {
        return remotingPort != -1;
    }


    @ConfigValidator
    public void checkValid() {
        if ( isJavaProcess() ) {
            //some properties are mandatory for java processes
            checkPropertiesForJavaProcess();
        } else {
            //some properties should not be used for non-java processes
            checkPropertiesForNativeProcess();
        }
    }

    private void checkPropertiesForJavaProcess() {
        String jre = getJre();
        if ( jre == null || ! new File(jre).isDirectory() ) {
            throw new ConfigValidatorException("jre property is null or jre path does not exist");
        } else if ( ! isSet(getClasspath()) ) {
            throw new ConfigValidatorException("classpath was null");
        } else if ( ! isSet(getMainclass()) ) {
            throw new ConfigValidatorException("main class was null or empty");
        }
    }

    private boolean checkPropertiesForNativeProcess() {
        boolean valid = true;
        if (isSet(getMainclass())) {
            throw new ConfigValidatorException("Cannot set the mainclass property for non-java process configured with pathToExecutable");
        } else if (isSet(getJvmargs()) ) {
            throw new ConfigValidatorException("Cannot set jvmargs property for non-java process configured with pathToExecutable");
        }
        return valid;
    }

    public static Properties convertToProperties(ProcessManagerConfig processConfig) {
        Properties p = new Properties();
        addIfSet(p, PATH_TO_EXECUTABLE_PROPERTY, processConfig.getPathToExecutable());
        addIfSet(p, JRE_PROPERTY, processConfig.getJre());
        addIfSet(p, CLASSPATH_PROPERTY, processConfig.getClasspath());
        addIfSet(p, ARGS_PROPERTY, processConfig.getArgs());
        addIfSet(p, JVMARGS_PROPERTY, processConfig.getJvmargs());
        addIfSet(p, MAINCLASS_PROPERTY, processConfig.getMainclass());
        p.setProperty(REMOTING_PORT_PROPERTY, String.valueOf(processConfig.getRemotingPort()));
        p.setProperty(DEBUG_PORT_PROPERTY, String.valueOf(processConfig.getDebugPort()));
        p.setProperty(TERMINATE_WAIT_TIME_PROPERTY, String.valueOf(processConfig.getTerminateWaitTime()));
        addIfSet(p, LOG_DIRECTORY_PROPERTY, processConfig.getLogDirectory());
        p.setProperty(APPEND_TO_LOGS_PROPERTY, String.valueOf(processConfig.isAppendToLogs()));
        p.setProperty(CREATE_LOG_DIR_PROPERTY, String.valueOf(processConfig.isCreateLogDir()));
        p.setProperty(PROCESS_CHECK_DELAY_PROPERTY, String.valueOf(processConfig.getProcessCheckDelay()));
        p.setProperty(STD_ERR_MODE_PROPERTY, processConfig.getStdErrMode().name());
        p.setProperty(STD_OUT_MODE_PROPERTY, processConfig.getStdOutMode().name());
        p.setProperty(READ_TIMEOUT_SECONDS_PROPERTY, String.valueOf(processConfig.getReadTimeoutSeconds()));
        p.setProperty(SCOPE_PROPERTY, processConfig.getProcessScope().name());
        return p;
    }

    private static void addIfSet(Properties p, String propertyKey, String value) {
        if ( value != null) {
            p.setProperty(propertyKey, value);
        }
    }

    
    public ProcessConfigBean copy() {
        return new ProcessConfigBean(
            configName, 
            pathToExecutable, 
            jre, 
            classpath, 
            jvmargs, 
            mainclass, 
            args, 
            stdOutMode, 
            stdErrMode, 
            remotingPort, 
            debugPort, 
            terminateWaitTime, 
            logDirectory, 
            appendToLogs, 
            createLogDir, 
            processCheckDelay, 
            readTimeoutSeconds, 
            processScope, 
            enabled);
    }
    
    
    @Override
    public String toString() {
        return "ProcessConfigBean{" +
            "configName='" + configName + '\'' +
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
            ", enabled=" + enabled +
            '}';
    }
}
