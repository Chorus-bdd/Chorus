Uses: Processes

Feature: Process With Log4j Config In Feature Dir

  Test that if we supply a log4j.xml config in the main feature dir then this is set as a log4j system
  property when the process is launched. Check that system properties feature.dir and feature.process.name
  are also set so log4j configs can reference them

  Scenario: Start a Process with log4j logging
    When I start a config1 process
    And I wait for config1 to stop
    Then log4j has written to the log4jlogs/log4jconfiginfeaturedir-config1.log file
    And I can delete the log file log4jlogs/log4jconfiginfeaturedir-config1.log






