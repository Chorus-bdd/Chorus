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
package org.chorusbdd.chorus.websockets.config;

import org.chorusbdd.chorus.annotations.Scope;
import org.chorusbdd.chorus.handlerconfig.configproperty.ConfigProperty;
import org.chorusbdd.chorus.handlerconfig.configproperty.ConfigValidator;
import org.chorusbdd.chorus.handlerconfig.configproperty.ConfigValidatorException;

/**
 * An immutable runtime config for a process
 */
public class WebSocketsConfigBean implements WebSocketsConfig {

    private String configName;
    private int stepTimeoutSeconds;
    private int port;
    private Scope scope;
    private int clientConnectTimeoutSeconds;

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    @Override
    public int getPort() {
        return port;
    }

    @ConfigProperty(
            name = "port",
            description = "Which local port the web socket server should listen on",
            defaultValue = "9080",
            validationPattern = "\\d+",
            order = 10
    )
    public void setPort(int port) {
        this.port = port;
    }

    public String getConfigName() {
        return configName;
    }

    @ConfigProperty(
            name = "stepTimeoutSeconds",
            description = "How long the Chorus interpreter should wait for a result after executing a step on a web socket client before failing the step",
            defaultValue = "60",
            validationPattern = "\\d+",
            order = 20
            
    )
    public void setStepTimeoutSeconds(int stepTimeoutSeconds) {
        this.stepTimeoutSeconds = stepTimeoutSeconds;
    }

    public int getStepTimeoutSeconds() {
        return stepTimeoutSeconds;
    }

    @ConfigProperty(
            name = "clientConnectTimeoutSeconds",
            description = "How long the Chorus interpreter should wait to receive a connection from a client before failing the connection step",
            defaultValue = "60",
            validationPattern = "\\d+",
            order = 30
    )
    public void setClientConnectTimeoutSeconds(int clientConnectTimeoutSeconds) {
        this.clientConnectTimeoutSeconds = clientConnectTimeoutSeconds;
    }

    @Override
    public int getClientConnectTimeoutSeconds() {
        return clientConnectTimeoutSeconds;
    }

    @ConfigProperty(
            name = "scope",
            description = "Whether the web socket should be closed at the end of each scenario, or at the end of the feature",
            defaultValue = "SCENARIO",
            order = 40
    )
    public void setScope(Scope scope) {
        this.scope = scope;
    }

    @Override
    public Scope getScope() {
        return scope;
    }

    @ConfigValidator
    public void checkValid() {
        if ( getPort() == 0) {
            throw new ConfigValidatorException("port cannot be 0");
        } else if ( getStepTimeoutSeconds() < 1) {
            throw new ConfigValidatorException("stepTimeoutSeconds was less than 1");
        } else if ( getScope() == null) {
            throw new ConfigValidatorException("scope was not set");
        } else if ( getClientConnectTimeoutSeconds() < 1) {
            throw new ConfigValidatorException("client connect timeout seconds cannot be < 1");
        }
    }

    @Override
    public String toString() {
        return "WebSocketsConfigBean{" +
            "configName='" + configName + '\'' +
            ", clientConnectTimeoutSeconds=" + clientConnectTimeoutSeconds +
            ", stepTimeoutSeconds=" + stepTimeoutSeconds +
            ", port=" + port +
            ", scope=" + scope +
            '}';
    }
}
