Uses: Processes

Feature: Capture Std Output

  Test that we can use the Processes handler to start a process with the std out 'captured' and then use processes
  step methods to match patterns

  Scenario: I write and read a line
    Given I start a echo process named myecho
    When I write the line 'Donald Bradman was the greatest of all time' to the myecho process   
    And I read the line 'Donald Bradman was the greatest of all time' from the myecho process
    Then I write the line 'you speak the truth, wise one' to the myecho process
    And I read the line 'you .* wise one' from the myecho process
    







