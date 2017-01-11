Uses: Feature Start With Tags

Feature: Feature Start With Background
  
  Test that when we run only a single scenario using tags the start and end sections still run

  Feature-Start:
    First I run feature start
  
  Background:
    Then I run a background section for each scenario

  @FeatureStartWithTagsScenario
  Scenario: Scenario One
    Then I run a scenario  
    
  Scenario: Scenario Two
    Then the scenarios are skipped

  @FeatureStartWithTagsScenario  
  Scenario: Scenario Three
    Then I run a scenario

  Feature-End:
    Finally I run feature end


