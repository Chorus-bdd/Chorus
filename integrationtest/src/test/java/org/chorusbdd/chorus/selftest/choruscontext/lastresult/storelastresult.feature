Uses: Chorus Context
Uses: Processes
Uses: Remoting

Feature: Store Last Result

  Test that the last non-null result returned by a step method is stored in the context under lastResult

  Feature-Start:
    First I start a storelastremote process
    And I connect to the process storelastremote

  Scenario: Test Last Result With No Intervening Step
    When I call a step which returns a String myTestString
    Then the type of context variable lastResult is String
    And context variable lastResult has the value myTestString

  Scenario: Test Last Result With Intervening Step With void Return Type
    When I call a step which returns a String myTestString
    And I call a step with a void return type
    Then the type of context variable lastResult is String
    And context variable lastResult has the value myTestString

  Scenario: Test Last Result With Intervening Step Which returns null
    When I call a step which returns a String myTestString
    And I call a step which returns a null value
    Then context variable lastResult has the value null

   #Now once more, this time remotely

  Scenario: Test Last Result for Remote Process
    When I call a step which returns a String myTestString in storelastremote
    Then the type of context variable lastResult is String
    And context variable lastResult has the value myTestString

  Scenario: Test Last Result With Intervening Step With void Return Type in Remote Process
    When I call a step which returns a String myTestString in storelastremote
    And I call a step with a void return type in storelastremote
    Then the type of context variable lastResult is String
    And context variable lastResult has the value myTestString

  Scenario: Test Last Result With Intervening Step Which returns null in Remote Process
    When I call a step which returns a String myTestString in storelastremote
    And I call a step which returns a null value in storelastremote
    Then context variable lastResult has the value null


