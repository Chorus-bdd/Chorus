
Uses: Timers
Uses: Selenium
Uses: CheckCustomWebDriver  

  Feature:  Set a custom WebDriverFactory for Selenium Handler

    Scenario:  I can set a custom WebDriverFactory implementation
      Given I open the customBrowser browser
      Then the custom WebDriverFactory was instantiated


