Uses: Processes

Feature: Process Check Delay

  This feature waits a brief period after process startup to check that the process has not immediately failed
  with an error condition 
  
  Test that when the process check delay is set positive, a NoClassDefFoundError on process start causes 
  the start process step to fail
  
  When processCheckDelay is set to -1, the feature is turned off so a NoClassDefFoundError results in an exception but 
  the start process step passes

  Scenario: Start a process with process check delay
    Given Chorus is working properly
    And I start a withchecking process named Frodo

  Scenario: Start a process without process check delay
    Given Chorus is working properly
    And I start a withoutchecking process named Samwise

  #! Processes start withoutchecking, withchecking
  Scenario: Start a process with checking using directives
    Given Chorus is working properly
    Then these steps will be skipped







