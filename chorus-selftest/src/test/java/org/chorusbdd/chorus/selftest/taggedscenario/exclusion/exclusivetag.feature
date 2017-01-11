Feature: Exclusive Tag

  Test that I can use an exclusive tag to select the scenarios which will not run

  Scenario: Scenario One
    Given Chorus is working properly
    Then this scenario will run

   @MyTag
  Scenario: Scenario Two
    Given Chorus is working properly
    Then this step will not run because the scenario is not tagged

  Scenario: Scenario Three
    Given Chorus is working properly
    Then this scenario will run


