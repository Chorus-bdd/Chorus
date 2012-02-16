Uses: Calculator

Feature: Calculator with Background keyword

  Background:
    Given I have entered 50 into the calculator
    And I have entered 70 into the calculator

  Scenario: Add two numbers
    When I press add
    Then the result should be 120 on the screen

  Scenario: Subtract two numbers to get positive result
    When I press subtract
    Then the result should be -20 on the screen
