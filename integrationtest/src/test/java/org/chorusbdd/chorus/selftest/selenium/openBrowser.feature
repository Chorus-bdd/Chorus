
Uses: Timers
Uses: Selenium

  Feature:  Selenium Integration

    Scenario:  I can open Chrome and navigate to a page
      Given I open Chrome
      When I navigate to http://www.bbc.co.uk
      Then the url is http://www.bbc.co.uk


