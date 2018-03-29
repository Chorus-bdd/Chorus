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
package org.chorusbdd.chorus.websockets.config;

import org.chorusbdd.chorus.handlerconfig.configbean.AbstractConfigBuilderFactory;
import org.chorusbdd.chorus.handlerconfig.configbean.ConfigBuilderFactory;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;

import java.util.Map;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: Nick E
 * Date: 21/09/12
 * Time: 11:22
 * To change this template use File | Settings | File Templates.
 */
public class WebSocketsConfigBuilderFactory extends AbstractConfigBuilderFactory<WebSocketsConfigBuilder> implements ConfigBuilderFactory<WebSocketsConfigBuilder> {

    private static final String stepTimeoutSeconds = "stepTimeoutSeconds";
    private static final String clientConnectTimeoutSeconds = "clientConnectTimeoutSeconds";
    private static final String port = "port";
    private static final String scope = "scope";

    private ChorusLog log = ChorusLogFactory.getLog(WebSocketsConfigBuilderFactory.class);

    public WebSocketsConfigBuilder createBuilder() {
        return new WebSocketsConfigBuilder();
    }

    protected void setProperties(Properties p, WebSocketsConfigBuilder c) {
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
                log.warn("Ignoring property " + key + " which is not a supported WebSocketsManagerImpl handler property");
            }
        }
    }
    
}
