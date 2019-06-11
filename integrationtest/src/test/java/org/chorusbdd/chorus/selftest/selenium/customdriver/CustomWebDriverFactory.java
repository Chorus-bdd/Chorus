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
package org.chorusbdd.chorus.selftest.selenium.customdriver;

import org.chorusbdd.chorus.selenium.config.SeleniumConfig;
import org.chorusbdd.chorus.selenium.config.SeleniumConfigBean;
import org.chorusbdd.chorus.selenium.config.SeleniumDriverType;
import org.chorusbdd.chorus.selenium.manager.DefaultWebDriverFactory;
import org.chorusbdd.chorus.util.ChorusException;
import org.openqa.selenium.WebDriver;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Test a custom web driver factory can be configured for Selenium
 */
public class CustomWebDriverFactory extends DefaultWebDriverFactory {

    public static AtomicInteger instanceCount = new AtomicInteger();

    @Override
    public WebDriver createWebDriver(SeleniumConfig seleniumConfig) {

        if ( seleniumConfig.getDriverType() != SeleniumDriverType.CUSTOM) {
            throw new ChorusException("Failed to receive CUSTOM driver type");
        }
        
        CustomWebDriverFactory.instanceCount.incrementAndGet();
        
        //create a chrome config to permit a driver to be created in superclass
        SeleniumConfigBean seleniumConfigBean = new SeleniumConfigBean();
        seleniumConfigBean.setDriverType(SeleniumDriverType.CHROME);
        return super.createWebDriver(seleniumConfigBean);
    }
    
}
