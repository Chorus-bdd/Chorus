package org.chorusbdd.chorus.remoting.manager;

import org.chorusbdd.chorus.annotations.Scope;
import org.chorusbdd.chorus.remoting.manager.RemotingManagerConfig;

/**
 * An immutable remoting config
 */
public class RemotingConfig implements RemotingManagerConfig {

    private final String protocol;
    private final String configName;
    private final String host;
    private final int port;
    private final int connectionAttempts;
    private final int connectionAttemptMillis;
    private final Scope scope;

    public RemotingConfig(String protocol, String configName, String host, int port, int connectionAttempts, int connectionAttemptMillis, Scope scope) {
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
