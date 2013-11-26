Feature: Chorus Resource

  Test that handler fields annotated with ChorusResource are set correctly

  Scenario: Test Chorus Resource 
    Given Chorus is working properly
    Then the feature.token resource is set correctly
    And the feature.dir resource is set correctly
    And the feature.file resource is set correctly
    And the scenario.token resource is set to Test Chorus Resource
    And the abstract superclass feature.token resource is set correctly
    And the abstract superclass feature.dir resource is set correctly
    And the abstract superclass feature.file resource is set correctly
    And the abstract superclass scenario.token resource is set to Test Chorus Resource


  Scenario: Test Chorus Resource scenario.token
    #this should change value when the scenario changes
    And the scenario.token resource is set to Test Chorus Resource scenario.token
    And the abstract superclass scenario.token resource is set to Test Chorus Resource scenario.token
    


