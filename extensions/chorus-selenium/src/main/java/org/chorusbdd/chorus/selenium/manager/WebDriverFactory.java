package org.chorusbdd.chorus.selenium.manager;

import org.chorusbdd.chorus.selenium.config.SeleniumConfig;
import org.openqa.selenium.WebDriver;

/**
 * Created by nickebbutt on 12/02/2018.
 */
public interface WebDriverFactory {
    
    WebDriver createWebDriver(SeleniumConfig seleniumConfig);
}
