Uses: Processes
Uses: Remoting

Feature: Default Handler Properties

  Test that it is possible to use the 'default' properties group to set
  properties which are then picked up by all other groups unless overridden

  Scenario: Start a Single Java Process
    I can start a config1 process
    And connect to the config1 process
    And call a remote method
    And stop process config1








