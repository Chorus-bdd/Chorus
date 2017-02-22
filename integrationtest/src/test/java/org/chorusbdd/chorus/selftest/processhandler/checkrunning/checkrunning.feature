Uses: Processes
Uses: Timers

Feature: Check Running

  Test that we can use the Processes handler to start a process and check that it is running

  Scenario: Start a Single Java Process
    Given Chorus is working properly
    And I start a config1 process named Frodo
    And I wait 100 milliseconds for the process to continue running
    Then the process named Frodo is running








