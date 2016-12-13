Feature: Failed Step

  Test that if a step fails it is labelled as failed, subsequent steps are skipped and return code is non-zero
  The message from the exception is appended to the step output for the failed step

  Scenario: Simple Scenario
    Given Chorus is working properly
    Then I can run a feature with a single scenario
    And if a step fails
    Then the subsequent step is skipped

