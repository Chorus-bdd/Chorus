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
package org.chorusbdd.chorus.selenium.config;

import org.chorusbdd.chorus.annotations.Scope;
import org.chorusbdd.chorus.handlerconfig.configbean.HandlerConfigBuilder;
import org.openqa.selenium.remote.BrowserType;

import java.util.Optional;

public class SeleniumConfigBuilder implements HandlerConfigBuilder<SeleniumConfigBuilder, SeleniumConfig>, SeleniumConfig {

    private String configName;
    private Scope scope = Scope.SCENARIO;
    private String chromeArgs;
    private SeleniumDriverType seleniumDriverType = SeleniumDriverType.REMOTE_WEB_DRIVER;
    private String remoteWebDriverURL = "http://localhost:4444/wd/hub";
    private String remoteWebDriverBrowserType = BrowserType.CHROME;

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

    public void setRemoteWebDriverBrowserType(String remoteWebDriverBrowserType) {
        this.remoteWebDriverBrowserType = remoteWebDriverBrowserType;
    }

    @Override
    public String getRemoteWebDriverBrowserType() {
        return remoteWebDriverBrowserType;
    }

    @Override
    public SeleniumConfigBean build() {
        return new SeleniumConfigBean(
            configName, 
            scope, 
            seleniumDriverType, 
            chromeArgs, 
            remoteWebDriverURL,
            remoteWebDriverBrowserType
        );
    }

}