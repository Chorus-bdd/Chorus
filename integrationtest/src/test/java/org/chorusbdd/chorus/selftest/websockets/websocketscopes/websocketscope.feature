Uses: Processes
Uses: Web Sockets
Uses: Chorus Context

Feature: Simple Step Publisher

  Check scenario scoping for web socket server
  
  Scenario: I can start a web socket server which is scenario scoped
    Given I start the web socket server
    And I start a simpleStepPublisher process
    And I wait for the web socket client SimpleWebSocketStepPublisher
    Then I can call a step with a result

  Scenario: I can start a web socket server which is scenario scoped in a second scenario
    Given I start the web socket server
    And I start a simpleStepPublisher process
    And I wait for the web socket client SimpleWebSocketStepPublisher
    Then I can call a step with a result
  