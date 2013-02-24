Feature: Feature Local Step Macro

  Test that Chorus can parse a StepMacro from a feature file and I can call the step macro from a Scenario step

  Scenario: Simple Scenario
    Given Chorus is working properly
    Then I can call a step macro with two capture groups 1234 and mytestgroup


  StepMacro: I can call a step macro with two capture groups (\d+) and (.*)
    Then I can call a handler step with group <$1>
    And I can call a handler step with group <$2>

