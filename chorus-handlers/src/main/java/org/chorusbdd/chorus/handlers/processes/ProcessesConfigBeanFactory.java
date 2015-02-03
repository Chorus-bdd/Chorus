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

import org.chorusbdd.chorus.processes.manager.config.OutputMode;
import org.chorusbdd.chorus.processes.manager.config.ProcessManagerConfig;
import org.chorusbdd.chorus.handlerconfig.configbean.AbstractConfigBeanFactory;
import org.chorusbdd.chorus.handlerconfig.configbean.ConfigBeanValidator;
import org.chorusbdd.chorus.handlerconfig.configbean.ConfigBeanFactory;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.processes.manager.config.ProcessManagerConfigBeanValidator;
import org.chorusbdd.chorus.util.ChorusException;

import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: GA2EBBU
 * Date: 21/09/12
 * Time: 11:22
 * To change this template use File | Settings | File Templates.
 */
public class ProcessesConfigBeanFactory extends AbstractConfigBeanFactory implements ConfigBeanFactory<ProcessesConfigBuilder> {

    private static ChorusLog log = ChorusLogFactory.getLog(ProcessesConfigBeanFactory.class);

    public ProcessesConfigBuilder createConfig(Properties p, String configName) {
        ProcessesConfigBuilder c = new ProcessesConfigBuilder();
        setProperties(p, c);
        c.setConfigName(configName);
        return c;
    }

    public ConfigBeanValidator<ProcessManagerConfig> createValidator(ProcessesConfigBuilder config) {
        return new ProcessManagerConfigBeanValidator();
    }

    private void setProperties(Properties p, ProcessesConfigBuilder c) {
        for (Map.Entry prop : p.entrySet()) {
            String key = prop.getKey().toString();
            String value = prop.getValue().toString();
            if ("pathToExecutable".equalsIgnoreCase(key)) {
                c.setPathToExecutable(value);
            } else if ("jre".equalsIgnoreCase(key)) {
                c.setJre(value);
            } else if ("classpath".equalsIgnoreCase(key)) {
                c.setClasspath(value);
            } else if ("args".equalsIgnoreCase(key)) {
                c.setArgs(value);
            } else if ("jvmargs".equalsIgnoreCase(key)) {
                c.setJvmargs(value);
            } else if ("mainclass".equalsIgnoreCase(key)) {
                c.setMainclass(value);
            } else if ("jmxport".equalsIgnoreCase(key)) {
                //jmxport is is deprecated, use remotingPort
                int jmxport = parseIntProperty(value, "jmxport");
                c.setRemotingPort(jmxport);
            } else if ("remotingPort".equalsIgnoreCase(key)) {
                int remotingPort = parseIntProperty(value, "remotingPort");
                c.setRemotingPort(remotingPort);
            } else if ("debugport".equalsIgnoreCase(key)) {
                int debugport = parseIntProperty(value, "debugport");
                c.setDebugPort(debugport);
            } else if ("terminateWaitTime".equalsIgnoreCase(key)) {
                c.setTerminateWaitTime(parseIntProperty(value, "terminateWaitTime"));
            } else if ("logging".equalsIgnoreCase(key)) {
                //we still support logging property as an alternative to stdOutMode and stdErrMode
                //if true, both process std out and error go to a file, if false inline
                Boolean b = parseBooleanProperty(value, "logging");
                OutputMode m = b ? OutputMode.FILE : OutputMode.INLINE;
                c.setStdErrMode(m);
                c.setStdOutMode(m);
            } else if ("logDirectory".equalsIgnoreCase(key)) {
                c.setLogDirectory(value);
            } else if ("appendToLogs".equalsIgnoreCase(key)) {
                c.setAppendToLogs(parseBooleanProperty(value, "appendToLogs"));
            } else if ("createLogDir".equalsIgnoreCase(key)) {
                c.setCreateLogDir(parseBooleanProperty(value, "createLogDir"));
            } else if ( "processCheckDelay".equalsIgnoreCase(key)) {
                c.setProcessCheckDelay(parseIntProperty(value, "processCheckDelay"));
            } else if ( "stdErrMode".equalsIgnoreCase(key)) {
                c.setStdErrMode(parseOutputMode(value, "stdErrMode"));
            } else if ( "stdOutMode".equalsIgnoreCase(key)) {
                c.setStdOutMode(parseOutputMode(value, "stdOutMode"));
            } else if ("readAheadBufferSize".equalsIgnoreCase(key)) {
                log.warn("Property readAheadBufferSize is no longer supported from version 2.x");
            } else if ("readTimeoutSeconds".equalsIgnoreCase(key)) {
                c.setReadTimeoutSeconds(parseIntProperty(value, "readTimeoutSeconds"));
            } else if ("scope".equalsIgnoreCase(key)) {
                c.setProcessScope(parseScope(value));
            } else {
                log.warn("Ignoring property " + key + " which is not a supported Processes handler property");
            }
        }
    }

    private static OutputMode parseOutputMode(String value, String propertyName) {
        OutputMode l = OutputMode.valueOf(value.toUpperCase().trim());
        if ( l == null) {
            throw new ChorusException(
                "Failed to parse property " + propertyName + " with value '" + value + "', shoud be one of: " 
                + Arrays.asList(OutputMode.values())
            );
        }
        return l;
    }

}
