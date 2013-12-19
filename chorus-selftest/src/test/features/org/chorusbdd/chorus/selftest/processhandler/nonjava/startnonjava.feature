Uses: Processes
Uses: Timers

Feature: Start Non Java Processes

  Test that we can use the Processes handler to start a non-java process by setting the executablePath property
  Check this works both for absolute paths and paths relative to the feature directory

  Scenario: Start a relative path process
    Given Chorus is working properly
    And I start a relativePathProcess process named Frodo
    And I write 'continue' to the Frodo process
    And I wait for the process named Frodo to terminate
    Then the process named Frodo has terminated

  Scenario: Write and Read to relative path process with argument
    Given Chorus is working properly
    And I start a capturedOutputProcess process named Cap
    And I read the line 'Started' from the Cap process
    And I write 'continue' to the Cap process
    And I wait for the process named Cap to terminate
    Then the process named Cap has terminated








