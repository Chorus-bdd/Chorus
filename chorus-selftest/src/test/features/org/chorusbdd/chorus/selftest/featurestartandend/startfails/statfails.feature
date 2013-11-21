Feature: Feature Start Fails

  Test that if the start section fails the scenarios are skipped but the feature end still runs

  Feature-Start:
    First I run feature start
    With a step that fails
  
  Scenario: Scenario One
    Then the scenarios are skipped

  Scenario: Scenario Two
    Then the scenarios are skipped

  Feature-End:
    But I run feature end


