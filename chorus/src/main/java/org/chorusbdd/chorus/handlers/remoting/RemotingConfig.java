package org.chorusbdd.chorus.handlers.remoting;

import org.chorusbdd.chorus.handlers.util.config.HandlerConfig;

/**
* Created by IntelliJ IDEA.
* User: Nick Ebbutt
* Date: 18/09/12
* Time: 08:20
*/
public class RemotingConfig implements HandlerConfig {

    private String protocol;
    private String name;
    private String host;
    private int port;
    private int connectionAttempts = 40;
    private int connectionAttemptMillis = 250;

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getConnectionAttempts() {
        return connectionAttempts;
    }

    public void setConnnectionAttempts(int connectionAttempts) {
        this.connectionAttempts = connectionAttempts;
    }

    public int getConnectionAttemptMillis() {
        return connectionAttemptMillis;
    }

    public void setConnectionAttemptMillis(int connectionAttemptMillis) {
        this.connectionAttemptMillis = connectionAttemptMillis;
    }

    public boolean isValid() {
        return getHost() != null &&
               getProtocol() != null &&
               getName() != null &&
               getPort() > 0;
    }

    public String toString() {
        return "RemotingConfig{" +
                "protocol='" + protocol + '\'' +
                ", name='" + name + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", connectionAttempts=" + connectionAttempts +
                ", connectionAttemptMillis=" + connectionAttemptMillis +
                '}';
    }
}
