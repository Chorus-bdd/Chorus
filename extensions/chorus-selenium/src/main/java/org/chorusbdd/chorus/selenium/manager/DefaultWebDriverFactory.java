package org.chorusbdd.chorus.selenium.manager;

import org.chorusbdd.chorus.selenium.config.SeleniumConfig;
import org.chorusbdd.chorus.util.ChorusException;
import org.openqa.selenium.Platform;
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
                // Assume chromedriver will be in user PATH so non need to set it here
                // System.setProperty("webdriver.chrome.driver", "/Users/nick/Desktop/dev/chromeDriver/chromedriver");

                //TODO support config to enable selenium log?
                // File logFile = new File(feature.getFeatureDir().toString(), "selenium.log");
                // System.setProperty("webdriver.chrome.logfile",  logFile.toString());
                
                setSpecialSilenceChromeDriverSysProperty();
                ChromeOptions chromeOptions = new ChromeOptions();
                seleniumConfig.getChromeArgs().map(s -> s.split(" ")).ifPresent(chromeOptions::addArguments);
                result = new ChromeDriver(chromeOptions);
                break;
            case REMOTE_WEB_DRIVER:
                DesiredCapabilities capabilities = new DesiredCapabilities(
                        seleniumConfig.getRemoteWebDriverBrowserType(), 
                        "",   //can't configure version yet
                        Platform.ANY  //can't configure platform yet
                );
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
