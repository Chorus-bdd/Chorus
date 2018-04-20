Uses: Processes
Uses: Remoting

Feature: Jmx Dual Handler Export

  Test that we can use the Jmx exporter to export a two handlers
  Test behaviour when those handler have conflicting step definitions

  Scenario: Call An Exported Method
    Given I start a config1 process named DualHandlerExport
    And I connect to the DualHandlerExport process
    Then I can call a step method exported by the handler
    And I can stop process DualHandlerExport

  Scenario: Call A Conflicting Method
    Given I start a config1 process named DualHandlerExport
    And I connect to the DualHandlerExport process
    And I call a step method exported by handler one
    And I call a step method exported by handler two
    And I call a step method exported by both handlers
    Then a ChorusException is thrown with a message which reports the ambiguity








