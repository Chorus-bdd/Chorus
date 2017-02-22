Uses: Timers
Uses: Web Agent Self Test

Feature: Feature With Step Macro
  Test the behaviour when a scenario references a step macro

  Scenario: Single Step Macro
    Given I call a step macro with two steps
    Then I run step three

  Scenario: Nested Step Macro
    Given I call a step macro with a nested macro call
    Then I run step four

  Scenario: Step Macro With Failing Step
    Given I call a step macro with a failing step
    Then subsequent steps are skipped

  Step-Macro: I call a step macro with two steps
    Given I run step one
    And I run step two

  Step-Macro: I call a step macro with a nested macro call
    Given I call a step macro with two steps
    And I run step three

  Step-Macro: I call a step macro with a failing step
    Given I run step one
    And a step fails an assertion
    Then the next macro step is skipped




