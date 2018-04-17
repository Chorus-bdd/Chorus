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
package org.chorusbdd.chorus.selenium;

import org.chorusbdd.chorus.annotations.*;
import org.chorusbdd.chorus.handlerconfig.ConfigurationManager;
import org.chorusbdd.chorus.handlerconfig.HandlerConfigLoader;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.results.ScenarioToken;
import org.chorusbdd.chorus.selenium.config.SeleniumConfigBuilderFactory;
import org.chorusbdd.chorus.selenium.config.SeleniumDriverType;
import org.chorusbdd.chorus.selenium.manager.SeleniumManager;
import org.chorusbdd.chorus.util.ChorusException;
import org.chorusbdd.chorus.util.ScopeUtils;
import org.chorusbdd.chorus.util.handler.HandlerPatterns;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.util.Properties;

import static org.chorusbdd.chorus.util.assertion.ChorusAssert.assertEquals;

/**
 * Created by nick on 23/12/2016.
 */
@Handler(value = "Selenium", scope = Scope.FEATURE)
public class SeleniumHandler {

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
    @Deprecated
    public void openChrome() throws IOException {
        
        //Set some sensible default properties for Chrome if not already set
        String chromeArgsKey = "selenium.Chrome.chromeArguments";
        Properties properties = new Properties();

        if ( ! configurationManager.getFeatureProperties().containsKey(chromeArgsKey)) {
            properties.setProperty(chromeArgsKey, "--disable-logging --silent");
        }
        properties.setProperty("selenium.Chrome." + SeleniumConfigBuilderFactory.driverType, SeleniumDriverType.CHROME.name());
        configurationManager.addFeatureProperties(properties);

        openNamedBrowser("Chrome");
    }
    
    @Step(".*open the " + HandlerPatterns.namePattern + " browser")
    public void openNamedBrowser(String configName) {
        Properties properties = getConfig(configName);
        seleniumManager.openABrowser(properties, configName);
    }

    @Step(".*navigate the " + HandlerPatterns.namePattern + " browser to (.*)")
    public void navigateTo(String configName, String url) {
        seleniumManager.navigateTo(configName, url);
    }

    @Step(".*navigate to (.*)")
    public void navigateNamedBrowserTo(String url) {
        navigateTo(SeleniumManager.LAST_OPENED_BROWSER, url);
    }

    @Step(".*refresh the page")
    public void refresh() {
        refreshNamedBrowser(SeleniumManager.LAST_OPENED_BROWSER);
    }
    
    @Step(".*refresh the page in the " + HandlerPatterns.namePattern + " browser") 
    public void refreshNamedBrowser(String configName) {
        seleniumManager.refreshThePage(configName);
    }

    @Step(value = ".*the url is (.*)", retryDuration = 2)
    public void checkCurrentUrl(String url) {
        checkCurrentUrlInNamedBrowser(SeleniumManager.LAST_OPENED_BROWSER, url);
    }

    @Step(value = ".*the url in the " + HandlerPatterns.namePattern + " browser is (.*)", retryDuration = 2)
    public void checkCurrentUrlInNamedBrowser(String configName, String url) {
       seleniumManager.checkCurrentURL(configName, url);
    }

    @Step(".*close the browser")
    public void quit() {
        quitNamedBrowser(SeleniumManager.LAST_OPENED_BROWSER);
    }

    @Step(".*close the " + HandlerPatterns.namePattern + " browser")
    public void quitNamedBrowser(String configName) {
        seleniumManager.quitBrowser(configName);
    }
    
    @Step(".*execute the script (.*) in the browser")
    public void executeScriptInDefaultBrowser(String scriptPath) {
        executeScriptInNamedBrowser(scriptPath, SeleniumManager.LAST_OPENED_BROWSER);
    }

    @Step(".*execute the script (.*) in the " + HandlerPatterns.namePattern + " browser")
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
}
