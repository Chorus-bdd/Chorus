
Uses: Processes
Uses: Timers

Feature: Http Connector
  Start chorus interpreter to publish some test suites into a chorus web agent, and then vaidate the
  xml and rss feeds supplied by the agent's http connector

  Scenario: Web Agent serves a main index
    Given I start a chorusInterpreter process named sessionOne
    Then http://localhost:9080/ matches mainIndex.xml
    And http://localhost:9080/index.xml matches mainIndex.xml

  Scenario: Web Agent serves a cache index
    Given I start a chorusInterpreter process named sessionOne
    Then http://localhost:9080/Main+Cache/ matches cacheIndex.xml
    Then http://localhost:9080/Main+Cache/index.xml matches cacheIndex.xml

  Scenario: Check list all test suites from main cache
    Given I start a chorusInterpreter process named sessionOne
    And I wait for 1 second to guarantee a unique timestamp for the next suite
    And I start a chorusInterpreter process named sessionTwo
    And I wait for 1 second to guarantee a unique timestamp for the next suite
    And I start a chorusInterpreter process named sessionThree
    Then http://localhost:9080/Main+Cache/allTestSuites.xml matches allTestSuites.xml
    Then http://localhost:9080/Main+Cache/allTestSuites.rss matches allTestSuitesRSS.xml

  Scenario: Check I can view xml for a test suite
    Given I start a chorusInterpreter process named sessionOne
    When the web agent cache contains 1 test suites
    And I reset the cache suite ids using a zero-based index
    #And I wait for 60 seconds
    Then http://localhost:9080/Main+Cache/testSuite.xml?suiteId=Test+Suite-0 matches suite-0.xml


