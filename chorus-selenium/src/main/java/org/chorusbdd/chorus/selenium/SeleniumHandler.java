package org.chorusbdd.chorus.selenium;

import org.chorusbdd.chorus.annotations.*;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.results.FeatureToken;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;

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


    @Initialize(scope = Scope.FEATURE)
    public void init() {
        seleniumLogging.suppressLogging();
    }

    @Step(".*open Chrome")
    public void openABrowserWindow() throws IOException {

        // Assume chromedriver will be in user PATH so non need to set it here
        // System.setProperty("webdriver.chrome.driver", "/Users/nick/Desktop/dev/chromeDriver/chromedriver");

        //TODO support config to enable selenium log?
        // File logFile = new File(feature.getFeatureDir().toString(), "selenium.log");
        // System.setProperty("webdriver.chrome.logfile",  logFile.toString());

        System.setProperty("webdriver.chrome.silentOutput", "true");

        //Make sure ChromeDriver is in your path on your OS to get this to run
        //https://sites.google.com/a/chromium.org/chromedriver/downloads
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--disable-logging");
        chromeOptions.addArguments("--silent");

        driver = new ChromeDriver(chromeOptions);
    }

    @Step(".*navigate to (.*)")
    public void navigateTo(String url) {
        driver.navigate().to(url);
    }

    @Step(".*refresh the page")
    public void refresh() {
        driver.navigate().refresh();
    }

    @Step(".*the url is (.*)")
    @PassesWithin(length =  2)
    public void checkCurrentUrl(String url) {
        String currentUrl = driver.getCurrentUrl();
        //ChromeDriver adds a trailing /
        if ( currentUrl.endsWith("/")) {
            currentUrl = currentUrl.substring(0, currentUrl.length() - 1);
        }
        assertEquals(url, currentUrl);
    }

    @Step(".*close the browser")
    public void quit() {
        driver.quit();
    }

    //Can be added to a feature when we want to investigate a test failure
    @Step(".*leave the browser open(?: at the end of the feature)?")
    public void leaveTheBrowserOpen() {
        closeBrowserAtEndOfFeature = false;
    }

    @Destroy(scope = Scope.FEATURE)
    public void destroy() {

        if ( closeBrowserAtEndOfFeature) {
            driver.quit();
        }
    }

}
