

#! This directive should cause a parser error
Feature: Simple Directives

  Putting a directive before a Feature: statement will cause a parsing error

  #! Directive One
  Scenario: Directive Before Scenario
    Given I can run a Step
    When I can run a Step
    Then I can run a Step









