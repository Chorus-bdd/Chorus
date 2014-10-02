Uses: Chorus Resource Scenario Scoped
Uses: Chorus Resource Feature Scoped
Uses: Timers

Feature: Chorus Resource

  Test that handler fields annotated with ChorusResource are set correctly

  Scenario: Test Chorus Resource 
    Given Chorus is working properly
    Then the feature.token resource is set correctly in a feature scoped handler
    And the feature.token resource is set correctly in a scenario scoped handler

    And the feature.dir resource is set correctly in a feature scoped handler
    And the feature.dir resource is set correctly in a scenario scoped handler

    And the feature.file resource is set correctly in a feature scoped handler
    And the feature.file resource is set correctly in a scenario scoped handler

    And the scenario.token resource is set to Test Chorus Resource in a feature scoped handler
    And the scenario.token resource is set to Test Chorus Resource in a scenario scoped handler

    And the timers handler is injected in a feature scoped handler
    And the timers handler is injected in a scenario scoped handler

    And the abstract superclass feature.token resource is set correctly in a feature scoped handler
    And the abstract superclass feature.token resource is set correctly in a scenario scoped handler

    And the abstract superclass feature.dir resource is set correctly in a feature scoped handler
    And the abstract superclass feature.dir resource is set correctly in a scenario scoped handler

    And the abstract superclass feature.file resource is set correctly in a feature scoped handler
    And the abstract superclass feature.file resource is set correctly in a scenario scoped handler

    And the abstract superclass scenario.token resource is set to Test Chorus Resource in a feature scoped handler
    And the abstract superclass scenario.token resource is set to Test Chorus Resource in a scenario scoped handler

    And the abstract superclass timers handler is injected in a feature scoped handler
    And the abstract superclass timers handler is injected in a scenario scoped handler


  Scenario: Test Chorus Resource scenario.token
    #this should change value when the scenario changes
    And the scenario.token resource is set to Test Chorus Resource scenario.token in a feature scoped handler
    And the scenario.token resource is set to Test Chorus Resource scenario.token in a scenario scoped handler

    And the abstract superclass scenario.token resource is set to Test Chorus Resource scenario.token in a feature scoped handler
    And the abstract superclass scenario.token resource is set to Test Chorus Resource scenario.token in a scenario scoped handler


    


