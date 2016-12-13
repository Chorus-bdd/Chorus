Uses: Processes

Feature: Properties In Feature Directory

  Test handler properties files can be in the local feature directory, instead of the conf subdirectory
  Also test that we can pick up properties from chorus.properties, ${featurename}.properties as well as
  in the process handler specific ${featurename}-processes.properties
  Also test setting the processes logDirectory config property to log out direct to the feature directory
  rather than the logs subdirectory

  Scenario: Start a Single Java Process
    Given Chorus is working properly
    And I start a config1 process
    And I wait for config1 to terminate
    Then there are logs in the local feature directory
    And the std out log contains the string spacewombats








