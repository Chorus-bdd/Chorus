Uses: Timers
Uses: Web Agent Self Test

Feature: Feature With Pending Passed And Failed Scenario
  This is a test feature a pending, passed and failed scenario. Feature state should end as Failed

  Scenario: Scenario Which Passes
    Given I run a scenario with a single test step which passes

  Scenario: Scenario Which Is Pending
    Given I run a scenario with several steps
    And one of the steps is marked pending
    Then the subsequent steps are skipped
    And the scenario and feature finish in pending state

  Scenario: Scenario Which Fails With Undefined Steps
    Given I run a scenario with several steps
    And at least one step is undefined
    Then the scenario will fail horribly
    And show up in red in the web agent html




