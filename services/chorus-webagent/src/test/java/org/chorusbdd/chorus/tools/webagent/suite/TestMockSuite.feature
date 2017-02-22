Uses: Http Connector
Uses: Processes
Uses: Timers

Feature: Self Test Suite

  Scenario: Check I can view xml for a test suite
    Given I start a chorusInterpreter process named sessionOne
    When the web agent cache contains 1 test suites
    And I reset the cache suite ids using a zero-based index
    Then http://localhost:9080/Main+Cache/testSuite.xml?suiteId=Test+Suite-0 matches mocksuiteone.xml