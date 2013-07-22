Uses: Processes

Feature: Create Process Log Directory

  Test that by default a log directory is created for a process, if not specified the log directory
  will be the logs subdirectory of the feature file directory. If createLogDirectory property is
  false then the directory is not created. createLogDirectory is true by default

  Scenario: Create Default Log Directory
    Given Chorus is working properly
    And I start a config1 process
    And I wait for up to 10 seconds for the process named config1 to stop
    Then the logs/createlogdir-config1-out.log file contains a line 1

  Scenario: Create Named Log Directory
    Given Chorus is working properly
    And I start a config2 process
    And I wait for up to 10 seconds for the process named config2 to stop
    Then the config2logs/createlogdir-config2-out.log file contains a line 2

  Scenario: Scenario fails if could not create log directory
    Given Chorus is working properly
    And I start a config3 process
    And I wait for up to 10 seconds for the process named config3 to stop
    #this should fail and log dir should not be created








