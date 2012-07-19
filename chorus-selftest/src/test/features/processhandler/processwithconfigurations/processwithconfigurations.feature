Configurations: confA, confB

Uses: Processes
Uses: Timers

Feature: Process With Configurations

 Start a process in two different configurations. Both should inherit the shared properties
 from the main properties file, and override with config-specific properties if a config
 specific properties file exists

  Scenario: Start a Single Java Process
    Given Chorus is working properly
    And I start a config1 process named Frodo
    And I wait for 1 second for the process to run
    #seems to take up to 1s to start and for for the output to make it to the logs
    And I can stop the process named Frodo








