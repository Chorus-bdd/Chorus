Feature: Tagged Scenario Or

  Test that when I use an or in my tag expression
  a scenario will run provided one side evaluates to true

  Scenario: Scenario One
    Given Chorus is working properly
    Then this scenario will run

   @NotThisOne
   Scenario: Scenario Two
    Given Chorus is working properly
    Then this step will not run because the scenario is not tagged

   @NotThisOne
   @ThisOne
   Scenario: Scenario Three
    Given Chorus is working properly
    Then this scenario will run


