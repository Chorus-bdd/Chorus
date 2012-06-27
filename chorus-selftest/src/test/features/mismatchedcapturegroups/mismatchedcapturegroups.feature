Feature: Mismatched Capture Groups

  Test the behaviour when a step is matched by regular expression, but the handler implementation method
  does not define arguments for the capture groups

  Scenario: Capture Groups One
    Given Chorus is working properly
    And I pass a parameter 10 matched by a regex with one capture group but no method params
    Then the subsequent steps fail

