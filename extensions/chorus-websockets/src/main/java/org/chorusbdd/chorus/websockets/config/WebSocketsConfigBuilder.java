package org.chorusbdd.chorus.websockets.config;

import org.chorusbdd.chorus.annotations.Scope;

public class WebSocketsConfigBuilder implements WebSocketsConfig {

    private String configName;
    private int stepTimeoutSeconds = 60;
    private int port = 9080;
    private Scope scope = Scope.SCENARIO;
    private int clientConnectTimeoutSeconds = 60;

    @Override
    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
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