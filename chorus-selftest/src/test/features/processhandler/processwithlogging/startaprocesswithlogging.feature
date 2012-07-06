Uses: Processes
Uses: Timers

Feature: Start A Process With Logging

  Test that we can use the Processes handler to start a process

  Scenario: Start a Single Java Process
    Given Chorus is working properly
    And I start a config1 process named Frodo
    And I wait for 1 second for the process to run
    #seems to take up to 1s to start and for for the output to make it to the logs
    And I can stop the process named Frodo
    Then the logs/startaprocesswithlogging-Frodo-out.log file contains a line 1
    And the logs/startaprocesswithlogging-Frodo-out.log file contains a line Woohoo, we have started a process
    And the logs/startaprocesswithlogging-Frodo-err.log file contains a line Eeek, an error might have occurred








