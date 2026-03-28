Feature: DocString

  Test that Gherkin DocStrings are passed to @Step handler methods
  that declare a DocString parameter as their final argument.

  Scenario: Single line DocString is passed to the handler
    Given I receive the following body
      """
      Hello World
      """
    Then the body should equal Hello World

  Scenario: Multi-line DocString is passed to the handler
    Given I receive the following body
      """
      Line One
      Line Two
      Line Three
      """
    Then the body should have 3 lines

  Scenario: DocString can follow capture groups in the step expression
    Given I receive the following body labeled greeting
      """
      Hello World
      """
    Then the label should be greeting
    And the body should equal Hello World

