Feature: Multiple Scenario

  Test that multiple scenario in a feature can run successfully
  If a step fails or is not implemented, the next scenario should still run

  Scenario: Simple Scenario One
    Given Chorus is working properly
    Then I can run a step in scenario one
    And if a step fails
    Then the subsequent steps are skipped
    But the next scenario still runs

  Scenario: Simple Scenario Two
    Given Chorus is working properly
    Then I can run a step in scenario two
    And if a step is not implemented
    Then the subsequent steps are skipped
    But the next scenario still runs

  Scenario: Simple Scenario Three
    Given Chorus is working properly
    Then I can run a step in scenario three

  Scenario: Simple Scenario Four
    Given Chorus is working properly
    Then I can run a step in scenario four

