Feature: Chorus Resource

  Test that handler fields annotated with ChorusResource are set correctly

  Scenario: Test Chorus Resource Annotation
    Given Chorus is working properly
    Then the feature.token resource is set correctly
    And the feature.dir resource is set correctly
    And the feature.file resource is set correctly
    And the abstract superclass feature.token resource is set correctly
    And the abstract superclass feature.dir resource is set correctly
    And the abstract superclass feature.file resource is set correctly


