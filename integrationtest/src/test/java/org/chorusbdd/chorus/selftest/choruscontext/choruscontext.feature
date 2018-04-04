Uses: Chorus Context

Feature: Check Chorus Context Handler

  Test that we can use the ChorusContext to save and recall local variables during a scenario
  Test that the context is cleared down between scenario

  Scenario: Test Context Variables
    Given Chorus is working properly
    And the context is empty
    And I create a context variable varone with value 1.2
    And I create a context variable vartwo with value wibble
    Then context variable varone exists
    And context variable vartwo exists
    And context variable varone has the value 1.2
    And context variable vartwo has the value wibble
    And I show context variable varone
    And I show context variable vartwo


   Scenario: Context Variables Are Cleared For Each Scenario
     Check the context is empty

   Scenario: Context Variable Expansion into Steps
     Given I create a context variable myVar with value wibble
     And I create a context variable variable containing spaces with value value
     Then I call a step passing the value ${myVar} and the variable gets expanded
     And I call a step passing the ${variable containing spaces} ${myVar} and the variables get expanded

  Scenario: Floating point Variable Expansion into Steps
    #Where it's possible to expand a float type as an integer and strip any redundant trailing .0 then Chorus should do this
    #This is because many steps tend to define reg exp captures for numeric values as (\d+) which doesn't
    #match a decimal point / floating point value
    #Where possible it seems a good idea to permit expanded values to match these simple captures even where
    #the underlying numeric type is floating point
    Given I create a context variable floatWithNoDecimalPlaces with value 1.0
    Given I create a context variable floatWithDecmialPlaces with value 1.2
    Then I call a step passing the value ${floatWithNoDecimalPlaces} and the variable gets expanded
    Then I call a step passing the value ${floatWithDecmialPlaces} and the variable gets expanded

   Scenario: Mathematical operations simple floating point
     Given I create a context variable myVar with value 1.2
     And the type of context variable myVar is Double
     And I add the value 1.2 to the context variable myVar
     Then context variable myVar has the value 2.4
     And the type of context variable myVar is Double

   Scenario: Mathematical operations promote integer to big decimal where required
     Given I create a context variable myVar with value 1
     And the type of context variable myVar is Long
     And I add the value 1.2 to the context variable myVar
     Then context variable myVar has the value 2.2
     And the type of context variable myVar is BigDecimal

   Scenario: The numeric class type is immaterial when checking a numeric value
     Given I create a context variable myVar with value 1
     And the type of context variable myVar is Long
     Then context variable myVar has the value 1.0
     And context variable myVar has the value 1

   Scenario: Values which don't fit into Long are paresed as BigInteger
     Given I create a context variable myVar with value 1000000000000000000000
     And the type of context variable myVar is BigInteger
     Then the context variable myVar has the value 1000000000000000000000.0

   Scenario: Adding values
     Given I create a context variable myVar with value 1
     And I add 1000 to the context variable myVar
     Then the context variable myVar has the value 1001

   Scenario: Subtracting values
     Given I create a context variable myVar with value 1000
     And I subtract 50 from the context variable myVar
     Then the context variable myVar has the value 950

   Scenario: Multiplying values
     Given I create a context variable myVar with value 1000
     And I multiply the context variable myVar by 5
     Then the context variable myVar has the value 5000

  Scenario: Multiplying values
     Given I create a context variable myVar with value 10
     And I divide the context variable myVar by 5
     Then the context variable myVar has the value 2

  #If we don't set maths context then any fractions which can't be represented perfectly in base 2 result in an error instead of rounding behaviour
  Scenario: Check maths context DECIMAL64 for divide
     Given I create a context variable myVar with value 10
     And I divide the context variable myVar by 6
     Then the context variable myVar has the value 1.666666666666667

  Scenario: Remainder
    Given I create a context variable myVar with value 10
    And I divide the context variable myVar by 3 and take the remainder
    Then the context variable myVar has the value 1

  Scenario: Increment variable
    Given I create a context variable myVar with value 10
    And I increment the context variable myVar
    Then the context variable myVar has the value 11

  Scenario: Decrement variable
    Given I create a context variable myVar with value 10
    And I decrement the context variable myVar
    Then the context variable myVar has the value 9

