Uses: Processes

Feature: Start A Process With Classpath

  Check that we can set the classpath when starting a process

  Scenario: Start a Single Java Process
    Given Chorus is working properly
    And I start a config1 process named Frodo
    And I wait for the process named Frodo to stop
    Then the process named Frodo is stopped








