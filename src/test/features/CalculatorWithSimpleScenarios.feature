Uses: Calculator

#this is a comment and should not be picked up by the parser
Feature: Calculator with simple Scenario syntax
    This feature is used to test very basic functionality within the Chorus interpreter.

  Scenario: Add two numbers
    Given I have entered 50 into the calculator #this is an eol comment and should not be picked up by the parser either
    And I have entered 70 into the calculator
    When I press add
    Then the result should be 120 on the screen

  Scenario: Subtract two numbers to get positive result
    Given I have entered 50 into the calculator
    And I have entered 70 into the calculator
    When I press subtract
    Then the result should be -20 on the screen

  Scenario: Perform add then subtract and get correct results for both
    Given I have entered 50 into the calculator
    And I have entered 70 into the calculator
    When I press add
    Then the result should be 120 on the screen
    Given I have entered 5 into the calculator
    And I have entered 40 into the calculator
    When I press subtract
    Then the result should be -35 on the screen
