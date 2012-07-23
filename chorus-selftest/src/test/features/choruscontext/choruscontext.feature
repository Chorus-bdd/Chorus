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
