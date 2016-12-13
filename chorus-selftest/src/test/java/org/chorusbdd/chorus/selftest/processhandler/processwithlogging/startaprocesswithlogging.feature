Uses: Processes

Feature: Start A Process With Logging

  Test that we can use the Processes handler to start a process

  Scenario: Start a Single Java Process
    Given Chorus is working properly
    And I start a config1 process named Frodo
    And I wait for up to 10 seconds for the process named Frodo to stop
    Then the logs/startaprocesswithlogging-Frodo-out.log file contains a line 1
    And the logs/startaprocesswithlogging-Frodo-out.log file contains a line Woohoo, we have started a process
    And the logs/startaprocesswithlogging-Frodo-err.log file contains a line Eeek, an error might have occurred








