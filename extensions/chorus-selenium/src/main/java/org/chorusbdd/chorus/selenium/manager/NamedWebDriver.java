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
import org.openqa.selenium.WebDriver;

/**
 * Created by nickebbutt on 12/02/2018.
 */
public class NamedWebDriver {
    
    private final String name;
    private SeleniumConfig seleniumConfig;
    private final WebDriver webDriver;
    private boolean leaveOpen;

    public NamedWebDriver(String name, SeleniumConfig seleniumConfig, WebDriver webDriver) {
        this.name = name;
        this.seleniumConfig = seleniumConfig;
        this.webDriver = webDriver;
    }

    public String getName() {
        return name;
    }

    public WebDriver getWebDriver() {
        return webDriver;
    }

    public SeleniumConfig getSeleniumConfig() {
        return seleniumConfig;
    }

    /**
     * If this is set, do not quit the browser at the end of a feature
     * This can be useful for debugging failing features
     */
    public boolean isLeaveOpen() {
        return leaveOpen;
    }

    public void setLeaveOpen(boolean leaveOpen) {
        this.leaveOpen = leaveOpen;
    }

    @Override
    public String toString() {
        return "NamedWebDriver{" +
                "name='" + name + '\'' +
                ", seleniumConfig=" + seleniumConfig +
                ", webDriver=" + webDriver +
                ", leaveOpen=" + leaveOpen +
                '}';
    }
}
