
Uses: Simple Directives

Feature: Background Directives

  Directives associated with a Background section are inherited by all scenarios

  #! Directive One
  #! Directive Two  #! Directive Three
  Background:
    Given I can run a Background Step

  #! Directive Four
  Scenario: Directive With Background
    Given I can run a Step
    When I can run a Step
    Then I can run a Step

  #! Directive Four
  #! Directive Five
  Scenario: Directive With Background And Secnario Directives
    Given I can run a Step      #! Directive Six
    When I can run a Step
    Then I can run a Step





