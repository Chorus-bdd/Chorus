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
     
  Scenario: Remoting with Polled Assertions
    Given I start a remotePolled process
    And I connect to the remotePolled process
    When I start a timer
    Then test condition eventually passes
    And another test condition fails with AssertionError

  Scenario: Remoting with Polled Assertions with Exception
    Given I start a remotePolled process
    And I connect to the remotePolled process
    Then another test condition fails with Exception

  Scenario: Remoting with Polled Assertions with RuntimeException
    Given I start a remotePolled process
    And I connect to the remotePolled process
    Then another test condition fails with RuntimeException

     
  Scenario: Long running step method overruns passes within period
    #This scenario is to test for a defect where PolledAssertion/PassesWithin keeps polling the step method even after
    #the expected PassesWithin time has expired
    #We can't easily kill a running step method which has exceeded the time specified by PassesWithin annotation
    #The best we can do is make sure once it returns we do not poll it again if the passes within period has been exceeded
    When I call a 1 second to run step method with passes within 1 second annotation
    Then the next step runs 1 second later

     
  Scenario: Long running step method overruns passes throughout period
    #This scenario is to test for a defect where PolledAssertion/PassesWithin keeps polling the step method even after
    #the expected PassesWithin time has expired
    When I call a 1 second to run step method with passes throughout 1 second annotation
    Then the next step runs 1 second later

  Scenario: I can immediately break out of a passes within by throwing FailImmediatelyException
    When call a passes within step method it can be terminated immediately by FailImmediatelyException

  Scenario: I can immediately break out of a remote passes within by throwing FailImmediatelyException
    Given I start a remotePolled process
    And I connect to the remotePolled process
    When call a passes within step method remotely it can be terminated immediately by FailImmediatelyException