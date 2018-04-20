Uses: Processes
Uses: Remoting

Feature: Remoting With Local Process

  Check that the remoting config/port can be detected from the process config
  This should work in cases where the process was launched locally by ProcessesHandler
  If we start multiple processes from the same template config, then the remoting ports are auto-incremented

  #! Processes start config1
  #! Processes connect config1
  Scenario: I don't need to define a remoting config for a locally started process
    Check I can call a step method exported by the 18806 handler

  Scenario: I can connect to multiple config1 processes and the remoting ports auto-increment
    Given I start a config1 process named Bill
    And I start a config1 process named Ben
    And I start a config1 process named Flowerpot
    Then I can call a step and get the jmx port from the 18806 handler          #! Processes connect Bill, Ben, Flowerpot
    And I can get the debug port for Bill from processes manager
    Then I can call a step and get the jmx port from the 18807 handler
    And I can get the debug port for Ben from processes manager
    Then I can call a step and get the jmx port from the 18808 handler
    And I can get the debug port for Flowerpot from processes manager

  Scenario: Remoting and debug ports do not auto-increment if no initial port set in template configuration
    Given I start a withoutRemotingOrDebug process named Bill
    And I start a withoutRemotingOrDebug process named Ben
    Then I can get the debug port for Bill from processes manager
    And I can get the debug port for Ben from processes manager
    And I can get the jmx port for Bill from processes manager
    And I can get the jmx port for Ben from processes manager












