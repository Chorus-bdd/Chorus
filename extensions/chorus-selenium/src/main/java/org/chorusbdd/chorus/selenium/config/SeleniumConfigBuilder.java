package org.chorusbdd.chorus.selenium.config;

import org.chorusbdd.chorus.annotations.Scope;
import org.chorusbdd.chorus.handlerconfig.configbean.HandlerConfigBuilder;

import java.util.Optional;

public class SeleniumConfigBuilder implements HandlerConfigBuilder<SeleniumConfigBuilder, SeleniumConfig>, SeleniumConfig {

    private String configName;
    private Scope scope = Scope.SCENARIO;
    private String chromeArgs;
    private SeleniumDriverType seleniumDriverType = SeleniumDriverType.REMOTE_WEB_DRIVER;
    private String remoteWebDriverURL = "http://localhost:4444/wd/hub";

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
    public Optional<String> getChromeArgs() {
        return Optional.ofNullable(chromeArgs);
    }

    @Override
    public String getRemoteWebDriverURL() {
        return remoteWebDriverURL;
    }

    public void setRemoteWebDriverURL(String remoteWebDriverURL) {
        this.remoteWebDriverURL = remoteWebDriverURL;
    }

    public void setChromeArgs(String chromeArgs) {
        this.chromeArgs = chromeArgs;
    }

    public SeleniumDriverType getSeleniumDriverType() {
        return seleniumDriverType;
    }

    public void setSeleniumDriverType(SeleniumDriverType seleniumDriverType) {
        this.seleniumDriverType = seleniumDriverType;
    }

    @Override
    public SeleniumConfigBean build() {
        return new SeleniumConfigBean(
            configName, 
            scope, 
            seleniumDriverType, 
            chromeArgs, 
            remoteWebDriverURL
        );
    }

}