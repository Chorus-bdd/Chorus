Feature: Inclusive Tag

  Test that I can use an inclusive tag to select the scenarios which run

  Scenario: Scenario One
    Given Chorus is working properly
    Then this step will not run because the scenario is not tagged

   @MyTag
   Scenario: Scenario Two
    Given Chorus is working properly
    Then this scenario will run

   Scenario: Scenario Three
    Given Chorus is working properly
    Then this step will not run because the scenario is not tagged


