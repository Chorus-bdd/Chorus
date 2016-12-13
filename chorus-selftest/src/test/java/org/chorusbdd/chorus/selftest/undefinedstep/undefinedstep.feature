Feature: Undefined Step

  Test that if a step is not defined by a handler the step is labelled undefined,
  subsequent steps are skipped and return code is non-zero

  Scenario: Simple Scenario
    Given Chorus is working properly
    Then I can run a feature with a single scenario
    And if a step is undefined
    Then the subsequent step is skipped

