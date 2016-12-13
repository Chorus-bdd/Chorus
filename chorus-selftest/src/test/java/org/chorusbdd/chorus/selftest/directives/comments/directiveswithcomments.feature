

Feature: Directives With Comments

  Directives can be commented out

  #! Directive One ##! Directive Two
  # #! Directive Three
  ##! Directive Four
  Scenario: Directive Before Scenario
    Given I can run a Step
    When I can run a Step
    Then I can run a Step

  Scenario: Two Directives Before Scenario
    Given I can run a Step  #! Directive One # #!Directive Two
    When I can run a Step   # #! Directive Three
    Then I can run a Step   ##! Directive Four









