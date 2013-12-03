Feature: Multiple User Execution Listener

  Test that we can set more than one custom ExecutionListener when running a test suite

  Scenario: Simple Scenario
    Given Chorus is working properly
    Then all User Execution Listener get their lifecycle methods invoked

