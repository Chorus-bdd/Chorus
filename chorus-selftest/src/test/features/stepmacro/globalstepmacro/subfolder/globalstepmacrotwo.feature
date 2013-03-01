
Uses: Global Step Macro

Feature: Global Step Macro Two

  Test that macros in a .stepmacro file are available to features globally
  This feature in a subdirectory can uses step macros defined in a .stepmacro file in the parent directory

  Scenario: Calling a global macro
    Given Chorus is working properly
    Then I can call a global macro with three variables lemon 123 and aardvark
    And I can call a featurelocal step macro in addition to a global one
    And I can call a macro from a different directory so long as it is in the feature path

  Step-Macro: I can call a featurelocal step macro in addition to a global one
    And I can call a global macro with three variables orange 456 and platypus