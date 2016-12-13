
Feature: Pending Steps

  Test that Chorus if a step method is annotated with pending the step has pending status and remaining steps are skipped

  Any scenarios which reaches a pending step gets the end state 'pending'

  A feature is pending so long as no scenarios fail, if at least one is 'pending'

  The output status is success so long as no steps / scenarios actually fail since the purpose of marking a step pending
  is to indicate that it is expected that there is not yet an implementation (we want to able to add features in advance
  of implmenting the code without failing the build)


  Scenario: Pending Annotations
    Given Chorus is working properly
    Then I can run a feature with three scenario
    And a pending step gets a pending status
    While subsequent steps are skipped

  Scenario: Pending Exceptions Are Handled
    Given Chorus is working properly
    If I throw a PendingException then that step is shown pending with the exception message
    And subsequent steps are skipped

  Scenario: Scenario Which Passes
    Given Chorus is working properly
    Then I can run a feature with three scenario

