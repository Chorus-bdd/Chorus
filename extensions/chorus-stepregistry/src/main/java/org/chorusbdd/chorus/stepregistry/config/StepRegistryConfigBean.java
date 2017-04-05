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
package org.chorusbdd.chorus.stepregistry.config;

import org.chorusbdd.chorus.annotations.Scope;

/**
 * An immutable runtime config for a process
 */
public class StepRegistryConfigBean implements StepRegistryConfig {

    private final String configName;
    private final int stepTimeoutSeconds;
    private final int port;
    private final Scope scope;
    private final int clientConnectTimeoutSeconds;

    public StepRegistryConfigBean(String configName, int stepTimeoutSeconds, int clientConnectTimeoutSeconds, int port, Scope scope) {
        this.configName = configName;
        this.stepTimeoutSeconds = stepTimeoutSeconds;
        this.clientConnectTimeoutSeconds = clientConnectTimeoutSeconds;
        this.port = port;
        this.scope = scope;
    }

    public int getStepTimeoutSeconds() {
        return stepTimeoutSeconds;
    }

    @Override
    public int getClientConnectTimeoutSeconds() {
        return clientConnectTimeoutSeconds;
    }

    @Override
    public int getPort() {
        return port;
    }

    public String getConfigName() {
        return configName;
    }

    @Override
    public Scope getScope() {
        return scope;
    }

    @Override
    public String toString() {
        return "StepRegistryConfigBean{" +
            "configName='" + configName + '\'' +
            ", clientConnectTimeoutSeconds=" + clientConnectTimeoutSeconds +
            ", stepTimeoutSeconds=" + stepTimeoutSeconds +
            ", port=" + port +
            ", scope=" + scope +
            '}';
    }
}
