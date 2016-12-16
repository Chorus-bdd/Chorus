Uses: Processes
Uses: StepServer

Feature: Simple Step Server Client

  I can connect and publish steps over a web socket with a simple step server client

  #! StepServer start server
  Feature-Start:

  #! Processes start simpleStepPublisher
  #! StepServer wait for client SimpleStepPublisher
  Scenario: I can call steps with and without a result
    Check I can call a step with a result
    And I can call a step without a result

  #! Processes start simpleStepPublisher
  #! StepServer wait for client SimpleStepPublisher
  Scenario: I can call steps which fail
    Check I can call a step with a result
    And I can call a step which fails









