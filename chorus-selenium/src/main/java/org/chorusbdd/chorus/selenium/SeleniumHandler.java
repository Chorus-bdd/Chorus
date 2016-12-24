package org.chorusbdd.chorus.selenium;

import org.chorusbdd.chorus.annotations.*;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.results.EndState;
import org.chorusbdd.chorus.results.FeatureToken;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.chorusbdd.chorus.util.assertion.ChorusAssert.assertEquals;

/**
 * Created by nick on 23/12/2016.
 */
@Handler(value = "Selenium", scope = Scope.FEATURE)
public class SeleniumHandler {

    private ChorusLog log = ChorusLogFactory.getLog(SeleniumHandler.class);

    @ChorusResource(value="feature.token")
    private FeatureToken feature;

    private WebDriver driver;

    @Step(".*open Chrome")
    public void openABrowserWindow() {
        // Create a new instance of the Firefox driver
        //        driver = new FirefoxDriver();
        //        log.warn("Finished creating FirefoxDriver");

        // Assume chromedriver will be in user PATH so non need to set it here
        //        System.setProperty("webdriver.chrome.driver", "/Users/nick/Desktop/dev/chromeDriver/chromedriver");
        System.setProperty("webdriver.chrome.args", "--disable-logging");
        System.setProperty("webdriver.chrome.silentOutput", "true");

        //Make sure ChromeDriver is in your path on your OS to get this to run
        //https://sites.google.com/a/chromium.org/chromedriver/downloads
        ChromeOptions chromeOptions = new ChromeOptions();
        driver = new ChromeDriver(chromeOptions);
    }

    @Step(".*navigate to (.*)")
    public void navigateTo(String url) {
        // Find the text input element by its name
        driver.navigate().to(url);
    }

//    @Step(".*I click the element with id (.*)")
//    public void clickAButton(String id) {
//
//        new WebDriverWait(driver, 10).until((WebDriver d) -> {
//            // Find the text input element by its name
//            WebElement element = driver.findElement(By.id(id));
//            System.out.println("Clicking " + element);
//            element.click();
//            return true;
//        });
//    }

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
        new WebDriverWait(driver, 10).until((WebDriver d) -> {
            d.quit();
            return true;
        });
    }

    @Destroy(scope = Scope.FEATURE)
    public void destroy() {

        //Close the browser but leave it open if the test failed so we can take a look
        if ( feature.getEndState() != EndState.FAILED) {
            driver.quit();
        }
    }
}
