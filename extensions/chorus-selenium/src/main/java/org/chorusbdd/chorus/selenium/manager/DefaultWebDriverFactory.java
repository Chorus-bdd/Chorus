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
        switch(seleniumConfig.getDriverType()) {
            case CHROME:
                // Assume chromedriver will be in user PATH so non need to set it here
                // System.setProperty("webdriver.chrome.driver", "/Users/nick/Desktop/dev/chromeDriver/chromedriver");

                //TODO support config to enable selenium log?
                // File logFile = new File(feature.getFeatureDir().toString(), "selenium.log");
                // System.setProperty("webdriver.chrome.logfile",  logFile.toString());
                
                setSpecialSilenceChromeDriverSysProperty();
                ChromeOptions chromeOptions = new ChromeOptions();
                
                //Setting this prevents 'Error: Loading of unpacked extensions is disabled by the administrator' which pops up a warning dialog
                //if starting chrome on an OS where the admin has disabled browser plugins/extensions
                chromeOptions.setExperimentalOption("useAutomationExtension", false);
                
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
                throw new ChorusException("SeleniumDriverType " + seleniumConfig.getDriverType() + " is not supported");
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
