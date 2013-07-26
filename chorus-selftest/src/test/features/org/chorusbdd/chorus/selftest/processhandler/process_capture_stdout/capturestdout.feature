Uses: Processes

Feature: Capture Std Output

  Test that we can use the Processes handler to start a process with the std out 'captured' and then use processes
  step methods to match patterns

  Scenario: I expect some output
    Given I start a config1 process named outputter
    When I read the line 'let's match a line' from outputter process 
    Then I read the line 'and another .* line' from outputter process
    
  Scenario: I exepect some output from noisy process
    Given I start a noisy process
    When I read the line 'Start with this' from noisy process
    Then I read the line 'Then read this' from noisy process
    And I read the line 'Finally a .* pattern' from noisy process







