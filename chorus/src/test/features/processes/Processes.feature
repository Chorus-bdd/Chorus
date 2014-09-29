
Feature: Processes
  Chorus should be able to stop and start external local processes

  To start a Java processes, it must be defined in a properties file first. The properties file should be in a conf
    directory adjacent to the .feature file and have the "-processes.properties" suffix. The purpose of the properties
    in this file are explained with examples in the configuration for this feature: Processes-processes.properties.

  To run a script the script file should be in the same directory as the .feature file.
   Processes can be named in a step to allow them to be explicitly stopped by a later step in a scenario

  @ChorusSuiteTag
  Scenario: Start and stop a Java process defined in the properties file
    Given I can start an archibald process named archie
    Then I can stop process named archie

  Scenario: Start a named process using a script
    Given I start a processBatchFile process named p1
    And I start a processBatchFile process named p2
    Then I can stop process named p1
    And I can stop process named p2

  Scenario: Start an anonymous process using a script
    First I start a processBatchFile process
    Then I wait for the process named processBatchFile to terminate

  Scenario: Process termination should not cause a test to fail
    Given I start a lemming process named lemmingA
    And I wait for up to 10 seconds for the process named lemmingA to terminate
    Then I can stop process named lemmingA without the test failing