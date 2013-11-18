Feature: Chorus Resource

  Test that handler fields annotated with ChorusResource are set correctly

  Scenario: Test Chorus Resource Annotation
    Given Chorus is working properly
    Then the feature.token resource is set correctly
    Then the feature.dir resource is set correctly
    Then the feature.file resource is set correctly


