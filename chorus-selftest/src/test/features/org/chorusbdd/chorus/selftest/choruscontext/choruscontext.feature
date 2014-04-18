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

  Scenario: Floating point Variable Expansion into Steps
    #Where it's possible to expand a float type as an integer and strip any redundant trailing .0 then Chorus should do this
    #This is because many steps tend to define reg exp captures for numeric values as (\d+) which doesn't
    #match a decimal point / floating point value
    #Where possible it seems a good idea to permit expanded values to match these simple captures even where
    #the underlying numeric type is floating point
    Given I create a variable floatWithNoDecimalPlaces with value 1.0
    Given I create a variable floatWithDecmialPlaces with value 1.2
    Then I call a step passing the value ${floatWithNoDecimalPlaces} and the variable gets expanded
    Then I call a step passing the value ${floatWithDecmialPlaces} and the variable gets expanded

   Scenario: Test mathematical operations simple floating point
     Given I create a variable myVar with value 1.2
     And the type of variable myVar is Double
     And I add the value 1.2 to myVar
     Then variable myVar has the value 2.4
     And the type of variable myVar is Double

   Scenario: Test mathematical operations promote integer to big decimal where required
     Given I create a variable myVar with value 1
     And the type of variable myVar is Long
     And I add the value 1.2 to myVar
     Then variable myVar has the value 2.2
     And the type of variable myVar is BigDecimal

   Scenario: Test the numeric type is immaterial when checking a numeric value
     Given I create a variable myVar with value 1
     And the type of variable myVar is Long
     Then variable myVar has the value 1.0
     And variable myVar has the value 1

   Scenario: Test BigInteger values
     Given I create a variable myVar with value 1000000000000000000000
     And the type of variable myVar is BigInteger
     Then the variable myVar has the value 1000000000000000000000.0


