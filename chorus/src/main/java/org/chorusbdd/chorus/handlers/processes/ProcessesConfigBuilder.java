/**
 *  Copyright (C) 2000-2012 The Software Conservancy and Original Authors.
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

import org.chorusbdd.chorus.handlers.util.config.AbstractHandlerConfig;
import org.chorusbdd.chorus.handlers.util.config.AbstractHandlerConfigBuilder;
import org.chorusbdd.chorus.handlers.util.config.HandlerConfigBuilder;
import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;

import java.util.Map;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: GA2EBBU
 * Date: 21/09/12
 * Time: 11:22
 * To change this template use File | Settings | File Templates.
 */
public class ProcessesConfigBuilder extends AbstractHandlerConfigBuilder implements HandlerConfigBuilder<ProcessesConfig> {

    private static ChorusLog log = ChorusLogFactory.getLog(ProcessesConfigBuilder.class);

    public ProcessesConfig createConfig(Properties p, Properties defaults) {
        ProcessesConfig c = new ProcessesConfig();
        setProperties(defaults, c);
        setProperties(p, c);
        return c;
    }

    private void setProperties(Properties p, ProcessesConfig c) {
        for (Map.Entry prop : p.entrySet()) {
            String key = prop.getKey().toString();
            String value = prop.getValue().toString();

            if ("name".equals(key)) {
                c.setName(value);
            } else if ("jre".equals(key)) {
                c.setJre(value);
            } else if ("classpath".equals(key)) {
                c.setClasspath(value);
            } else if ("args".equals(key)) {
                c.setArgs(value);
            } else if ("jvmargs".equals(key)) {
                c.setJvmargs(value);
            } else if ("mainclass".equals(key)) {
                c.setMainclass(value);
            } else if ("jmxport".equals(key)) {
                c.setJmxPort(parseIntProperty(value, "jmxport"));
            } else if ("debugport".equals(key)) {
                c.setDebugPort(parseIntProperty(value, "debugport"));
            } else if ("terminateWaitTime".equals(key)) {
                c.setTerminateWaitTime(parseIntProperty(value, "terminateWaitTime"));
            } else if ("logging".equals(key)) {
                c.setLogging(parseBooleanProperty(value, "logging"));
            } else if ("logDirectory".equals(key)) {
                c.setLogDirectory(value);
            } else {
                log.warn("Ignoring property " + key + " which is not a supported Processes handler property");
            }
        }
    }

}
