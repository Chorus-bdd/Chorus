/**
 * MIT License
 *
 * Copyright (c) 2023 Chorus BDD Organisation.
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

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.selenium.config.DriverLogLevel;
import org.chorusbdd.chorus.selenium.config.SeleniumConfig;
import org.chorusbdd.chorus.util.ChorusException;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chromium.ChromiumDriverLogLevel;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeDriverService;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;


/**
 * Created by nickebbutt on 12/02/2018.
 */
public class DefaultWebDriverFactory implements WebDriverFactory {

    private final ChorusLog log = ChorusLogFactory.getLog(DefaultWebDriverFactory.class);

    @Override
    public WebDriver createWebDriver(SeleniumConfig seleniumConfig) {
        WebDriver result;
        switch(seleniumConfig.getDriverType()) {
            case CHROME:
                // Assume chromedriver will be in user PATH so non need to set it here
                // System.setProperty("webdriver.chrome.driver", "/Users/nick/dev/chromeDriver/chromedriver");

                //TODO support config to enable selenium log?
                // File logFile = new File(feature.getFeatureDir().toString(), "selenium.log");
                // System.setProperty("webdriver.chrome.logfile",  logFile.toString());
                
                ChromiumDriverLogLevel chromeLogLevel = getLogLevel(seleniumConfig);
                ChromeDriverService chromeDriverService = new ChromeDriverService.Builder().withLogLevel(chromeLogLevel).build();
                ChromeOptions chromeOptions = new ChromeOptions();

                //Setting this prevents 'Error: Loading of unpacked extensions is disabled by the administrator' which pops up a warning dialog
                //if starting chrome on an OS where the admin has disabled browser plugins/extensions
                chromeOptions.setExperimentalOption("useAutomationExtension", false);
                
                seleniumConfig.getChromeArgs().map(s -> s.split(" ")).ifPresent(chromeOptions::addArguments);
                result = new ChromeDriver(chromeDriverService, chromeOptions);
                break;
            case EDGE:
                ChromiumDriverLogLevel edgelogLevel = getLogLevel(seleniumConfig);
                EdgeDriverService edgeDriverService = new EdgeDriverService.Builder().withLoglevel(edgelogLevel).build();
                EdgeOptions edgeOptions = new EdgeOptions();
                seleniumConfig.getEdgeArgs().map(s -> s.split(" ")).ifPresent(edgeOptions::addArguments);
                result = new EdgeDriver(edgeDriverService, edgeOptions);
                break;
            case REMOTE_WEB_DRIVER:
                DesiredCapabilities capabilities = new DesiredCapabilities(
                        seleniumConfig.getRemoteWebDriverBrowserType(), 
                        "",   //can't configure version yet
                        Platform.ANY  //can't configure platform yet
                );
                Level logLevel = getJavaInfoLogLevel(seleniumConfig);
                URL url = getRemoteWebDriverURL(seleniumConfig.getRemoteWebDriverURL());
                RemoteWebDriver remoteWebDriver = new RemoteWebDriver(url, capabilities);
                remoteWebDriver.setLogLevel(logLevel);
                result = remoteWebDriver;;
                break;
            default:
                throw new ChorusException("SeleniumDriverType " + seleniumConfig.getDriverType() + " is not supported");
        }
        return result;
    }

    private Level getJavaInfoLogLevel(SeleniumConfig seleniumConfig) {
        DriverLogLevel logLevel = seleniumConfig.getLogLevel();
        Level level;
        try {
            level = Level.parse(logLevel.name());
        } catch (IllegalArgumentException e) {
            log.warn("The log level " + logLevel + " was not supported by the selenium driver, will be defaulted to OFF");
            level = Level.OFF;
        }
        return level;
    }

    private ChromiumDriverLogLevel getLogLevel(SeleniumConfig seleniumConfig) {
        DriverLogLevel logLevel = seleniumConfig.getLogLevel();
        ChromiumDriverLogLevel result = ChromiumDriverLogLevel.fromString(logLevel.name());
        if (result == null) {
            log.warn("The log level " + logLevel + " was not supported by the selenium driver, will be defaulted to OFF");
            result = ChromiumDriverLogLevel.OFF;
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

}
