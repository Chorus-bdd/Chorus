package org.chorusbdd.chorus.selenium;

import org.chorusbdd.chorus.annotations.*;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.results.EndState;
import org.chorusbdd.chorus.results.FeatureToken;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.chorusbdd.chorus.util.assertion.ChorusAssert.assertEquals;

/**
 * Created by nick on 23/12/2016.
 */
@Handler(value = "Selenium", scope = Scope.FEATURE)
public class SeleniumHandler {

    static {
        //Selenium uses JDK logging and logs quite noisily
        //We don't want that in the Chorus output by default
        //TODO make Selenium logging configurable
        Logger.getLogger("").setLevel(Level.OFF);
    }

    private ChorusLog log = ChorusLogFactory.getLog(SeleniumHandler.class);

    @ChorusResource(value="feature.token")
    private FeatureToken feature;

    private WebDriver driver;

    @Step(".*open Chrome")
    public void openABrowserWindow() throws IOException {

        // Assume chromedriver will be in user PATH so non need to set it here
        // System.setProperty("webdriver.chrome.driver", "/Users/nick/Desktop/dev/chromeDriver/chromedriver");


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

    @Destroy(scope = Scope.FEATURE)
    public void destroy() {

        //Close the browser but leave it open if the test failed so we can take a look
        if ( feature.getEndState() != EndState.FAILED) {
            driver.quit();
        }
    }

//        new WebDriverWait(driver, 10).until((WebDriver d) -> {
//            d.quit();
//            return true;
//        });

}
