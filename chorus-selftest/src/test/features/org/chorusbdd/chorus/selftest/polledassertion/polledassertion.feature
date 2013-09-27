Uses: Remoting
Uses: Processes

Feature: Polled Assertion

  Test that Chorus PolledAssertion and invoker annotations work as expected
  
  Scenario: Passes Within Invoker Is Working
    Given I increment a value with a timer task
    And the value is 1 within default period
    When I increment a value with a timer task
    Then the value is 2 within 2 seconds
    When I increment a value with a timer task
    Then the value is not 3 within 0.2 seconds so this step should fail

  Scenario: Passes For Invoker Is Working
    Given I increment a value
    And the value is 1 for half a second
    And the check method was polled 10 times
    When I increment a value
    Then the value is 2 for half a second
    Then the value is 3 for half a second

   Scenario: Conflicting Annotations
     Given I use multiple annotations
     
   Scenario: Remoting with Polled Assertions
     Given I start a remotePolled process 
     When I start a timer in remotePolled
     Then test condition eventually passes in remotePolled
     And another test condition fails with AssertionError in remotePolled

    Scenario: Remoting with Polled Assertions with Exception
      Given I start a remotePolled process
      Then another test condition fails with Exception in remotePolled

    Scenario: Remoting with Polled Assertions with RuntimeException
      Given I start a remotePolled process
      Then another test condition fails with RuntimeException in remotePolled
