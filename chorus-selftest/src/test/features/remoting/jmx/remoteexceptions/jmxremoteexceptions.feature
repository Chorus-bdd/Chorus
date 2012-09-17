Uses: Processes
Uses: Timers
Uses: Remoting

Feature: Jmx Single Handler Export

  Test that remote exceptions are handled and we see the message in the interpreter output

  Scenario: Handle Exceptions In Remote Steps
    Given I start a config1 process named SingleHandlerExport
    And I wait for 1 second for the process to start up
    And I call a method which throws an exception in SingleHandlerExport
    Then my subsequent steps are skipped

  Scenario: Handle Assertion Exceptions In Remote Steps
      Given I start a config1 process named SingleHandlerExport
      And I wait for 1 second for the process to start up
      And I call a method which throws an assertion exception in SingleHandlerExport
      Then my subsequent steps are skipped

  Scenario: Handler Null Pointer Exceptions in Remote Steps
    #or any Exception which does not set a message
    Given I start a config1 process named SingleHandlerExport
    And I wait for 1 second for the process to start up
    And I call a method which throws a NullPointerException in SingleHandlerExport
    Then my subsequent steps are skipped









