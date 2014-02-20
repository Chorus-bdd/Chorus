Feature: Step Macro Duplicate Match

  Test that Chorus warns when a duplicate step macro exists

  Scenario: Simple Step Macro Scenario
    Given Chorus is working properly
    Then I can call a step macro with two variables 1234 and mytestgroup

  Step-Macro: I can call a step macro with two variables <my-var1> and <my_var2>
    Then I can call a handler step with group <my-var1>
    And I can call a handler step with group <my_var2>

  Step-Macro: I can call a step macro with two variables 1234 and <my_var2>
    Then I can call a handler step with group <my-var1>
    And I can call a handler step with group <my_var2>
