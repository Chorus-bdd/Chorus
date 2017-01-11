Uses: Processes
Uses: Timers

Feature: Start Non Java Processes

  Test that we can use the Processes handler to start a non-java process by setting the executablePath property
  Check this works both for absolute paths and paths relative to the feature directory

  Scenario: Start a relative path process
    Given Chorus is working properly
    And I start a relativePath process named Relative
    And I write the line 'continue' to the Relative process
    And I wait for the process named Relative to terminate
    Then the process named Relative has terminated

  Scenario: Write and Read to relative path process with argument
    Given Chorus is working properly
    And I start a capturedOutput process named Cap
    And I read the line 'Started' from the Cap process
    And I write the line 'continue' to the Cap process
    And I wait for the process named Cap to terminate
    Then the process named Cap has terminated

  Scenario: Start an absolute path process
    Given Chorus is working properly
    And I start a absolutePath process named Absolute
    And I write the line 'continue' to the Absolute process
    And I wait for the process named Absolute to terminate
    Then the process named Absolute has terminated








