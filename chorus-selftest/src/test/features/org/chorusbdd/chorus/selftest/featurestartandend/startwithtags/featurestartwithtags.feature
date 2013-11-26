Feature: Feature Start With Tags

  Test that when we run only a single scenario using tags the start and end sections still run

  Feature-Start:
    First I run feature start
  
  Scenario: Scenario One
    Then the scenarios are skipped

  @FeatureStartWithTagsScenario  
  Scenario: Scenario Two
    Then I run a scenario

  Feature-End:
    But I run feature end


