

Feature: Step Macro Directives

  Directives work within step macros

  Scenario: Directive Before Scenario
    First I run a Step Macro Standalone
    Then I run a Step Macro In Feature


   #! Directive One
   Step-Macro: I run a Step Macro In Feature
      Given macro step 1                            #! Directive Two
      When macro step 2                             #! Directive Three    #! Directive Four
      Then I run a Step Macro Standalone




