package org.chorusbdd.chorus.selenium.manager;

import org.chorusbdd.chorus.annotations.ExecutionPriority;
import org.chorusbdd.chorus.annotations.Scope;
import org.chorusbdd.chorus.executionlistener.ExecutionListener;
import org.chorusbdd.chorus.executionlistener.ExecutionListenerAdapter;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.results.ExecutionToken;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.results.ScenarioToken;
import org.chorusbdd.chorus.selenium.config.SeleniumConfig;
import org.chorusbdd.chorus.selenium.config.SeleniumConfigBuilder;
import org.chorusbdd.chorus.selenium.config.SeleniumConfigBuilderFactory;
import org.chorusbdd.chorus.selenium.config.SeleniumConfigBeanValidator;
import org.chorusbdd.chorus.util.ChorusException;
import org.chorusbdd.chorus.util.FileUtils;
import org.chorusbdd.chorus.util.assertion.ChorusAssert;
import org.chorusbdd.chorus.util.assertion.ChorusAssertionError;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static org.chorusbdd.chorus.util.assertion.ChorusAssert.assertEquals;

/**
 * Created by nickebbutt on 05/02/2018.
 */
public class SeleniumManagerImpl implements SeleniumManager {

    private ChorusLog log = ChorusLogFactory.getLog(SeleniumManagerImpl.class);

    private final SeleniumConfigBuilderFactory seleniumConfigBuilderFactory = new SeleniumConfigBuilderFactory();
    private final SeleniumConfigBeanValidator seleniumConfigBeanValidator = new SeleniumConfigBeanValidator();
    private final WebDriverFactory webDriverFactory = new DefaultWebDriverFactory();
    
    private Map<String, NamedWebDriver> webDriverMap = new LinkedHashMap<>();
    
    private String lastOpenedBrowserConfigName = "N/A";

    private FeatureToken feature;

    private CleanupShutdownHook cleanupShutdownHook = new CleanupShutdownHook();

    public SeleniumManagerImpl() {
        addShutdownHook();
    }
    
    private void addShutdownHook() {
        log.trace("Adding shutdown hook for SeleniumManager " + this);
        Runtime.getRuntime().addShutdownHook(cleanupShutdownHook);
    }
    

    @Override
    public void openABrowser(Properties properties, String configName) {
        SeleniumConfig seleniumConfig = createAndValidateConfig(properties, configName);
        
        //creating the web driver has the effect of opening the browser
        WebDriver webDriver = webDriverFactory.createWebDriver(seleniumConfig);
        NamedWebDriver namedWebDriver = new NamedWebDriver(configName, seleniumConfig, webDriver);
        addNamedWebDriver(configName, namedWebDriver);
    }

    private SeleniumConfig createAndValidateConfig(Properties properties, String configName) {
        SeleniumConfig seleniumConfig = getSeleniumConfig(configName, properties);
        checkConfigValidAndNotAlreadyOpened(configName, seleniumConfig);
        return seleniumConfig;
    }

    private void addNamedWebDriver(String configName, NamedWebDriver namedWebDriver) {
        log.debug("Adding named web driver " + namedWebDriver);
        webDriverMap.put(configName, namedWebDriver);
        this.lastOpenedBrowserConfigName = configName;
    }

    private void removeWebDriver(String configName) {
        log.debug("Removing named web driver " + configName );
        webDriverMap.remove(configName);
    }


    private void checkConfigValidAndNotAlreadyOpened(String configName, SeleniumConfig seleniumConfig) {
        ChorusAssert.assertFalse(
            "There is already a browser open with the config name " + configName, webDriverMap.containsKey(configName)
        );
        
        boolean valid = seleniumConfigBeanValidator.isValid(seleniumConfig);
        if ( ! valid) {
            log.warn(seleniumConfigBeanValidator.getErrorDescription());
            throw new ChorusException("The selenium config for " + configName + " must be valid");
        }
    }

    @Override
    public void navigateTo(String configName, String url) {
        WebDriver driver = getWebDriver(configName);
        driver.navigate().to(url);
    }

    @Override
    public void refreshThePage(String configName) {
        WebDriver driver = getWebDriver(configName);
        driver.navigate().refresh();
    }

    @Override
    public void checkCurrentURL(String configName, String url) {
        WebDriver driver = getWebDriver(configName);
        String currentUrl = driver.getCurrentUrl();
        //ChromeDriver adds a trailing /
        if ( currentUrl.endsWith("/")) {
            currentUrl = currentUrl.substring(0, currentUrl.length() - 1);
        }
        assertEquals(url, currentUrl);
    }

