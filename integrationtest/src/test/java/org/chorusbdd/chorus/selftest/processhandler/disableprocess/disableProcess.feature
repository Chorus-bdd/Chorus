Uses: Processes

Feature: Disable a process

  Test that we can use the Processes handler to start a process

  Scenario: Start a Disabled Process
    Given I start a disabled process
    Then the process named disabled is not running





