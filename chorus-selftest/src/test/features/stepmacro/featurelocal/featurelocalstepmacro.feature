Feature: Feature Local Step Macro

  Test that Chorus can parse a StepMacro from a feature file and I can call the step macro from a Scenario step

  Scenario: Simple Step Macro Scenario
    Given Chorus is working properly
    Then I can call a step macro with two variables 1234 and mytestgroup

  Scenario-Outline: Step Macro With Outline
    Given Chorus is working properly
    Then I can call a step macro with two variables <group1> and <group2>

  Examples:
    | group1 | group2 |
    | 123  | test1    |
    | 456  | test2    |

  StepMacro: I can call a step macro with two variables <myvar1> and <myvar2>
    Then I can call a handler step with group <myvar1>
    And I can call a handler step with group <myvar2>

