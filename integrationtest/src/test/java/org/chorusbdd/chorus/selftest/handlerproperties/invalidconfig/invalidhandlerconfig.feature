Uses: Processes
Uses: Remoting

Feature: Invalid Handler Config

  Test that invalid configs are discarded with an appropriate warning logged

  Scenario: Invalid config1 Processes Properties
    Given I start a config1 process
    And I wait for config1 to terminate

  Scenario: Invalid config1 Remoting Properties
    Given I can connect to the config1 process
    And I can call an exported method








