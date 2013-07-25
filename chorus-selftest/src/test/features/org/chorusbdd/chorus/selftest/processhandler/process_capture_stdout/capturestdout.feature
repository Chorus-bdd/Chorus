Uses: Processes

Feature: Capture Std Output

  Test that we can use the Processes handler to start a process with the std out 'captured' and then use processes
  step methods to match patterns

  Scenario: I expect some output
    Given I start a config1 process named outputter
    When I read the line 'let's match a line' from process outputter
    And I read the line 'and another .* lsine' from process outputter
    







