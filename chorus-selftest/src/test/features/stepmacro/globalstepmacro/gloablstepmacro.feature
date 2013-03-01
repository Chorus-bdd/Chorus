
Feature: Global Step Macro

  Test that macros in a .stepmacro file are available to features globally

  Scenario: Calling a global macro
    Given Chorus is working properly
    Then I can call a global macro with three variables lemon 123 and aardvark
    And I can call a global macro with three variables orange 456 and platypus
    And I can call a macro from a different directory so long as it is in the feature path
