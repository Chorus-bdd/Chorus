package org.chorusbdd.chorus.handlers.remoting;

/**
* Created by IntelliJ IDEA.
* User: Nick Ebbutt
* Date: 18/09/12
* Time: 08:20
*/
public class RemotingConfig {

    private String protocol;
    private String name;
    private String host;
    private int port;
    private int connectionRetryAttempts = 5;
    private int connectionRetryMillis = 10000;

    public boolean isValid() {
        return getHost() != null && getProtocol() != null && getName() != null && getPort() > 0;
    }

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

    public int getConnectionRetryAttempts() {
        return connectionRetryAttempts;
    }

    public void setConnectionRetryAttempts(int connectionRetryAttempts) {
        this.connectionRetryAttempts = connectionRetryAttempts;
    }

    public int getConnectionRetryMillis() {
        return connectionRetryMillis;
    }

    public void setConnectionRetryMillis(int connectionRetryMillis) {
        this.connectionRetryMillis = connectionRetryMillis;
    }

    public String toString() {
        return "RemotingConfig{" +
                "protocol='" + protocol + '\'' +
                ", name='" + name + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", connectionRetryAttempts=" + connectionRetryAttempts +
                ", connectionRetryMillis=" + connectionRetryMillis +
                '}';
    }
}
