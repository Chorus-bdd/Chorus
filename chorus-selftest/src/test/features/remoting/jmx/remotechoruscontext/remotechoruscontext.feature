Uses: Processes
Uses: Timers
Uses: Remoting
Uses: Chorus Context

Feature: Jmx Remote Chorus Context

  Test that chorus context variable set locally are also exported and visible to
  remote components during step execution. Also if set remotely, variables are visible
  locally to the interpreter

  Scenario: View And Change A Context Variable Remotely
    Given I start a config1 process named Casablanca
    And I set the context variable theUsualSuspects to Nick in Casablanca
    Then I can access the context variable theUsualSuspects in Casablanca
    And_if I set the context variable theUsualSuspects to Steve in Casablanca
    Then I show variable theUsualSuspects










