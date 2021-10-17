Uses: HandlerA
Uses: HandlerB

@tagA @tagB
Feature: Test Feature

  Scenario: Test Scenario One
    Given this is step 1
    When this is step 2
    Then this is step 3

  @tagC @tagD @tagE
  Scenario: Test Scenario Two
    Given this is step 1
    When this is step 2
    Then this is step 3

  @tagF
  Scenario-Outline: Test Scenario Three
    Given this is step 1
    When this is step 2
    Then this is step 3

    Examples:
        | Header A | Header B |
        | dataA1   | dataB1   |
        | dataA2   | dataB2   |
        | dataA3   | dataB3   |
        | dataA4   | dataB4   |
