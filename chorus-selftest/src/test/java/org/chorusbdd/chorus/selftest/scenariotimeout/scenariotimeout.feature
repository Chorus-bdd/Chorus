Feature: Scenario Timeout

  Test that a scenario will timeout once the scenario timeout period is elapsed

  First an attempt should be made to interrupt the interpreter thread
  This may or may not succeed in waking the blocked thread
  If it does succeed then the steps which was interrupted should be marked with TIMEOUT state and subsequent threads are skipped

  If interrupting fails to make the interpreter proceed, then eventually the interpreter thread should be killed and the tests fail
  While running this test we set the timeout value to 1 second

  We are not counting TIMEOUT separately from FAILED in the summary stats since timeout should be rare and we wish to be succinct
  - here a TIMEOUT step counts as a FAILED step

  Scenario: This scenario thread is interrupted due to scenario timeout
    Given Chorus is working properly
    And I try to sleep for 10 seconds
    Then the previous step ends in state TIMEOUT
    And the final steps end in state SKIPPED


  Scenario: The scenario should continue to run
    Given Chorus is working properly


  Scenario: This scenario should trigger a ThreadDeath
    Given I enter a perpetually blocked step

