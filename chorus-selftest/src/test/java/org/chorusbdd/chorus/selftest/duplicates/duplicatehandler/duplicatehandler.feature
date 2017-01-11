Feature: Duplicate Handlers

  If more than one handler with the same name is defined, any features which use that handler name will fail

  Scenario: Simple Scenario
    Given Chorus is working properly
    Then I can run a feature with a single scenario

