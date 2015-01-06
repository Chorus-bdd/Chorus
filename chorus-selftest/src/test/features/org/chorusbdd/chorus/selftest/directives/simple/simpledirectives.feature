

Feature: Simple Directives

  Directives can be placed before a Scenario: statement or appended to a step

  #! Directive One
  Scenario: Directive Before Scenario
    Given I can run a Step
    When I can run a Step
    Then I can run a Step

  #! Directive One
  #! Directive Two
  Scenario: Two Directives Before Scenario
    Given I can run a Step
    When I can run a Step
    Then I can run a Step

  #! Directive One   #! Directive Two
  Scenario: Two Directives Before Scenario combined in a line
    Given I can run a Step
    When I can run a Step
    Then I can run a Step

  #! Directive One
  # Blank line or comments here make no difference, the directive still gets associated with the next scenario

  #!    Directive Two
  Scenario: Two Directives Before Scenario with intervening blank line and comment
    Given I can run a Step
    When I can run a Step
    Then I can run a Step


  Scenario: Directives after a step get run before the step
    Given I can run a Step        #! Directive One
    When I can run a Step
    Then I can run a Step

  Scenario: Two Directives after a step get inserted before the step
    Given I can run a Step        #! Directive One
    When I can run a Step         #! Directive One   #! Directive Two
    Then I can run a Step









