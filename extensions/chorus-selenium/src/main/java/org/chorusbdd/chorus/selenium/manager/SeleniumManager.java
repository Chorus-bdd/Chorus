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

import org.chorusbdd.chorus.annotations.SubsystemConfig;
import org.chorusbdd.chorus.handlerconfig.ConfigPropertySource;
import org.chorusbdd.chorus.subsystem.Subsystem;
import org.openqa.selenium.WebDriver;

import java.util.Properties;

/**
 * Created by nickebbutt on 05/02/2018.
 */
@SubsystemConfig(
        id = "seleniumManager", 
        implementationClass = "org.chorusbdd.chorus.selenium.manager.SeleniumManagerImpl", 
        overrideImplementationClassSystemProperty = "chorusSeleniumManager")
public interface SeleniumManager extends Subsystem {


    /**
     * Used to indicate that the user didn't name a browser.
     * We will run the action on the first browser which was opened
     */
    String LAST_OPENED_BROWSER = "SELENIUM_MANAGER_LAST_OPENED_BROWSER";

    void openABrowser(Properties properties, String configName);

    /**
     * Navigate the named browser to the URL provided
     */
    void navigateTo(String configName, String url);

    void refreshThePage(String configName);

    void checkCurrentURL(String configName, String url);

    void quitBrowser(String configName);

    void leaveBrowserOpenAtFeatureEnd(String configName);

    /**
     *  return One of Boolean, Long, Double, String, List, Map or WebElement. Or null.
     */
    Object executeScriptFile(String configName, String scriptPath);

    /**
     * This is here to allow users to write handlers which reuse the SeleniumManager for creation of the WebDriver instance, 
     * but add extra custom steps using the WebDriver instance
     * 
     * @return the WebDriver instance created for this config
     */
    WebDriver getWebDriver(String configName);
}
