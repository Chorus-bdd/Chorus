Uses: Processes
Uses: Timers

Feature: Start A Process With Classpath

  Check that we can set the classpath when starting a process

  Scenario: Start a Single Java Process
    Given Chorus is working properly
    And I start a config1 process named Frodo
    And I wait for 1 second for the process to run
    #seems to take up to 1s to start and for for the output to make it to the logs
    And I can stop the process named Frodo








