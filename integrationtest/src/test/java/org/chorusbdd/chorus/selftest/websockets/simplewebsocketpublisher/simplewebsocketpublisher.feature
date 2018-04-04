Uses: Processes
Uses: Web Sockets
Uses: Chorus Context

Feature: Simple Step Publisher

  I can connect and publish steps over a web socket with a simple step server client
  
  Feature-Start:
    Given I start the web socket server
    And I start a simpleStepPublisher process
    And I wait for the web socket client SimpleWebSocketStepPublisher

  Scenario: I can call steps with and without a result
    Given the web socket client SimpleWebSocketStepPublisher is connected
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
    Given web socket client SimpleWebSocketStepPublisher is connected
    Then I show all the steps published by connected web socket clients

  Scenario: I can call a step with a step retry
    Given web socket client SimpleWebSocketStepPublisher is connected
    Then I can call a step with a step retry and the step is polled until it passes

  Scenario: Fail nicely if client is not connected
    Given web socket client DoesNotExist is connected

  Scenario: I can read a variable from the Chorus Context in the step publisher
    Given I create a context variable outbound with the value do
    Then in the step publisher outbound has the value do

  Scenario: I can set a variable in the Chorus Context in the step publisher
    When I set the inbound variable to re in the step publisher
    Then the context variable inbound has the value re

  Scenario: I can overwrite a variable in the Chorus Context in the step publisher
    Given I create a context variable outbound with the value re
    When I set the outbound variable to mi in the step publisher
    Then the context variable outbound has the value mi












