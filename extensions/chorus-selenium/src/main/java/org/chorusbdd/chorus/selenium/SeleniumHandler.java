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
package org.chorusbdd.chorus.selenium;

import org.chorusbdd.chorus.annotations.*;
import org.chorusbdd.chorus.handlerconfig.ConfigPropertySource;
import org.chorusbdd.chorus.handlerconfig.ConfigurationManager;
import org.chorusbdd.chorus.handlerconfig.HandlerConfigLoader;
import org.chorusbdd.chorus.handlerconfig.configproperty.ConfigBuilderException;
import org.chorusbdd.chorus.handlerconfig.configproperty.ConfigPropertyParser;
import org.chorusbdd.chorus.handlerconfig.configproperty.ConfigurationProperty;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.results.ScenarioToken;
import org.chorusbdd.chorus.selenium.config.SeleniumConfigBean;
import org.chorusbdd.chorus.selenium.config.SeleniumDriverType;
import org.chorusbdd.chorus.selenium.manager.SeleniumManager;
import org.chorusbdd.chorus.util.ChorusException;
import org.chorusbdd.chorus.util.ScopeUtils;
import org.chorusbdd.chorus.util.handler.HandlerPatterns;

import java.io.IOException;
import java.util.List;
import java.util.Properties;


/**
 * Created by nick on 23/12/2016.
 */
@Handler(value = "Selenium", scope = Scope.FEATURE)
public class SeleniumHandler implements ConfigPropertySource {

    private ChorusLog log = ChorusLogFactory.getLog(SeleniumHandler.class);

    private SeleniumLoggingSuppression seleniumLogging = new SeleniumLoggingSuppression();

    @ChorusResource(value="feature.token")
    private FeatureToken feature; 

    @ChorusResource("subsystem.configurationManager")
    private ConfigurationManager configurationManager;
    
    @ChorusResource("subsystem.seleniumManager")
    private SeleniumManager seleniumManager;
    
    @ChorusResource("scenario.token")
    private ScenarioToken scenarioToken;


    @Initialize(scope = Scope.FEATURE)
    public void init() {
        seleniumLogging.suppressLogging();
    }

    @Step(".*open Chrome")
    @Documentation(order = 10, description = "Open a window in a local Chrome browser using the chromedriver installed on the system. Deprecated - use 'open the Chrome browser' which allows configuration", example = "Given I open Chrome")
    @Deprecated
    public void openChrome() throws IOException {
        
        //Set some sensible default properties for Chrome if not already set
        String chromeArgsKey = "selenium.Chrome.chromeDriver.arguments";
        Properties properties = new Properties();

        if ( ! configurationManager.getFeatureProperties().containsKey(chromeArgsKey)) {
            properties.setProperty(chromeArgsKey, "--disable-logging --silent");
        }
        properties.setProperty("selenium.Chrome.driverType", SeleniumDriverType.CHROME.name());
        configurationManager.addFeatureProperties(properties);

        openNamedBrowser("Chrome");
    }
    
    @Step(".*open the " + HandlerPatterns.namePattern + " browser")
    @Documentation(order = 20, description = "Open a window in a named browser which is defined in handler properties", example = "Given I open the myChrome browser")
    public void openNamedBrowser(String configName) {
        Properties properties = getConfig(configName);
        seleniumManager.openABrowser(properties, configName);
    }

    @Step(".*navigate the " + HandlerPatterns.namePattern + " browser to (.*)")
    @Documentation(order = 30, description = "Open an provided URL in the window of the named browser", example = "When I navigate the myChrome browser to http://www.bbc.co.uk")
    public void navigateTo(String configName, String url) {
        seleniumManager.navigateTo(configName, url);
    }

    @Step(".*navigate to (.*)")
    @Documentation(order = 40, description = "Open the provided URL in the most recently opened browser window", example = "When I navigate to http://www.bbc.co.uk")
    public void navigateNamedBrowserTo(String url) {
        navigateTo(SeleniumManager.LAST_OPENED_BROWSER, url);
    }

    @Step(".*refresh the page")
    @Documentation(order = 50, description = "Refresh the current page in the most recently opened browser window", example = "And I refresh the page")
    public void refresh() {
        refreshNamedBrowser(SeleniumManager.LAST_OPENED_BROWSER);
    }
    
    @Step(".*refresh the page in the " + HandlerPatterns.namePattern + " browser")
    @Documentation(order = 60, description = "Refresh the current page in the named browser window", example = "And I refresh the page in the myChrome browser")
    public void refreshNamedBrowser(String configName) {
        seleniumManager.refreshThePage(configName);
    }

    @Step(value = ".*the url is (.*)", retryDuration = 2)
    @Documentation(order = 70, description = "Test the URL in the most recently opened browser matches the provided URL", example = "Then the url is http://www.bbc.co.uk")
    public void checkCurrentUrl(String url) {
        checkCurrentUrlInNamedBrowser(SeleniumManager.LAST_OPENED_BROWSER, url);
    }

    @Step(value = ".*the url in the " + HandlerPatterns.namePattern + " browser is (.*)", retryDuration = 2)
    @Documentation(order = 80, description = "Test the URL in the named browser matches the provided URL", example = "Then the url in the myChrome browser is http://www.bbc.co.uk")
    public void checkCurrentUrlInNamedBrowser(String configName, String url) {
       seleniumManager.checkCurrentURL(configName, url);
    }

    @Step(".*close the browser")
    @Documentation(order = 90, description = "Close the most recently opened browser window", example = "Then I close the browser")
    public void quit() {
        quitNamedBrowser(SeleniumManager.LAST_OPENED_BROWSER);
    }

    @Step(".*close the " + HandlerPatterns.namePattern + " browser")
    @Documentation(order = 100, description = "Close the open window in the named browser", example = "Then I close the myChrome browser")
    public void quitNamedBrowser(String configName) {
        seleniumManager.quitBrowser(configName);
    }

    @Step(".*execute the script (.*) in the browser")
    @Documentation(order = 110, description = "Execute a script at the file path relative to the feature directory within the window of the last opened browser", example = "Then I execute the script myJavascript.js in the browser")
    public void executeScriptInDefaultBrowser(String scriptPath) {
        executeScriptInNamedBrowser(scriptPath, SeleniumManager.LAST_OPENED_BROWSER);
    }

    @Step(".*execute the script (.*) in the " + HandlerPatterns.namePattern + " browser")
    @Documentation(order = 120, description = "Execute a script at the file path relative to the feature directory within the window of the named browser", example = "Then I execute the script myJavascript.js in the myChrome browser")
    public void executeScriptInNamedBrowser(String scriptPath, String configName) {
        Object o = seleniumManager.executeScriptFile(configName, scriptPath);
        if ( o instanceof Boolean && !((Boolean)o)) {
            throw new ChorusException("Execution of script failed (script returned false)");   
        }
    }

    private Properties getConfig(String configName) {
        Properties p = new HandlerConfigLoader().loadPropertiesForSubGroup(configurationManager, "selenium", configName);
        new ScopeUtils().setScopeForContextIfNotConfigured(scenarioToken, p);
        return p;
    }

    @Override
    public List<ConfigurationProperty> getConfigProperties() throws ConfigBuilderException {
        return new ConfigPropertyParser().getConfigProperties(SeleniumConfigBean.class);
    }
}
