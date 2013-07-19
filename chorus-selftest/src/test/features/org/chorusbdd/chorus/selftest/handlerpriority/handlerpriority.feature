Uses: Handler Priority Two
Uses: Handler Priority Three

Feature: Handler Priority

  Test that the order of priority for finding steps is the default handler, followed by handlers defined in Uses:
  in order of how the Uses: handlers appear in the feature file

  Scenario: Simple Scenario
    Given Chorus is working properly
    Then steps in all three handlers should be handled by Handler Priority
    And steps in just handler two and three should be handled by Handler Priority Two
    And steps in just handler two should be handled by Handler Priority Two

