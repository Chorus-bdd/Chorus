Uses: Processes

Feature: Jmx Execution Listener

  Test that the jmx execution listener features can be used to send execution steps to a remote jmx listener process

  Scenario: Test a Remote Jmx Execution Listener
    Given Chorus is working properly
    Then the remote jmx listener should be able to generate the same output as the local execution listener







