Uses: Processes
Uses: Web Sockets
Uses: Chorus Context

Feature: Two Web Socket Step Publishers

  Test that two web socket clients can connect and publish steps
  
  Feature-Start:
    Given I start the web socket server
    And I start a simpleStepPublisher process
    And I wait for the web socket client SimpleWebSocketStepPublisher
    And I start a secondStepPublisher process
    And I wait for the web socket client SecondWebSocketStepPublisher

  Scenario: I can call steps on both publisher processes
    Check I can call a step with a result
    And I can call a step on the second publisher








