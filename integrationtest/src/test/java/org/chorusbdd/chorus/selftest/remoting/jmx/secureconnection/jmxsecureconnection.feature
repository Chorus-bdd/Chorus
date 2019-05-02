Uses: Processes
Uses: Remoting
Uses: Timers  

Feature: Jmx Secure Connection

  Test that we can use the Jmx connector to connect to a process which has a secured JMX Platform Service

  Scenario: Call An Exported Method
    Given I start a secureconnection process
    When I connect to the secureconnection process
    Then I can call a step method exported by the handler
    And I can stop the process named secureconnection  









