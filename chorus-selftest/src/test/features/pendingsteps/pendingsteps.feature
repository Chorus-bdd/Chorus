Feature: Pending Steps

  Test that Chorus if a step method is annotated with pending the step has pending status and remaining steps are skipped
  The overall state is fail

  Scenario: Simple Scenario
    Given Chorus is working properly
    Then I can run a feature with a single scenario
    And a pending step gets a pending status
    While subsequent steps are skipped

