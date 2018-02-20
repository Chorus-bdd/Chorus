package org.chorusbdd.chorus.selenium.manager;

import org.chorusbdd.chorus.annotations.SubsystemConfig;
import org.chorusbdd.chorus.subsystem.Subsystem;

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

    void executeScriptFile(String configName, String scriptPath);
}
