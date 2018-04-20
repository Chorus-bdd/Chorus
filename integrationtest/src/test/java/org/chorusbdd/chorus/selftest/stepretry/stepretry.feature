Uses: Remoting
Uses: Processes

Feature: Step Retry

  Test that Chorus PolledAssertion and invoker annotations work as expected

  Feature-Start:
    First I start a remotePolled process
    And I connect to the remotePolled process

  Scenario: Step Retry Is Working In Local Handler
    Given I increment a value with a timer task
    And the value is 1 within default period
    When I increment a value with a timer task
    Then the value is 2 within 2 seconds
    When I increment a value with a timer task
    Then the value is not 3 within 0.2 seconds so this step should fail

  Scenario: Long running step method overruns passes within period
    #This scenario is to test for a defect where PolledAssertion/PassesWithin keeps polling the step method even after
    #the expected PassesWithin time has expired
    #We can't easily kill a running step method which has exceeded the time specified by PassesWithin annotation
    #The best we can do is make sure once it returns we do not poll it again if the passes within period has been exceeded
    When I call a 1 second to run step method with passes within 1 second annotation
    Then the next step runs 1 second later

  Scenario: I can immediately break out of a Step Retry by throwing FailImmediatelyException
    When call a passes within step method it can be terminated immediately by FailImmediatelyException

  Scenario: Remoting with Step Retry
    When I start a timer
    Then test condition eventually passes
    And another test condition fails with AssertionError

  Scenario: Exceptions fail tests with remote Step Retry
    Then another test condition fails with Exception

  Scenario: Runtime Exceptions fail tests with remote Step Retry
    Then another test condition fails with RuntimeException

  Scenario: I can immediately break out of a remote Step Retry by throwing FailImmediatelyException
    When call a passes within step method remotely it can be terminated immediately by FailImmediatelyException