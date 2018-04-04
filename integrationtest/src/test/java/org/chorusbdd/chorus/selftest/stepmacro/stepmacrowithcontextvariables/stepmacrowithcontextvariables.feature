Uses: Chorus Context

Feature: Step Macro With Context Variables

  Test the corner cases when you combine context variable expansions in steps with step macros and scenario outlines.
  Here the expansion of the variable is delayed until the step macro step steps are executed.

  This means we are finding step macros based on the unexpanded / pre-expansion parent step definition
  This is at present inevitable because step macro identification is a 'compile time / parse time' operation,
  and at this point we cannot know the values of variables in the context

  This does at least mean that we fix the steps to run in advance, and during a 'dry run' all steps which will be
  executed are shown.

  Scenario: Simple Step Macro Scenario
    Given I create a context variable myVar with value wibble
    Then I can call a step macro with two variables 1234 and ${myVar}

  Scenario-Outline: Step Macro With Outline
    Given I create a context variable myVar with value wibble
    Then I can call a step macro with two variables <group1> and <group2>

  Examples:
  | group1 | group2      |
  | 123    | ${myVar}    |

  Step-Macro: I can call a step macro with two variables <my-var1> and <my_var2>
    Then I can call a handler step with group <my-var1>
    And I can call a handler step with group <my_var2>

