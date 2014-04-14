Uses: Chorus Context

Feature: Check Chorus Context Handler

  Test that we can use the ChorusContext to save and recall local variables during a scenario
  Test that the context is cleared down between scenario

  Scenario: Test Context Variables
    Given Chorus is working properly
    And the context has no values in it
    And I create a variable varone with value 1.2
    And I create a context variable vartwo with value wibble
    Then variable varone exists
    And context variable vartwo exists
    And variable varone has the value 1.2
    And variable vartwo has the value wibble
    And I show variable varone
    And I show context variable vartwo


   Scenario: Context Variables Are Cleared For Each Scenario
     Check the context has no values in it

   Scenario: Context Variable Expansion into Steps
     Given I create a variable myVar with value wibble
     And I create a variable variable containing spaces with value value
     Then I call a step passing the value ${myVar} and the variable gets expanded
     And I call a step passing the ${variable containing spaces} ${myVar} and the variables get expanded

   Scenario: Test mathematical operations
     Given I create a variable myVar with value 1.2
     And I add the value 1.2 to myVar
     Then variable myVar has the value 2.4
