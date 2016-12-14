Uses: Processes
Uses: StepServer

Feature: Simple Step Server Client

  I can connect and publish steps over a web socket with a simple step server client

  #! StepServer start server
  #! Processes start simplestepclient
  #! StepServer wait for client SimpleStepServerClient
  Scenario: Call a simple step
    Check I can call a step with a result










