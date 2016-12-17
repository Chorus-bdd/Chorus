Uses: Processes
Uses: StepServer

Feature: Simple Step Server Client

  I can connect and publish steps over a web socket with a simple step server client

  #! StepServer start server
  #! Processes start simpleStepPublisher
  #! StepServer wait for client SimpleStepPublisher
  Feature-Start:

  Scenario: I can call steps with and without a result
    Check I can call a step with a result
    And I can call a step without a result

  Scenario: I can call steps which fail
    Check I can call a step with a result
    And I can call a step which fails

  Scenario: I can call steps which fail
    Given I can call a step with a result
    When I call a step which blocks indefinitely
    Then the next step is skipped but the interpreter timed out










