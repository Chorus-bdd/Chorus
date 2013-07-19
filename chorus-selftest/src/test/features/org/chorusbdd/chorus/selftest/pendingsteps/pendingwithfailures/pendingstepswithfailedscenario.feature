
Feature: Pending Steps With Failures

  Test that Chorus if a step method is annotated with pending the step has pending status and remaining steps are skipped

  Any scenarios which reaches a pending step gets the end state 'pending'

  A feature is pending so long as no scenarios fail, if at least one is 'pending'
  Here the feature should 'fail' because one or more scenarios failed


  Scenario: Pending Annotations
    Given Chorus is working properly
    Then I can run a feature with three scenario
    And a pending step gets a pending status
    While subsequent steps are skipped

  Scenario: Scenario Which Fails
    Given Chorus is working properly
    Then if I call a missing step not marked pending the scenario and feature will fail

