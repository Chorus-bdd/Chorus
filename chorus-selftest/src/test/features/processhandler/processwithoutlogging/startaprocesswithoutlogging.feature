Uses: Processes
Uses: Timers

Feature: Start A Process Without Logging

  Test that we can use the Processes handler to start a process and if we don't specify logging property or set it to false then the standard out/err from the process appears inline with the interpreter std out and err

  Scenario: Start a Single Java Process
    Given Chorus is working properly
    And I start a config1 process named Frodo
    And I wait for 1 second for the process to run
    #seems to take up to 1s to start and for for the output to make it to the logs
    And I can stop the process named Frodo








