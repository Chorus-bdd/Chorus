Uses: Processes
Uses: Web Sockets
Uses: Chorus Context
Uses: Timers  

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
    
  Scenario: I can disconnect then restart a publisher
    Given I disconnect the web socket publisher
    And I wait for 500 milliseconds
    And I stop the process simpleStepPublisher
    And I wait for 1 seconds for the disconnection
    And I start a simpleStepPublisher process
    And I wait for the web socket client SimpleWebSocketStepPublisher
    And the web socket client SimpleWebSocketStepPublisher is connected
    Then I can call a step with a result
    And I can call a step on the second publisher

  Scenario: I can stop without disconnecting then restart a publisher
    Given I stop the process simpleStepPublisher
    And I wait for 1 seconds for the disconnection
    And I start a simpleStepPublisher process
    And I wait for the web socket client SimpleWebSocketStepPublisher
    And the web socket client SimpleWebSocketStepPublisher is connected
    Then I can call a step with a result
    And I can call a step on the second publisher
    




