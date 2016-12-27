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
package org.chorusbdd.chorus.stepserver.config;

import org.chorusbdd.chorus.handlerconfig.configbean.AbstractConfigBeanFactory;
import org.chorusbdd.chorus.handlerconfig.configbean.ConfigBeanFactory;
import org.chorusbdd.chorus.handlerconfig.configbean.ConfigBeanValidator;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;

import java.util.Map;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: GA2EBBU
 * Date: 21/09/12
 * Time: 11:22
 * To change this template use File | Settings | File Templates.
 */
public class StepServerConfigBeanFactory extends AbstractConfigBeanFactory implements ConfigBeanFactory<StepServerConfigBuilder> {

    private static final String stepTimeoutSeconds = "stepTimeoutSeconds";
    private static final String clientConnectTimeoutSeconds = "clientConnectTimeoutSeconds";
    private static final String port = "port";
    private static final String scope = "scope";

    private ChorusLog log = ChorusLogFactory.getLog(StepServerConfigBeanFactory.class);

    public StepServerConfigBuilder createConfig(Properties p, String configName) {
        StepServerConfigBuilder c = new StepServerConfigBuilder();
        setProperties(p, c);
        c.setConfigName(configName);
        return c;
    }

    public ConfigBeanValidator<StepServerConfig> createValidator(StepServerConfigBuilder config) {
        return new StepServerConfigBeanValidator();
    }

    private void setProperties(Properties p, StepServerConfigBuilder c) {
        for (Map.Entry prop : p.entrySet()) {
            String key = prop.getKey().toString();
            String value = prop.getValue().toString();
            if (stepTimeoutSeconds.equals(key)) {
                c.setStepTimeoutSeconds(parseIntProperty(value, stepTimeoutSeconds));
            } else if (clientConnectTimeoutSeconds.equals(key)) {
                c.setClientConnectTimeoutSeconds(parseIntProperty(value, clientConnectTimeoutSeconds));
            } else if (port.equals(key)) {
                c.setPort(parseIntProperty(value, port));
            } else if (scope.equals(key)) {
                c.setScope(parseScope(value));
            } else {
                log.warn("Ignoring property " + key + " which is not a supported StepServer handler property");
            }
        }
    }

    public Properties getProperties(StepServerConfig processConfig) {
        Properties p = new Properties();
        p.setProperty(port, String.valueOf(processConfig.getPort()));
        p.setProperty(stepTimeoutSeconds, String.valueOf(processConfig.getStepTimeoutSeconds()));
        return p;
    }

}
