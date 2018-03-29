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

import org.chorusbdd.chorus.annotations.Scope;
import org.chorusbdd.chorus.handlerconfig.configbean.HandlerConfigBuilder;

public class WebSocketsConfigBuilder implements HandlerConfigBuilder<WebSocketsConfigBuilder, WebSocketsConfig>, WebSocketsConfig {

    private String configName;
    private int stepTimeoutSeconds = 60;
    private int port = 9080;
    private Scope scope = Scope.SCENARIO;
    private int clientConnectTimeoutSeconds = 60;

    @Override
    public String getConfigName() {
        return configName;
    }

    public WebSocketsConfigBuilder setConfigName(String configName) {
        this.configName = configName;
        return this;
    }

    @Override
    public int getStepTimeoutSeconds() {
        return stepTimeoutSeconds;
    }

    public void setStepTimeoutSeconds(int stepTimeoutSeconds) {
        this.stepTimeoutSeconds = stepTimeoutSeconds;
    }

    @Override
    public int getClientConnectTimeoutSeconds() {
        return clientConnectTimeoutSeconds;
    }

    public void setClientConnectTimeoutSeconds(int clientConnectTimeoutSeconds) {
        this.clientConnectTimeoutSeconds = clientConnectTimeoutSeconds;
    }

    @Override
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public WebSocketsConfigBean build() {
        return new WebSocketsConfigBean(configName, stepTimeoutSeconds, clientConnectTimeoutSeconds, port, scope);
    }

}