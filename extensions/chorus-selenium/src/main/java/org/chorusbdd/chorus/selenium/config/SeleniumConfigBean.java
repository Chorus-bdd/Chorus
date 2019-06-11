/**
 * MIT License
 *
 * Copyright (c) 2019 Chorus BDD Organisation.
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
import org.chorusbdd.chorus.handlerconfig.configproperty.ConfigProperty;
import org.chorusbdd.chorus.handlerconfig.configproperty.ConfigValidator;
import org.chorusbdd.chorus.handlerconfig.configproperty.ConfigValidatorException;

import java.util.Optional;

import static org.chorusbdd.chorus.handlerconfig.configproperty.ConfigPropertyUtils.checkNotNullAndNotEmpty;

/**
 * Config for a Selenium browser session
 */
public class SeleniumConfigBean implements SeleniumConfig {

    private String configName;
    private Scope scope;
    private SeleniumDriverType seleniumDriverType;
    private String webDriverFactoryClass;
    private String chromeArgs;
    private String remoteWebDriverURL;

    private String remoteWebDriverBrowserType;

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    @Override
    public SeleniumDriverType getDriverType() {
        return seleniumDriverType;
    }

    @ConfigProperty(
            name = "driverType",
            description = "Defines the selenium driver type, e.g. CHROME, REMOTE_WEB_DRIVER, CUSTOM (if specifying a custom webDriverFactoryClass)",
            defaultValue = "REMOTE_WEB_DRIVER",
            order = 10
    )
    public void setDriverType(SeleniumDriverType seleniumDriverType) {
        this.seleniumDriverType = seleniumDriverType;
    }
    
    @Override
    public String getWebDriverFactoryClass() {
        return this.webDriverFactoryClass;
    }
    
    @ConfigProperty(
            name="webDriverFactoryClass",
            description = "Set the implementation class for WebDriverFactory. Supply a fully qualified class name. Class must have a nullary constructor and implement WebDriverFactory",
            mandatory = false,
            defaultValue = "org.chorusbdd.chorus.selenium.manager.DefaultWebDriverFactory",
            order = 15
    )
    public void setWebDriverFactory(String factoryClass) {
        this.webDriverFactoryClass=factoryClass;
    }

    @Override
    public Optional<String> getChromeArgs() {
        return Optional.ofNullable(chromeArgs);
    }

    @ConfigProperty(
            name = "chromeDriver.arguments",
            description = "Arguments to pass to the chrome browser if using CHROME driver type",
            mandatory = false,
            order = 20
    )
    public void setChromeArgs(String chromeArgs) {
        this.chromeArgs = chromeArgs;
    }

    @Override
    public String getRemoteWebDriverBrowserType() {
        return remoteWebDriverBrowserType;
    }

    @ConfigProperty(
            name = "remoteWebDriver.browserType",
            description = "If using REMOTE_WEB_DRIVER, a value to pass to the remote selenium web driver to request a browser type, e.g. chrome, firefox, safari",
            defaultValue = "chrome",
            mandatory = false,
            order = 30
    )
    public void setRemoteWebDriverBrowserType(String remoteWebDriverBrowserType) {
        this.remoteWebDriverBrowserType = remoteWebDriverBrowserType;
    }

    @Override
    public String getRemoteWebDriverURL() {
        return remoteWebDriverURL;
    }

    @ConfigProperty(
            name = "remoteWebDriver.URL",
            description = "If using REMOTE_WEB_DRIVER, the URL to use to make the connection to the remote web driver or selenium grid",
            defaultValue = "http://localhost:4444/wd/hub",
            mandatory = false,
            order = 40
    )
    public void setRemoteWebDriverURL(String remoteWebDriverURL) {
        this.remoteWebDriverURL = remoteWebDriverURL;
    }

    @Override
    public Scope getScope() {
        return scope;
    }

    @ConfigProperty(
            name = "scope",
            description = "Defines whether a browser connection should be closed at the end of a feature, or after each scenario" +
                    " This will be set automatically to FEATURE for connections established during 'Feature-Start:' if not provided, otherwise Scenario",
            defaultValue = "SCENARIO",
            order = 50
    )
    public void setScope(Scope scope) {
        this.scope = scope;
    }

    @ConfigValidator
    public void checkValid() {
        switch (seleniumDriverType) {
            case CHROME:
                checkChromeProperties();
                break;
            case REMOTE_WEB_DRIVER:
                checkRemoteWebDriverProperites();
                break;
            case CUSTOM:
                break;
            default:
                throw new ConfigValidatorException("Selenium Driver Type " + seleniumDriverType + " config cannot be validated");
        }
    }

    private void checkRemoteWebDriverProperites() {
        checkNotNullAndNotEmpty(remoteWebDriverURL, "remoteWebDriverURL");
        checkNotNullAndNotEmpty(remoteWebDriverBrowserType, "remoteWebDriverBrowserType");
    }

    private void checkChromeProperties() { }
}
