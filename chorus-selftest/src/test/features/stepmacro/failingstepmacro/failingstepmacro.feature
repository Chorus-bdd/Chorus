Feature: Failing Step Macro

  When a step macro step fails, the step macro steps are expanded and displayed in chorus' output

  Scenario: Simple Step Macro Scenario
    Given Chorus is working properly
    Then I can call a step macro with two variables 1234 and mytestgroup
    And Chorus is working properly

  Step-Macro: I can call a step macro with two variables <myvar1> and <myvar2>
    Then I can call a handler step with group <myvar1>
    And I can call a handler step which is unimplememted and this will fail
    And I can call a step which is unimplemented and this will be skipped

