Feature: Pending Steps

  Test that Chorus if a step method is annotated with pending the step has pending status and remaining steps are skipped
  The overall state is fail

  Scenario: Pending Annotations
    Given Chorus is working properly
    Then I can run a feature with a single scenario
    And a pending step gets a pending status
    While subsequent steps are skipped

  Scenario: Pending Exceptions Are Handled
    Given Chorus is working properly
    If I throw a PendingException then that step is shown pending with the exception message
    And subsequent steps are skipped

