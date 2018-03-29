/**
 * MIT License
 *
 * Copyright (c) 2018 Chorus BDD Organisation.
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

import org.chorusbdd.chorus.handlerconfig.configbean.AbstractConfigBuilderFactory;
import org.chorusbdd.chorus.handlerconfig.configbean.ConfigBuilderFactory;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.util.ChorusException;

import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: Nick E
 * Date: 21/09/12
 * Time: 11:22
 * To change this template use File | Settings | File Templates.
 */
public class ProcessesConfigBuilderFactory extends AbstractConfigBuilderFactory<ProcessesConfigBuilder> implements ConfigBuilderFactory<ProcessesConfigBuilder> {

    public static final String pathToExecutable = "pathToExecutable";
    public static final String jre = "jre";
    public static final String classpath = "classpath";
    public static final String args = "args";
    public static final String jvmargs = "jvmargs";
    public static final String mainclass = "mainclass";
    public static final String remotingPort = "remotingPort";
    public static final String debugPort = "debugPort";
    public static final String terminateWaitTime = "terminateWaitTime";
    public static final String logging = "logging";
    public static final String logDirectory = "logDirectory";
    public static final String appendToLogs = "appendToLogs";
    public static final String createLogDir = "createLogDir";
    public static final String processCheckDelay = "processCheckDelay";
    public static final String stdErrMode = "stdErrMode";
    public static final String stdOutMode = "stdOutMode";
    public static final String readTimeoutSeconds = "readTimeoutSeconds";
    public static final String scope = "scope";
    public static final String enabled = "enabled";


    private ChorusLog log = ChorusLogFactory.getLog(ProcessesConfigBuilderFactory.class);

    public ProcessesConfigBuilder createBuilder() {
        return new ProcessesConfigBuilder();
    }

    protected void setProperties(Properties p, ProcessesConfigBuilder c) {
        for (Map.Entry prop : p.entrySet()) {
            String key = prop.getKey().toString();
            String value = prop.getValue().toString();
            if (pathToExecutable.equals(key)) {
                c.setPathToExecutable(value);
            } else if (jre.equals(key)) {
                c.setJre(value);
            } else if (classpath.equals(key)) {
                c.setClasspath(value);
            } else if (args.equals(key)) {
                c.setArgs(value);
            } else if (jvmargs.equals(key)) {
                c.setJvmargs(value);
            } else if (mainclass.equals(key)) {
                c.setMainclass(value);
            } else if ("jmxPort".equals(key)) {
                //jmxport is is deprecated, use remotingPort
                int jmxport = parseIntProperty(value, "jmxPort");
                c.setRemotingPort(jmxport);
            } else if (remotingPort.equals(key)) {
                int rp = parseIntProperty(value, remotingPort);
                c.setRemotingPort(rp);
            } else if (debugPort.equals(key)) {
                int dp = parseIntProperty(value, debugPort);
                c.setDebugPort(dp);
            } else if (terminateWaitTime.equals(key)) {
                c.setTerminateWaitTime(parseIntProperty(value, terminateWaitTime));
            } else if (logging.equals(key)) {
                //we still support logging property as an alternative to stdOutMode and stdErrMode
                //if true, both process std out and error go to a file, if false inline
                Boolean b = parseBooleanProperty(value, logging);
                OutputMode m = b ? OutputMode.FILE : OutputMode.INLINE;
                c.setStdErrMode(m);
                c.setStdOutMode(m);
            } else if (logDirectory.equals(key)) {
                c.setLogDirectory(value);
            } else if (appendToLogs.equals(key)) {
                c.setAppendToLogs(parseBooleanProperty(value, appendToLogs));
            } else if (createLogDir.equals(key)) {
                c.setCreateLogDir(parseBooleanProperty(value, createLogDir));
            } else if ( processCheckDelay.equals(key)) {
                c.setProcessCheckDelay(parseIntProperty(value, processCheckDelay));
            } else if ( stdErrMode.equals(key)) {
                c.setStdErrMode(parseOutputMode(value, stdErrMode));
            } else if ( stdOutMode.equals(key)) {
                c.setStdOutMode(parseOutputMode(value, stdOutMode));
            } else if ("readAheadBufferSize".equals(key)) {
                log.warn("Property readAheadBufferSize is no longer supported from version 2.x");
            } else if (readTimeoutSeconds.equals(key)) {
                c.setReadTimeoutSeconds(parseIntProperty(value, readTimeoutSeconds));
            } else if (scope.equals(key)) {
                c.setProcessScope(parseScope(value));
            } else if (enabled.equals(key)) {
                c.setEnabled(parseBooleanProperty(value, enabled));
            } else {
                log.warn("Ignoring property " + key + " which is not a supported Processes handler property");
            }
        }

    }

    public Properties getProperties(ProcessManagerConfig processConfig) {
        Properties p = new Properties();
        addIfSet(p, pathToExecutable, processConfig.getPathToExecutable());
        addIfSet(p, jre, processConfig.getJre());
        addIfSet(p, classpath, processConfig.getClasspath());
        addIfSet(p, args, processConfig.getArgs());
        addIfSet(p, jvmargs, processConfig.getJvmargs());
        addIfSet(p, mainclass, processConfig.getMainclass());
        p.setProperty(remotingPort, String.valueOf(processConfig.getRemotingPort()));
        p.setProperty(debugPort, String.valueOf(processConfig.getDebugPort()));
        p.setProperty(terminateWaitTime, String.valueOf(processConfig.getTerminateWaitTime()));
        addIfSet(p, logDirectory, processConfig.getLogDirectory());
        p.setProperty(appendToLogs, String.valueOf(processConfig.isAppendToLogs()));
        p.setProperty(createLogDir, String.valueOf(processConfig.isCreateLogDir()));
        p.setProperty(processCheckDelay, String.valueOf(processConfig.getProcessCheckDelay()));
        p.setProperty(stdErrMode, processConfig.getStdErrMode().name());
        p.setProperty(stdOutMode, processConfig.getStdOutMode().name());
        p.setProperty(readTimeoutSeconds, String.valueOf(processConfig.getReadTimeoutSeconds()));
        p.setProperty(scope, processConfig.getProcessScope().name());
        return p;
    }

    private void addIfSet(Properties p, String propertyKey, String value) {
        if ( value != null) {
            p.setProperty(propertyKey, value);
        }
    }


    private static OutputMode parseOutputMode(String value, String propertyName) {
        OutputMode l = OutputMode.valueOf(value.toUpperCase().trim());
        if ( l == null) {
            throw new ChorusException(
                "Failed to parse property " + propertyName + " with value '" + value + "', should be one of: "
                + Arrays.asList(OutputMode.values())
            );
        }
        return l;
    }

}
