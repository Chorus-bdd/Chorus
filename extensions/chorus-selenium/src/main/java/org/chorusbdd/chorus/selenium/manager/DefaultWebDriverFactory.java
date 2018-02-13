package org.chorusbdd.chorus.selenium.manager;

import org.chorusbdd.chorus.selenium.config.SeleniumConfig;
import org.chorusbdd.chorus.util.ChorusException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

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
            case REMOTE_WEB_DRIVER:
                DesiredCapabilities capabilities = DesiredCapabilities.chrome();
                capabilities.setJavascriptEnabled(true);
                URL url = getRemoteWebDriverURL(seleniumConfig.getRemoteWebDriverURL());
                result = new RemoteWebDriver(url, capabilities);;
                break;
            default:
                throw new ChorusException("SeleniumDriverType " + seleniumConfig.getSeleniumDriverType() + " is not supported");
        }
        return result;
    }

    private URL getRemoteWebDriverURL(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new ChorusException("Invalid URL for Selenium RemoteWebDriver [" + url + "]", e);
        }
    }

    //Before creating a ChromeDriver set this to silence the verbose output
    private void setSpecialSilenceChromeDriverSysProperty() {
        System.setProperty("webdriver.chrome.silentOutput", "true");
    }
}
