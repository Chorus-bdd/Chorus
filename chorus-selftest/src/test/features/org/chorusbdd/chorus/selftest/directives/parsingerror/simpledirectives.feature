

Feature: No New Line Directives Between Steps

  During a scenario directives can be appended to step lines but cannot appear on their own line
  Otherwise it would not be clear if a directive which appears last within a scenario
  applies to that scenario or to the following Scenario: statement

  #! Directive One is OK
  Scenario: Directive Before Scenario
    Given I can run a Step      #! Directive Two is OK
    #! Directive Three This should cause a parser error
    When I can run a Step
    Then I can run a Step
