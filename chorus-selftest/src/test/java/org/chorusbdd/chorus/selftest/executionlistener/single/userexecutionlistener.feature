Feature: User Execution Listener

  Test that we can set a custom ExecutionListener when running a test suite

  Scenario: Simple Scenario
    Given Chorus is working properly
    Then a User Execution Listener gets its lifecycle methods invoked

