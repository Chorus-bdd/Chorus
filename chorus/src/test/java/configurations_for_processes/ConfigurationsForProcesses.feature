Uses: Processes

Configurations: confA, confB

Feature: Configurations for Processes
  The processes handler will firstly look for its normal configuration file before looking for one with the
  configuration name appended to it. If it finds the latter file, it will use the properties defined in
  it to override the properties loaded from the first one.

  Note that the following scenarios will be run once for each configuration declared at the head of this file:

  #
  # This scenario should produce 2 log files, the file for confA (with the overriden property) should show a
  # sleep time of 2 seconds, the one for configB (has no custom property file) should show 1.
  #
  Scenario: Start and stop a Java process defined in the properties file
    Given I start a bertrand process named bert
    And I wait for up to 10 seconds for bert to terminate and write to its log file
    Then bert is stopped
