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

    private WebDriver driver;
    private boolean closeBrowserAtEndOfFeature = true;


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

        // Assume chromedriver will be in user PATH so non need to set it here
        // System.setProperty("webdriver.chrome.driver", "/Users/nick/Desktop/dev/chromeDriver/chromedriver");

        //TODO support config to enable selenium log?
        // File logFile = new File(feature.getFeatureDir().toString(), "selenium.log");
        // System.setProperty("webdriver.chrome.logfile",  logFile.toString());
        
        
        //Set some sensible default properties for Chrome if not already set
        String chromeArgsKey = "selenium.Chrome.chromeArguments";
        Properties properties = new Properties();

        if ( ! configurationManager.getFeatureProperties().containsKey(chromeArgsKey)) {
            properties.setProperty(chromeArgsKey, "--disable-logging --silent");
        }
        properties.setProperty("selenium.Chrome." + SeleniumConfigBuilderFactory.driverType, SeleniumDriverType.CHROME.name());
        configurationManager.addFeatureProperties(properties);
        

        //Make sure ChromeDriver is in your path on your OS to get this to run
        //https://sites.google.com/a/chromium.org/chromedriver/downloads
//        ChromeOptions chromeOptions = new ChromeOptions();
//        chromeOptions.addArguments("--disable-logging");
//        chromeOptions.addArguments("--silent");

        openNamedBrowser("Chrome");
//        driver = new ChromeDriver(chromeOptions);
    }

//    @Step(".*open RemoteWebDriver")
//    public void openRemoteWebDriver() throws IOException {
//
//        // Assume chromedriver will be in user PATH so non need to set it here
//        // System.setProperty("webdriver.chrome.driver", "/Users/nick/Desktop/dev/chromeDriver/chromedriver");
//
//        //TODO support config to enable selenium log?
//        // File logFile = new File(feature.getFeatureDir().toString(), "selenium.log");
//        // System.setProperty("webdriver.chrome.logfile",  logFile.toString());
//
////        System.setProperty("webdriver.chrome.silentOutput", "true");
//        
////        driver = new ChromeDriver(chromeOptions);
//
//        Capabilities capabilities = DesiredCapabilities.chrome();
//        RemoteWebDriver remoteWebDriver = new RemoteWebDriver(capabilities);
//    }
    
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

    //Can be added to a feature when we want to investigate a test failure
    @Step(".*leave the browser open(?: at the end of the feature)?")
    public void leaveTheBrowserOpen() {
        leaveTheNamedBrowserOpen(SeleniumManager.LAST_OPENED_BROWSER);
    }

    @Step(".*leave the " + HandlerPatterns.namePattern + " browser open(?: at the end of the feature)?")
    public void leaveTheNamedBrowserOpen(String configName) {
        seleniumManager.leaveBrowserOpenAtFeatureEnd(configName);
    }

    private Properties getConfig(String configName) {
        Properties p = new HandlerConfigLoader().loadPropertiesForSubGroup(configurationManager, "selenium", configName);
        new ScopeUtils().setScopeForContextIfNotConfigured(scenarioToken, p);
        return p;
    }
}
