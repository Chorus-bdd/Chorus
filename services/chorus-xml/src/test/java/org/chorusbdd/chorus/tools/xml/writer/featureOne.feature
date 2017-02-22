
Feature: Feature One

  Test XML generation for a very simple feature with a single scenario
  Here we are simply using the chorus interpreter to run the feature and then we can get
  hold of the ExecutionToken and Feature tokens to convert with our xml writer

  Scenario: Convert A Simple Scenario to xml
    Given I have a simple scenario
    And this contains some steps
    Then I can use this to generate tokens which I can access in my junit xml writers