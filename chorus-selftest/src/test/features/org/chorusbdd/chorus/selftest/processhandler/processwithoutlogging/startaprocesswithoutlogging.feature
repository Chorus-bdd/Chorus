Uses: Processes

Feature: Start A Process Without Logging

  Test that we can use the Processes handler to start a process and if we don't specify logging property
  or set it to false then the standard out/err from the process appears inline with the interpreter std
  out and err

  Scenario: Start a Single Java Process
    Given Chorus is working properly
    And I start a config1 process named Frodo
    And I wait for the process named Frodo to terminate
    Then the process named Frodo has terminated








