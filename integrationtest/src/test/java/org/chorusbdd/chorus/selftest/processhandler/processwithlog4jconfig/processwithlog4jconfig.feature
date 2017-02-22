Uses: Processes

Feature: Process With Log4j

  Test that if we supply a log4j.xml config in the conf sudirectory then this is set as a log4j system
  property when the process is launched. Check that system properties feature.dir and feature.process.name
  are also set so log4j configs can reference them

  Scenario: Start a Process with log4j logging
    When I start a config1 process
    And I wait for config1 to stop
    Then log4j has written to the processwithlog4jconfig-config1.log file
    And I can delete the log file processwithlog4jconfig-config1.log

  Scenario: Start two Processes with log4j logging
    #also testing that the second un-named process picks up the suffix -2
    When I start a config1 process
    And I start a config1 process
    And I wait for config1 to terminate
    And I wait for config1-2 to terminate
    Then log4j has written to the processwithlog4jconfig-config1.log file
    And I can delete the log file processwithlog4jconfig-config1.log
    And log4j has written to the processwithlog4jconfig-config1-2.log file
    And I can delete the log file processwithlog4jconfig-config1-2.log






