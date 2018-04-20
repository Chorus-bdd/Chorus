Uses: Processes
Uses: Remoting
Uses: Chorus Context
Uses: Local Chorus Context Handler


Feature: JMX Remote Chorus Context

  Test that chorus context variable set locally are also exported and visible to
  remote components during step execution. Also if set remotely, variables are visible
  locally to the interpreter

  Scenario: View And Change A Context Variable Remotely
    Given I start a config1 process named Casablanca
    And I connect to the Casablanca process
    And I set the context variable theUsualSuspects to Nick in Casablanca
    Then I can access the context variable theUsualSuspects in Casablanca
    And_if I set the context variable theUsualSuspects to Steve in Casablanca
    Then I show the context variable theUsualSuspects

  Scenario: Set a Variable Locally and Read it Remotely
    Given I start a config1 process named Casablanca
    And I connect to the Casablanca process
    And I create a context variable myVar with the value localValue
    Then the value of variable myVar is localValue in Casablanca

  Scenario: Can serialize a map
    Given I start a config1 process named Casablanca
    And I connect to the Casablanca process
    And I create a context map myMap in Casablanca
    Then the size of map myMap is 3

  Scenario: Can share a context variable between two remote processes
    Given I start a config1 process named Casablanca
    And I start a config2 process named Casablanca2
    And I connect to the processes named Casablanca, Casablanca2
    When I set the context variable theUsualSuspects to Nick in Casablanca
    Then I can access the context variable theUsualSuspects in Casablanca2












