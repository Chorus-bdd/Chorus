Configurations: confA, confB

Feature: Configurations With Spring Fixtures
  The interpreter will use configurations when looking for Spring context files that are used to create test fixtures.
  If a file exists with the configuration name appended to it, then it will be used in preference to the one named in
  the annotations on the handler class.

  Note that the following scenarios will be run once for each configuration declared at the head of this file:

  Scenario: The correct Spring context is loaded
    Assert the injected variable contains the name of the run configuration
