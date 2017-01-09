Uses: Timers
Uses: Web Agent Self Test

Feature: Feature Which Is Pending
  This is a test feature with a scenario which has pending steps. The scenario and feature should take on pending
  state

  Scenario: Scenario Which Has Pending Steps
    Given I run a scenario with several steps
    And one of the steps is marked pending
    Then the subsequent steps are skipped
    And the scenario and feature finish in pending state

  Scenario: Scenario Which Throws Pending Exception
    Given I run a scenario with several steps
    And one of the steps throws a pending exception
    Then the subsequent steps are skipped






