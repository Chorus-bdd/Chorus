/**
 * MIT License
 *
 * Copyright (c) 2018 Chorus BDD Organisation.
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
package org.chorusbdd.chorus.selenium.config;

import org.chorusbdd.chorus.handlerconfig.configbean.AbstractConfigBeanValidator;
import org.chorusbdd.chorus.util.ChorusException;

/**
 * Created by nick on 23/09/2014.
 */
public class SeleniumConfigBeanValidator extends AbstractConfigBeanValidator<SeleniumConfig> {

    protected boolean checkValid(SeleniumConfig seleniumConfig) {
        boolean valid = true;
        
        switch(seleniumConfig.getSeleniumDriverType()) {
            case CHROME:
                valid = checkChromeProperties(seleniumConfig);
                break;
            case REMOTE_WEB_DRIVER:
                valid = checkRemoteWebDriverProperites(seleniumConfig);
                break;
            default:
                throw new ChorusException("Selenium Driver Type " + seleniumConfig.getSeleniumDriverType() + 
                        " config cannot be validated");
        }
        return valid;
    }

    private boolean checkRemoteWebDriverProperites(SeleniumConfig seleniumConfig) {
        boolean result = true;
        if ( ! isSet(seleniumConfig.getRemoteWebDriverURL())) {
            logInvalidConfig(SeleniumConfigBuilderFactory.remoteWebDriverURL + " cannot be null", seleniumConfig);
            result = false;
        } else if ( ! isSet(seleniumConfig.getRemoteWebDriverBrowserType())) {
            logInvalidConfig(SeleniumConfigBuilderFactory.remoteWebDriverBrowserType + " cannot be null", seleniumConfig);
            result = false;        }
        return result;
    }

    private boolean checkChromeProperties(SeleniumConfig seleniumConfig) {
        return true;        
    }

}