    @Override
    public void quitBrowser(String configName) {
        NamedWebDriver driver = getNamedWebDriver(configName);
        quitAndRemoveWebDrivers(singletonList(driver));
    }

    @Override
    public void leaveBrowserOpenAtFeatureEnd(String configName) {
        NamedWebDriver webDriver = getNamedWebDriver(configName);
        webDriver.setLeaveOpen(true);
    }

    @Override
    public void executeScriptFile(String configName, String scriptPath) {
        //Resolve the script path relative to the feature file
        File script = feature.getFeatureDir().toPath().resolve(scriptPath).toFile();
        String scriptContents = FileUtils.readScriptFile(log, configName, scriptPath, script);

        log.trace(format("About to execute script on web driver %s: [%n%s%n]", configName, scriptContents));
        ((JavascriptExecutor)getWebDriver(configName)).executeScript(scriptContents);
        log.debug("Finished executing script from " + scriptPath);
    }
    
    public WebDriver getWebDriver(String configName) {
        return getNamedWebDriver(configName).getWebDriver();
    }

    private NamedWebDriver getNamedWebDriver(String name) {
        String c = SeleniumManager.LAST_OPENED_BROWSER.equals(name) ? lastOpenedBrowserConfigName : name;
        
        Optional<NamedWebDriver> n = Optional.ofNullable(webDriverMap.get(c));
        return n.orElseThrow(() -> new ChorusAssertionError("A WebDriver for a browser with the config name " + c + " does not exist"));
    }


    @Override
    public ExecutionListener getExecutionListener() {
        
        @ExecutionPriority(ExecutionPriority.SELENIUM_MANAGER_PRIORITY)
        class SeleniumExecutionListenerAdapter extends ExecutionListenerAdapter {

            @Override
            public void featureStarted(ExecutionToken testExecutionToken, FeatureToken feature) {
                SeleniumManagerImpl.this.feature = feature;
            }

            @Override
            public void scenarioCompleted(ExecutionToken testExecutionToken, ScenarioToken scenario) {
                removeWebDriversForScope(Scope.SCENARIO);
            }

            @Override
            public void featureCompleted(ExecutionToken testExecutionToken, FeatureToken feature) {
                removeWebDriversForScope(Scope.FEATURE);
            }

            private void removeWebDriversForScope(Scope scope) {
                List<NamedWebDriver> l = getNamedWebDriversWithScope(scope);
                if ( l.size() > 0) {
                    log.trace("Removing these Selenium WebDrivers at " + scope + " end: " + l);
                }

                quitAndRemoveWebDrivers(l);
            }

            private List<NamedWebDriver> getNamedWebDriversWithScope(Scope scope) {
                return webDriverMap
                        .values()
                        .stream()
                        .filter(wd -> wd.getSeleniumConfig().getScope() == scope)
                        .collect(Collectors.toList());
            }

        };
        return new SeleniumExecutionListenerAdapter();
    }

    private void quitAndRemoveAllWebDrivers() {
        log.debug("Quitting and removing all web drivers");
        List<NamedWebDriver> webDrivers = new LinkedList<>(webDriverMap.values());
        quitAndRemoveWebDrivers(webDrivers);
    }

    private void quitAndRemoveWebDrivers(List<NamedWebDriver> l) {
        l.forEach(d -> {
            log.trace("Quitting and removing web driver named " + d.getName());
            try {
                quitBrowser(d);
            } finally {
                removeWebDriver(d.getName());
            }
        });
    }

    private void quitBrowser(NamedWebDriver d) {
        if (d.isLeaveOpen()) {
            log.debug("Not Quitting the browser for config named " + d.getName() + " since leave open is set true");
        } else {
            log.debug("Quitting the browser for config named " + d.getName());
            d.getWebDriver().quit();
        }
    }

    private SeleniumConfig getSeleniumConfig(String configName, Properties processProperties) {
        SeleniumConfigBuilder config = seleniumConfigBuilderFactory.createConfigBuilder(processProperties, configName);
        return config.build();
    }

    /**
     * If shut down before a scenario completes, try as hard as we can to cleanly close down any open web drivers
     * Not doing so can leave selenium hub in an inoperable state
     */
    private class CleanupShutdownHook extends Thread {
        public void run() {
            log.debug("Running Cleanup on shutdown for Selenium Manager");
            try {
                quitAndRemoveAllWebDrivers();
            } catch (Throwable t) {
                log.debug("Failed during cleanup", t);
            }
        }
    }
    

}
