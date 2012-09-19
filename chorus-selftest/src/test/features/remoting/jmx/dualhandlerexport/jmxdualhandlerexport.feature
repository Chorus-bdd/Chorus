Uses: Processes
Uses: Timers
Uses: Remoting

Feature: Jmx Dual Handler Export

  Test that we can use the Jmx exporter to export a two handlers
  Test behaviour when those handler have conflicting step definitions

  Scenario: Call An Exported Method
    Given I start a config1 process named DualHandlerExport
    Then I can call a step method exported by the handler in DualHandlerExport

  Scenario: Call A Conflicting Method
      Given I start a config1 process named DualHandlerExport
      And I call a step method exported by handler one in DualHandlerExport
      And I call a step method exported by handler two in DualHandlerExport
      And I call a step method exported by both handlers in DualHandlerExport
      Then the ambiguity is logged and one of the matching steps is called in DualHandlerExport
      #TODO we should tighten the contract here to make it fully deterministic which definition will be used








