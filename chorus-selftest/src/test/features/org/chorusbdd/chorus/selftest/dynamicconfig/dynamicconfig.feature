Uses: Processes
Uses: Remoting

Feature: Dynamic Configuration

  Test that we can dynamically configure the processes and remoting handlers

  Scenario: Configure and start a process dynamically
    Given I add a process config called dynamicOne
    And I add a process config called dynamicTwo
    When I start a dynamicOne process named Ebeneezer
    And I start a dynamicTwo process named Scrooge
    Then the process named Ebeneezer is running
    And the process named Scrooge is running

  Scenario: Configure remoting dynamically
    Given I add a process config called hagrid
    And I add a remoting config called harryPotter
    And I start a hagrid process
    Then I can call an exported method in harryPotter









