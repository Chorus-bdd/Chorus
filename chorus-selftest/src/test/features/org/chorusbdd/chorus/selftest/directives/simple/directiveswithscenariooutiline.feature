
Uses: Simple Directives

Feature: Directives With Scenario Outline

  Directives can be placed before a Scenario-Outline: and can use variables from the Examples table


  #! Directive <var1>
  Scenario-Outline: A Scenario Outline can use directives
    Given I can run a Step <var1> #! Directive <var1>

    Examples:
    | var1 |
    | One  |
    | Two  |










