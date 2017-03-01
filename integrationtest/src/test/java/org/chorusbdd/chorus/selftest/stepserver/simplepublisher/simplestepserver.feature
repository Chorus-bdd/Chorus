Uses: Processes
Uses: StepServer

Feature: Simple Step Server Client

  I can connect and publish steps over a web socket with a simple step server client

  #! StepServer start
  #! Processes start simpleStepPublisher
  #! StepServer wait for the client SimpleStepPublisher
  Feature-Start:

  Scenario: I can call steps with and without a result
    Given StepServer client SimpleStepPublisher is connected
    Then I can call a step with a result
    And I can call a step without a result

  Scenario: I can call steps which fail
    Check I can call a step with a result
    And I can call a step which fails

  Scenario: I can call steps which fail
    Given I can call a step with a result
    When I call a step which blocks
    Then the next step is skipped because the interpreter timed out

  Scenario: I can show all steps
    Given StepServer client SimpleStepPublisher is connected
    Then I can show all StepServer steps

  Scenario: I can call a step with a step retry
    Given StepServer client SimpleStepPublisher is connected
    Then I can call a step with a step retry and the step is polled until it passes

  Scenario: Fail nicely if client is not connected
    Given StepServer client DoesNotExist is connected











