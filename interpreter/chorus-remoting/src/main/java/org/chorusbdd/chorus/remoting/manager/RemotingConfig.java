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
package org.chorusbdd.chorus.remoting.manager;

import org.chorusbdd.chorus.annotations.Scope;
import org.chorusbdd.chorus.handlerconfig.configproperty.ConfigProperty;
import org.chorusbdd.chorus.handlerconfig.configproperty.ConfigValidator;
import org.chorusbdd.chorus.handlerconfig.configproperty.ConfigValidatorException;

/**
 *  Remoting config
 */
public class RemotingConfig implements RemotingManagerConfig {

    private String protocol;
    private String configName;
    private String host;
    private int port;
    private int connectionAttempts;
    private int connectionAttemptMillis;
    private Scope scope;

    @Override
    public String getProtocol() {
        return protocol;
    }

    @ConfigProperty(
        name="protocol",
        description="Protocol to make connection (only JMX supported at present)",
        defaultValue = "jmx",
        validationPattern = "jmx"
    )
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    @Override
    public String getConfigName() {
        return configName;
    }
    
    public void setConfigName(String configName) {
        this.configName = configName;
    }

    @Override
    public String getHost() {
        return host;
    }

    @ConfigProperty(
        name="host",
        description="host where remote component is running",
        mandatory = false //instead can set 'connection' property
    )
    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public int getPort() {
        return port;
    }

    @ConfigProperty(
        name="port",
        description="port on which remote component's jmx service is listening for connections",
        validationPattern = "\\d+",
        mandatory = false  //instead can set 'connection' property
    )
    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public int getConnectionAttempts() {
        return connectionAttempts;
    }

    @ConfigProperty(
        name="connectionAttempts",
        description="Number of times to attempt connection",
        defaultValue = "40",
        validationPattern = "\\d+"
    )
    public void setConnectionAttempts(int connectionAttempts) {
        this.connectionAttempts = connectionAttempts;
    }

    @Override
    public int getConnectionAttemptMillis() {
        return connectionAttemptMillis;
    }

    @ConfigProperty(
        name="connectionAttemptMillis",
        description="Wait time between each connection attempt",
        defaultValue = "250",
        validationPattern = "\\d+"
    )
    public void setConnectionAttemptMillis(int connectionAttemptMillis) {
        this.connectionAttemptMillis = connectionAttemptMillis;
    }

    @Override
    public Scope getScope() {
        return scope;
    }

    @ConfigProperty(
        name="scope",
        description="Whether the remoting connection is closed at the end of the scenario or at the end of the feature. This will be defaulted to FEATURE for connections established during 'Feature-Start:' otherwise SCENARIO",
        defaultValue = "SCENARIO",
        validationPattern = Scope.SCOPE_MATCHING_PATTERN
    )
    public void setScope(Scope scope) {
        this.scope = scope;
    }
    
    @ConfigProperty(
        name="connection",
        description="A shorthand way of setting protocol host and port properties delimited by colon, e.g. jmx:myHost:myPort",
        validationPattern = "jmx:\\S+:\\d+",
        mandatory = false
    )
    public void setConnection(String connection) {
        String[] vals = String.valueOf(connection).split(":");
        if (vals.length != 3) {
            throw new ConfigValidatorException(
                "Could not parse remoting property 'connection', " +
                    "expecting a value in the form protocol:host:port"
            );
        }
        setProtocol(vals[0]);
        setHost(vals[1]);
        setPort(Integer.parseInt(vals[2]));
    }
    
    
    @ConfigValidator
    public void validate() {
        if (host == null) throw new ConfigValidatorException("host property must be set");
        if (port < 0) throw new ConfigValidatorException("port must be set and > 0");
        if (connectionAttempts < 0) throw new ConfigValidatorException("connectionAttempts must be set and > 0");
        if (connectionAttemptMillis < 0) throw new ConfigValidatorException("connectionAttemptMillis must be set and > 0");
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
}
