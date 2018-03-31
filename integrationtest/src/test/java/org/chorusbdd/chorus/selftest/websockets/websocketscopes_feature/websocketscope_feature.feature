Uses: Processes
Uses: Web Sockets
Uses: Chorus Context

Feature: Simple Step Publisher

  Check feature scoping for a web socket server started in Feature-Start:
  
  Feature-Start:
    Given I start the web socket server
    And I start a simpleStepPublisher process
    And I wait for the web socket client SimpleWebSocketStepPublisher

  Scenario: I can call a web socket step in a scenario
    Then I can call a step with a result

  Scenario: I can call a web socket step in a second scenario 
    Then I can call a step with a result
  