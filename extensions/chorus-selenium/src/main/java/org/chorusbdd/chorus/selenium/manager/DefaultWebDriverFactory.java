package org.chorusbdd.chorus.selenium.manager;

import org.chorusbdd.chorus.selenium.config.SeleniumConfig;
import org.chorusbdd.chorus.util.ChorusException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * Created by nickebbutt on 12/02/2018.
 */
public class DefaultWebDriverFactory implements WebDriverFactory {
    
    @Override
    public WebDriver createWebDriver(SeleniumConfig seleniumConfig) {
        WebDriver result;
        switch(seleniumConfig.getSeleniumDriverType()) {
            case CHROME:
                setSpecialSilenceChromeDriverSysProperty();
                ChromeOptions chromeOptions = new ChromeOptions();
                seleniumConfig.getChromeArgs().map(s -> s.split(" ")).ifPresent(chromeOptions::addArguments);
                result = new ChromeDriver(chromeOptions);
                break;
            default:
                throw new ChorusException("SeleniumDriverType " + seleniumConfig.getSeleniumDriverType() + " is not supported");
        }
        return result;
    }

    //Before creating a ChromeDriver set this to silence the verbose output
    private void setSpecialSilenceChromeDriverSysProperty() {
        System.setProperty("webdriver.chrome.silentOutput", "true");
    }
}
