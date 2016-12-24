package org.chorusbdd.chorus.selenium;

import org.chorusbdd.chorus.annotations.ChorusResource;
import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Scope;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.results.FeatureToken;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Created by nick on 23/12/2016.
 */
@Handler(value = "Selenium", scope = Scope.FEATURE)
public class SeleniumHandler {

    private ChorusLog log = ChorusLogFactory.getLog(SeleniumHandler.class);

    @ChorusResource(value="feature.token")
    private FeatureToken feature;

    private WebDriver driver;

    @Step(".*open Firefox")
    public void openABrowserWindow() {
        // Create a new instance of the Firefox driver
        driver = new FirefoxDriver();
        log.warn("Finished creating FirefoxDriver");

//        System.setProperty("webdriver.chrome.driver", "src/assembly/chromedriver.exe");

        //Make sure ChromeDriver is in your path on your OS to get this to run
        //https://sites.google.com/a/chromium.org/chromedriver/downloads
//        driver = new ChromeDriver();
    }

    @Step(".*navigate to ([\\w\\./:]+)")
    public void navigateTo(String uri) {
        // Find the text input element by its name
        String url = "http://localhost:8080/" + uri;
        driver.navigate().to(url);
    }

    @Step(".*I click the button with id (.*)")
    public void clickAButton(String id) {

        new WebDriverWait(driver, 10).until((WebDriver d) -> {
            // Find the text input element by its name
            WebElement element = driver.findElement(By.id(id));
            System.out.println("Clicking " + element);
            element.click();
            return true;
        });
    }

    @Step(".*close the browser")
    public void close() {
        new WebDriverWait(driver, 10).until((WebDriver d) -> {
            d.close();
            return true;
        });
    }
}
