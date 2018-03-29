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

import org.chorusbdd.chorus.annotations.Scope;
import org.openqa.selenium.remote.BrowserType;

import java.util.Optional;

/**
 * An immutable runtime config for a process
 */
public class SeleniumConfigBean implements SeleniumConfig {

    private final String configName;
    private final Scope scope;
    private final SeleniumDriverType seleniumDriverType;
    private final String chromeArgs;
    private final String remoteWebDriverURL;
    private final String remoteWebDriverBrowserType;

    public SeleniumConfigBean(String configName, Scope scope, SeleniumDriverType seleniumDriverType, String chromeArgs, String remoteWebDriverURL, String remoteWebDriverBrowserType) {
        this.configName = configName;
        this.scope = scope;
        this.seleniumDriverType = seleniumDriverType;
        this.chromeArgs = chromeArgs;
        this.remoteWebDriverURL = remoteWebDriverURL;
        this.remoteWebDriverBrowserType = remoteWebDriverBrowserType;
    }

    public String getConfigName() {
        return configName;
    }

    @Override
    public Scope getScope() {
        return scope;
    }

    @Override
    public SeleniumDriverType getSeleniumDriverType() {
        return seleniumDriverType;
    }

    @Override
    public Optional<String> getChromeArgs() {
        return Optional.ofNullable(chromeArgs);
    }
    
    
    public String getRemoteWebDriverBrowserType() { return remoteWebDriverBrowserType; }

    @Override
    public String getRemoteWebDriverURL() {
        return remoteWebDriverURL;
    }
}
