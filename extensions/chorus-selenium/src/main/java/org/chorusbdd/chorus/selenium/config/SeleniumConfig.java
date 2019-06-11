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
package org.chorusbdd.chorus.selenium.config;

import org.chorusbdd.chorus.annotations.Scope;
import org.chorusbdd.chorus.handlerconfig.configbean.NamedConfigBean;
import org.chorusbdd.chorus.handlerconfig.configproperty.ConfigProperty;

import java.util.Optional;

/**
 * Created by nick on 24/09/2014.
 */
public interface SeleniumConfig extends NamedConfigBean {
    
    Scope getScope();

    SeleniumDriverType getDriverType();
    
    String getWebDriverFactoryClass();

    // Chrome Driver specific:

    Optional<String> getChromeArgs();

    // Remote Web Driver Specific

    String getRemoteWebDriverBrowserType();
    
    String getRemoteWebDriverURL();
}
