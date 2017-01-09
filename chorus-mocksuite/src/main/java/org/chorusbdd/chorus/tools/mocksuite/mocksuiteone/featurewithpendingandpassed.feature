Uses: Timers
Uses: Web Agent Self Test

Feature: Feature With Pending And Passed
  This is a test feature a pending and a passed scenario. Feature state should end as Pending

  Scenario: Scenario Which Passes
    Given I run a scenario with a single test step which passes

  Scenario: Scenario Which Is Pending
    Given I run a scenario with several steps
    And one of the steps is marked pending
    Then the subsequent steps are skipped
    And the scenario and feature finish in pending state




