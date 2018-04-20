Uses: Processes
Uses: Remoting


Feature: Feature Start Scoping

  Test that processes started and remoting connections made in feature-start: are scoped to feature
  not scenario, unless the scope is specifically set in config

  Feature-Start:
    When I run feature start
    And I start a notScopedProcess process named myNotScoped
    And I start a scopedProcess process named myScopedToScenario
    And I connect to the processes myNotScoped, myScopedToScenario
  
  Scenario: Scenario One
    Then the process named myNotScoped is running
    And the process named myScopedToScenario is not running
    And I can call an exported method on myNotScoped


