
Uses: Timers
Uses: Selenium

  Feature:  Chrome Driver Connection

    Scenario:  I can open Chrome and navigate to a page
      Given I open Chrome
      And I set the chorus context variable pathToTestHtmlFile
      When I navigate to ${pathToTestHtmlFile}
      Then the url is ${pathToTestHtmlFile}


