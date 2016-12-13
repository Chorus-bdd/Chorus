
Feature: Step Macro Path

  Test that where we set a step macro path which differs from feature paths this is used to find .stepmacro files

  Scenario: Calling a global macro
    Given Chorus is working properly
    Then I can call a global macro with three variables lemon 123 and aardvark
