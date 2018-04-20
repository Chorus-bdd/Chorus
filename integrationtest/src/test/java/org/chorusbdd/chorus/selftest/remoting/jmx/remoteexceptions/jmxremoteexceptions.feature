Uses: Processes
Uses: Remoting

Feature: Jmx Single Handler Export

  Test that remote exceptions are handled and we see the message in the interpreter output

  Scenario: Handle Exceptions In Remote Steps
    Given I start a config1 process named SingleHandlerExport
    And I connect to the SingleHandlerExport process
    And I call a method which throws an exception
    Then my subsequent steps are skipped

  Scenario: Handle Assertion Exceptions In Remote Steps
    Given I start a config1 process named SingleHandlerExport
    And I connect to the SingleHandlerExport process
    And I call a method which throws an assertion exception
    Then my subsequent steps are skipped

  Scenario: Handler Null Pointer Exceptions in Remote Steps
    #or any Exception which does not set a message
    Given I start a config1 process named SingleHandlerExport
    And I connect to the SingleHandlerExport process
    And I call a method which throws a NullPointerException
    Then my subsequent steps are skipped









