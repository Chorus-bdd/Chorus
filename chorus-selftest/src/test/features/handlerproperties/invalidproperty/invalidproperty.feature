Uses: Processes
Uses: Remoting
#nb doesnt actually use Remoting but this causes the remoting props to be read

Feature: Invalid Properties

  Test that invalid property settings are logged as such and that default properties are picked up

  Scenario: Start a Single Java Process
    I can start a config1 process
    And wait for config1 to terminate








