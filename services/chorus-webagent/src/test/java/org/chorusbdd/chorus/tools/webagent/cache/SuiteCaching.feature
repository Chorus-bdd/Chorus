
Uses: Processes
Uses: Timers

Feature: Suite Caching
  Start a chorus interpreter as a child process with a web agent configured as a remote listener
  Check the web agent receives a feature and inserts it into the web agent cache

  Scenario: Web Agent can receive a test suite from a chorus interpreter
    Given the web agent cache is empty
    And I start a chorusInterpreter process
    Then the web agent cache contains 1 test suite

  Scenario: Web Agent can receive two test suites from different chorus interpreter sessions
    Given the web agent cache is empty
    And I start a chorusInterpreter process named sessionOne
    And I wait for 500 milliseconds to guarantee a unique timestamp for the next suite
    And I start a chorusInterpreter process named sessionTwo
    Then the web agent cache received 2 test suites
    Then the web agent cache contains 2 test suites

  Scenario: Web Agent cache observes max history value
    Given the web agent cache is empty
    And I set the web agent cache to a max history of 2
    And I start a chorusInterpreter process named sessionOne
    And I wait for 1 second to guarantee a unique timestamp for the next suite
    And I start a chorusInterpreter process named sessionTwo
    And I wait for 1 second to guarantee a unique timestamp for the next suite
    And I start a chorusInterpreter process named sessionThree
    Then the web agent cache received 3 test suites
    And the web agent cache contains 2 test suites

  Scenario: Web Agent max history can be reduced
    Given the web agent cache is empty
    And I start a chorusInterpreter process named sessionOne
    And I wait for 1 second to guarantee a unique timestamp for the next suite
    And I start a chorusInterpreter process named sessionTwo
    Then the web agent cache contains 2 test suites
    And when I set the web agent cache to a max history of 1
    Then the web agent cache contains 1 test suites





