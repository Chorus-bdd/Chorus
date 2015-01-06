
Uses: Simple Directives
Uses: Chorus Context

Feature: Directives With Context Variables

  Context variables can be expanded within directives

  Scenario: Directives can expand context variables
    Given I create a context variable myVar with the value One
    Then I can run a Step                                           #! Directive ${myVar}










