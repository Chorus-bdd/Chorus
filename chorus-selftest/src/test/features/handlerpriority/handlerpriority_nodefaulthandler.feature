Uses: Handler Priority Three
Uses: Handler Priority Two

Feature: Handler Priority No Default Handler

  Test that where there is no default handler implemented for the feature, the order of priority for finding steps is
  determined by the order the Uses: handlers appear in the feature file
  Here we reverse the Uses: order for the _defaulthandler.feature to check it works the other way around
  In fact there are only two handlers used here, although we are reusing the step definition for all three handlers

  Scenario: Simple Scenario
    Given Chorus is working properly
    Then steps in all three handlers should be handled by Handler Priority Three
    And steps in just handler two and three should be handled by Handler Priority Three
    And steps in just handler two should be handled by Handler Priority Two

