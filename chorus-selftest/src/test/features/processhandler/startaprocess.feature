Uses: Processes
Uses: Timers

Feature: Start A Process

  Test that we can use the Processes handler to start a process

  Scenario: Start a Single Java Process
    Given Chorus is working properly
    And I start a config1 process named Frodo
    And I wait for 50 milliseconds for the process to start
    Then I can stop the process named Frodo







