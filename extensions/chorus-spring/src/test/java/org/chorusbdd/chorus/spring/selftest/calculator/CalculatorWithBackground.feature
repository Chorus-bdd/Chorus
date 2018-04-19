Uses: Calculator

Feature: Calculator with Background keyword

  Background:
    Given I have entered 50
    And I have entered 70

  Scenario: Add two numbers
    When I press add
    Then the result should be 120

  Scenario: Subtract two numbers to get positive result
    When I press subtract
    Then the result should be -20
