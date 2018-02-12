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
