
Uses: Timers
Uses: Selenium

  Feature:  Edge Driver Connection

    Scenario:  I can open Edge and navigate to a page
      Given I open the EDGE browser
      And I set the chorus context variable pathToTestHtmlFile
      When I navigate to ${pathToTestHtmlFile}
      Then the url is ${pathToTestHtmlFile}
      And I close the browser


