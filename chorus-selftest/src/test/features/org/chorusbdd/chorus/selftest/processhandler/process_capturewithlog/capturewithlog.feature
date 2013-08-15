Uses: Processes
Uses: Chorus Context

Feature: Capture With Log

  Test that when in capturewithlog mode, process output which Chorus reads is logged to a file
    
  Scenario: When in capturewithlog mode log files are written
    Given I start a capturewithlog process 
    When I read the line 'and another longer line' from the capturewithlog process
    And I read the line 'an error has occurred' from the capturewithlog process std error
    Then the std out log contains the string let's match a line
    Then the std out log contains the string and another longer line 
    And the std err log contains the string an error has occurred
    
    



