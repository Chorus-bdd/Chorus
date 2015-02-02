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
package org.chorusbdd.chorus.handlers.remoting;

import org.chorusbdd.chorus.annotations.Scope;
import org.chorusbdd.chorus.remoting.manager.RemotingManagerConfig;

/**
* Created by IntelliJ IDEA.
* User: Nick Ebbutt
* Date: 18/09/12
* Time: 08:20
*
* Configuration for a remote component used by the RemotingHandler
* A builder for RemotingInfo used by the RemotingManager to control remoting
*/
public class RemotingConfig implements RemotingManagerConfig {

    //default protocol to jmx so we don't have to specify it if loading props from db
    private String protocol = "jmx";
    private String configName;
    private String host;
    private int port;
    private int connectionAttempts = 40;
    private int connectionAttemptMillis = 250;
    private Scope scope = Scope.SCENARIO;

    public RemotingManagerConfig buildRemotingManagerConfig() {
        return new RuntimeRemotingConfig(
            protocol,
            configName,
            host,
            port,
            connectionAttempts,
            connectionAttemptMillis,
            scope
        );
    }

    public String getProtocol() {
        return protocol;
    }

    public RemotingConfig setProtocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    public String getConfigName() {
        return configName;
    }

    public RemotingConfig setConfigName(String configName) {
        this.configName = configName;
        return this;
    }

    public int getConnectionAttemptMillis() {
        return connectionAttemptMillis;
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public RemotingConfig setConnectionAttemptMillis(int connectionAttemptMillis) {
        this.connectionAttemptMillis = connectionAttemptMillis;
        return this;
    }

    public int getConnectionAttempts() {
        return connectionAttempts;
    }

    public RemotingConfig setConnnectionAttempts(int connectionAttempts) {
        this.connectionAttempts = connectionAttempts;
        return this;
    }

    public String getHost() {
        return host;
    }

    public RemotingConfig setHost(String host) {
        this.host = host;
        return this;
    }

    public int getPort() {
        return port;
    }

    public RemotingConfig setPort(int port) {
        this.port = port;
        return this;
    }

    @Override
    public String toString() {
        return "RemotingConfig{" +
                "protocol='" + protocol + '\'' +
                ", configName='" + configName + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", connectionAttempts=" + connectionAttempts +
                ", connectionAttemptMillis=" + connectionAttemptMillis +
                ", scope=" + scope +
                '}';
    }

    /**
     * An immutable remoting config
     */
    class RuntimeRemotingConfig implements RemotingManagerConfig {

        private final String protocol;
        private final String configName;
        private final String host;
        private final int port;
        private final int connectionAttempts;
        private final int connectionAttemptMillis;
        private final Scope scope;

        RuntimeRemotingConfig(String protocol, String configName, String host, int port, int connectionAttempts, int connectionAttemptMillis, Scope scope) {
            this.protocol = protocol;
            this.configName = configName;
            this.host = host;
            this.port = port;
            this.connectionAttempts = connectionAttempts;
            this.connectionAttemptMillis = connectionAttemptMillis;
            this.scope = scope;
        }

        public String getProtocol() {
            return protocol;
        }

        public String getConfigName() {
            return configName;
        }

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }

        public int getConnectionAttempts() {
            return connectionAttempts;
        }

        public int getConnectionAttemptMillis() {
            return connectionAttemptMillis;
        }

        public Scope getScope() {
            return scope;
        }

        @Override
        public String toString() {
            return "RuntimeRemotingConfig{" +
                    "protocol='" + protocol + '\'' +
                    ", configName='" + configName + '\'' +
                    ", host='" + host + '\'' +
                    ", port=" + port +
                    ", connectionAttempts=" + connectionAttempts +
                    ", connectionAttemptMillis=" + connectionAttemptMillis +
                    ", scope=" + scope +
                    '}';
        }
    }

}
