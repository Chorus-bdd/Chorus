Uses: DuplicateHandlerOne
Uses: DuplicateHandlerTwo

Feature: Test For Duplicate Match Errors

  Test that Chorus steps can fail with a duplicate match error

  Scenario: Fail With A Duplicate Match From A Single Handler
    Given Chorus is working properly
    Then I can not run a step with two matching definitions

  Scenario: Fail With A Duplicate Match Across Two Handlers
    Given Chorus is working properly
    Then steps which conflict between two handlers also cause a match error



