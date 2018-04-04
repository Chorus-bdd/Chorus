Feature: Chorus Context
  A Chorus Context should be passed between handlers when they are executing
  their steps allowing state to propogate between steps within a scenario.

  Scenario: Context propagates boolean state
    Given the context is empty
    When I create a context variable b with value true
    Then context variable b exists
    And context variable b has value true

  @ChorusSuiteTag
  Scenario: Context propagates int state
    Given the context is empty
    When I create a context variable i with value 10
    Then context variable i has value 10

  Scenario: Context propagates double state
    Given the context is empty
    When I create a context variable d with value 1.5
    Then context variable d has value 1.5

  Scenario: Context propagates String state
    Given the context is empty
    When I create a context variable str with value abc
    Then show context variable str
    Then context variable str has value abc
