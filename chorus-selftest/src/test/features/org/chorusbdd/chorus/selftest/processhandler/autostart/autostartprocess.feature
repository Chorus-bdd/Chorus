Uses: Processes
Uses: Remoting
Uses: Chorus Context

Feature: Autostart Process

  Check that feature scoped processes set to autostart are started once per feature as expected
  Check that scenario scope processes are started for each scenario

  Feature-Start: Check Processes are running
    When the process featurescope is running
    And the process featurescope-two is running

    And I set the process id to 1 in featurescope
    And I set the process id to 2 in featurescope-two

    #Feature-Start is a special scenario so scenario scoped processes will get started
    Then I check the process scenarioscope is running
    And I check the process scenarioscope-two is running

    #Stop them so we can check they are restarted for next scenario
    And I stop the process scenarioscope
    And I stop the process scenarioscope-two

  Scenario: Check feature scoped processes are still running
    When I get the process id from featurescope
    Then the variable lastResult has the value 1
    And I get the process id from featurescope-two
    Then the variable lastResult has the value 2

  Scenario: Check scenario scoped processes are running
    Check the process scenarioscope is running
    And the process scenarioscope-two is running












