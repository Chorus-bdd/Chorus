Uses: Calculator

# configurations allow the feature to be reused with different backing config files
Configurations: configA, configB, configC

Feature: Calculator with simple Scenario syntax
    This feature is used to test very basic functionality within the Chorus interpreter.

  Scenario: Add two numbers
    Given I have entered 50 into the calculator #this is an eol comment and should not be picked up by the parser either
    And I have entered 70 into the calculator
    When I press add
    Then the result should be 120 on the screen

  Scenario-Outline: Check the calculator operators
    Given I have entered <a> into the calculator
    And I have entered <b> into the calculator
    When I press <operator>
    Then the result should be <result>

    Examples:
      | a   | b   | operator | result |
      | 50  | 100 | add      | 150    |
      | 50  | 100 | subtract | -50    |
      | 50  | 100 | multiply | 5000   |
      | 500 | 10  | divide   | 50     |
