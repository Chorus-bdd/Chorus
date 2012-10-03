Uses: Processes
Uses: Remoting

Feature: Invalid Handler Config

  Test that invalid configs are discarded with an appropriate warning logged

  Scenario: Invalid config1 Processes Properties
    I can start a config1 process
    And wait for config1 to terminate

  Scenario: Invalid config1 Remoting Properties
    I can call an exported method in config1








