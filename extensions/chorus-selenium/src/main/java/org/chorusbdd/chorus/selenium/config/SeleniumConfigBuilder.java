package org.chorusbdd.chorus.selenium.config;

import org.chorusbdd.chorus.annotations.Scope;
import org.chorusbdd.chorus.handlerconfig.configbean.HandlerConfigBuilder;

public class SeleniumConfigBuilder implements HandlerConfigBuilder<SeleniumConfigBuilder, SeleniumConfig>, SeleniumConfig {

    private String configName;
    private Scope scope = Scope.SCENARIO;

    @Override
    public String getConfigName() {
        return configName;
    }

    public SeleniumConfigBuilder setConfigName(String configName) {
        this.configName = configName;
        return this;
    }

    @Override
    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    @Override
    public SeleniumConfigBean build() {
        return new SeleniumConfigBean(configName, scope);
    }

}