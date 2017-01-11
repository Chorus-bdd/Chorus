Uses: Processes

Feature: Named Processes

  I can give a process config a name or alias when I start it

  Scenario: Start a process with an alias
    Given I start a namedprocess process named Timothy
    And I start a namedprocess process named Roberto
    Then the process named Timothy is running
    And the process named Roberto is running

  #! Processes start namedprocess as Timothy, namedprocess as Roberto
  Scenario: Start a process without process check delay
    Check the process named Timothy is running
    And the process named Roberto is running









