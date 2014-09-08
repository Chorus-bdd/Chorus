Uses: Processes
Uses: Remoting

Feature: Remoting With Local Process

  Check that the remoting config/port can be detected from the process config
  This should work in cases where the process was launched locally by ProcessesHandler
  If we start multiple processes from the same template config, then the remoting ports are auto-incremented

  Scenario: I don't need to define a remoting config for a locally started process
    Given I start a config1 process
    Then I can call a step method exported by the handler in config1


  Scenario: I can connect to multiple config1 processes and the remoting ports auto-increment
    Given I start a config1 process named Bill
    And I start a config1 process named Ben
    And I start a config1 process named Flowerpot
    Then I can call a step and get the jmx port from the handler in Bill
    Then I can call a step and get the jmx port from the handler in Ben
    Then I can call a step and get the jmx port from the handler in Flowerpot











