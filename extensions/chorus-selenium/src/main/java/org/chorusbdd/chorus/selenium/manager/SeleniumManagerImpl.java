package org.chorusbdd.chorus.selenium.manager;

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
import org.chorusbdd.chorus.util.assertion.ChorusAssert;
import org.chorusbdd.chorus.util.assertion.ChorusAssertionError;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.format;
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
        quitBrowser(driver);
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
        if ( script.canRead()) {
            try {
                log.debug(format("About to execute script at %s in browser %s", script.getAbsolutePath(), configName ));
                String fileContents = Files.lines(script.toPath()).collect(Collectors.joining());
                log.trace(format("About to execute script on web driver %s: [%n%s%n]", configName, fileContents));
                ((JavascriptExecutor)getWebDriver(configName)).executeScript(fileContents);
                log.debug("Finished executing script from " + scriptPath);
            } catch (IOException e) {
                throw new ChorusException("Failed while reading script file", e);
            }
        } else {
            throw new ChorusException("Cannot find a script file [" + scriptPath + "] at [" + script.getAbsolutePath() + "], or it is not readable");
        }
    }

    private void quitBrowser(NamedWebDriver d) {
        if (d.isLeaveOpen()) {
            log.debug("Not Quitting the browser for config named " + d.getName() + " since leave open is set true");
        } else {
            log.debug("Quitting the browser for config named " + d.getName());
            d.getWebDriver().quit();
        }
    }
    
    private WebDriver getWebDriver(String configName) {
        return getNamedWebDriver(configName).getWebDriver();
    }

    private NamedWebDriver getNamedWebDriver(String name) {
        String c = SeleniumManager.LAST_OPENED_BROWSER.equals(name) ? lastOpenedBrowserConfigName : name;
        
        Optional<NamedWebDriver> n = Optional.ofNullable(webDriverMap.get(c));
        return n.orElseThrow(() -> new ChorusAssertionError("A WebDriver for a browser with the config name " + c + " does not exist"));
    }


    @Override
    public ExecutionListener getExecutionListener() {
        return new ExecutionListenerAdapter() {
            
            public void featureStarted(ExecutionToken testExecutionToken, FeatureToken feature) {
                SeleniumManagerImpl.this.feature = feature;
            }
            
            public void scenarioCompleted(ExecutionToken testExecutionToken, ScenarioToken scenario) {
                removeWebDriversForScope(Scope.SCENARIO);
            }


            public void featureCompleted(ExecutionToken testExecutionToken, FeatureToken feature) {
                removeWebDriversForScope(Scope.FEATURE);
            }

            private void removeWebDriversForScope(Scope scope) {
                List<NamedWebDriver> l = getNamedWebDriversWithScope(scope);
                if ( l.size() > 0) {
                    log.trace("Removing these Selenium WebDrivers at " + scope + " end: " + l);
                }

                l.forEach(d -> {
                    quitBrowser(d);
                    removeWebDriver(d.getName());
                });
            }

            private List<NamedWebDriver> getNamedWebDriversWithScope(Scope scope) {
                return webDriverMap
                        .values()
                        .stream()
                        .filter(wd -> wd.getSeleniumConfig().getScope() == scope)
                        .collect(Collectors.toList());
            }

        };
    }


    private SeleniumConfig getSeleniumConfig(String configName, Properties processProperties) {
        SeleniumConfigBuilder config = seleniumConfigBuilderFactory.createConfigBuilder(processProperties, configName);
        return config.build();
    }

}
