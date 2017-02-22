Uses: Processes
Uses: Chorus Context

Feature: Capture Std Output

  Test that we can use the Processes handler to start a process with the std out 'captured' and then use processes
  step methods to match patterns

  Scenario: I expect some output
    Given I start a config1 process named outputter
    When I read the line 'let's match a line' from outputter process 
    Then I read the line 'and another .* line' from outputter process

  Scenario: Matched pattern is stored in chorus context
    Given I start a config1 process named outputter
    When I read the line 'let's .* a line' from outputter process
    Then context variable ProcessesHandler.match has the value let's match a line
    
  Scenario: I expect some output from noisy process
    Given I start a noisy process
    When I read the line 'Start with this' from noisy process
    Then I read the line 'Then read this' from noisy process
    And I read the line 'Finally a .* pattern' from noisy process
    
  Scenario: Process exits before match
    Given I start a config1 process named lemming
    When I read the line 'it could never happen here' from the lemming process
    Then this line will be skipped and the previous line will fail since process died

  Scenario: Process exits with error before match
    Given I start a errorcode process named lemming
    When I read the line 'it could never happen here' from the lemming process
    Then this line will be skipped and the previous line will fail since process died

  Scenario: Process exits with timeout before match
    Given I start a noisytimeout process named anylastorders 
    When I read the line 'it could never happen here' from the anylastorders process
    Then this line will be skipped and the previous line will fail since anylastorders timed out

  Scenario: I can read from std and std error separately
    Given I start a outanderr process named poetical
    When I read the line 'The time has come, the Walrus said' from the poetical process
    Then I read the line 'To talk of many things' from the poetical process std error
    When I read the line 'Of shoes and ships and sealing-wax' from the poetical process
    Then I read the line 'Of cabbages and kings' from the poetical process std error
    
  Scenario: I can match a line without a line feed
    Given I start a nolinefeed process named nolinefeed
    When I read 'without' from the nolinefeed process
    Then I write the line 'wibble' to the nolinefeed process
    And I read the line 'wibble' from the nolinefeed process

  Scenario: I can read with timeouts
    Given I start a outputwithpauses process
    And I read 'To' from the outputwithpauses process within 1 second
    And I read the line 'Or not to be' from the outputwithpauses process within 1 second
    When I read 'That is' from the outputwithpauses process std error within 2 seconds
    Then I read the line 'the question' from the outputwithpauses process std error within 1 second
    #last of these should timeout
    
    



