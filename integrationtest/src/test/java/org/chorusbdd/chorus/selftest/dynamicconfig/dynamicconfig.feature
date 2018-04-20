Uses: Processes
Uses: Remoting

Feature: Dynamic Configuration

  Test that we can dynamically configure the processes and remoting handlers

  Scenario: Configure and start a process dynamically
    Given I add a process config on port 12345 called dynamicOne
    And I start a dynamicOne process named Ebeneezer
    When I add a process config on port 23456 called dynamicTwo
    And I start a dynamicTwo process named Scrooge
    Then the process named Ebeneezer is running
    And the process named Scrooge is running

  Scenario: Configure remoting dynamically
    Given I add a process config on port 23456 called hagrid
    And I add a remoting config on port 23456 called harryPotter
    And I start a hagrid process
    And I connect to the hagrid process
    Then I can call an exported method









