Uses: Chorus Resource Scenario Scoped
Uses: Chorus Resource Feature Scoped
Uses: Timers

Feature: Chorus Resource

  Test that handler fields annotated with ChorusResource are set correctly

  Scenario-Outline: Test Chorus Resource 
    Check the feature.token resource is set correctly in a <scope> scoped handler
    Check the feature.dir resource is set correctly in a <scope> scoped handler
    Check the feature.file resource is set correctly in a <scope> scoped handler
    Check the scenario.token resource is set to Test Chorus Resource [<number>] <scope> in a <scope> scoped handler
    Check the timers handler is injected in a <scope> scoped handler
    Check the abstract superclass feature.token resource is set correctly in a <scope> scoped handler
    Check the abstract superclass feature.dir resource is set correctly in a <scope> scoped handler
    Check the abstract superclass feature.file resource is set correctly in a <scope> scoped handler
    Check the abstract superclass scenario.token resource is set to Test Chorus Resource [<number>] <scope> in a <scope> scoped handler
    Check the abstract superclass timers handler is injected in a <scope> scoped handler

    Examples:
    | scope    | number |
    | feature  | 1      |
    | scenario | 2      |
    
  Scenario-Outline: Test Chorus Resource scenario scoped resources
    #this should change value when the scenario changes
    Check the scenario.token resource is set to Test Chorus Resource scenario scoped resources [<number>] <scope> in a <scope> scoped handler
    Check the abstract superclass scenario.token resource is set to Test Chorus Resource scenario scoped resources [<number>] <scope> in a <scope> scoped handler

    Examples:
    | scope    | number |
    | feature  | 1      |
    | scenario | 2      |
    


