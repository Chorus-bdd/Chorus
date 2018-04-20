Configurations: config1, config2

Uses: Processes
Uses: Remoting

Feature: Jmx With Configurations

  Test that configurations work with properties files
  In this case the base configuration (used for config1) should fail to connect, the override config2 should work

  Scenario: Config Properties Override Main Properties For Jmx Remoting
    Given I start a config1 process named SingleHandlerExport
    And I connect to the SingleHandlerExport process
    Then I can call a step method exported by the handler








