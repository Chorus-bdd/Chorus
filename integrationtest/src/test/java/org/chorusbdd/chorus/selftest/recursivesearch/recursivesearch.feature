Feature: Recursive Search

  Test that Chorus will find feature files and handler classes recursively when the featurePaths and handlerPackage
  properties are set

  Scenario: Top Level Scenario
    Given Chorus is working properly
    Then I can run a feature with a single scenario

