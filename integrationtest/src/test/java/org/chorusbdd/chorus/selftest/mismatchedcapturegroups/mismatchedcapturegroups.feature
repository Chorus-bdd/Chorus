Feature: Mismatched Capture Groups

  Test the behaviour when a step is matched by regular expression, but the handler implementation method
  does not define arguments for all the capture groups.
  In this case warnings are logged which would be visible in the interpreter output at default log level

  Scenario: Mismatched Capture Groups Params One
    Given Chorus is working properly
    And I pass a parameter 10 matched by a regex with one capture group but no method params
    Then the subsequent steps fail


  Scenario: Mismatched Capture Groups Params Two
    Given Chorus is working properly
    And I pass a parameter 10 matched by a regex with 2 capture groups but just one param
    Then the subsequent steps fail

