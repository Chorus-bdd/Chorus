Configurations: confA, confB

Uses: Processes

Feature: Process With Configurations

 Start a process in two different configurations. Both should inherit the shared properties
 from the main properties file, and override with config-specific properties if a config
 specific properties file exists

  Scenario: Start a Single Java Process
    Given Chorus is working properly
    And I start a config1 process named Frodo
    And I wait for up to 10 seconds for the process Frodo to terminate
    #seems to take up to 1s to start and for for the output to make it to the logs
    Then the process named Frodo has stopped








