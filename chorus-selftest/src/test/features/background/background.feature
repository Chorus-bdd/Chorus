Feature: Test Background

  Test that the background section gets included in each scenario

  Background:
    Given Chorus is working properly
    And I have some background steps

  Scenario: Scenario With Background One
    Then my background steps should appear in Scenario One

  Scenario: Scenario With Background Two
    Then my background steps should appear in Scenario Two


