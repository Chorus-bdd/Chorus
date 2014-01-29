@ADDITIVE_FEATURE
Feature: Scenario Outline

  Test that Scenario Outlines can have tags in the examples table using the special chorusTags parameter
  These tags add to any specified at feature or scenario level

  @ADDITIVE_SCENARIO
  Scenario-Outline: Simple Scenario
    Given Chorus is working properly
    Then I can run a step with value <ALongValueIdentifier>

    Examples:
      | ALongValueIdentifier  | chorusTags          |  
      | A Long Value          | @TESTTAG            |
      | B Long Value          | @IGNORE @TESTTAG    |
      | C Long Value          | @IGNORE             |
      | D Long Value          |                     |
