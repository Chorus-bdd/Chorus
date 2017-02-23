Feature: Dry Run

  Test that Dry Run does not execute steps, only locates them
  Missing steps still cause subsequent steps to be skipped (debatable, whether that's good)
  The output for matched steps prints 'This step is OK'

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

