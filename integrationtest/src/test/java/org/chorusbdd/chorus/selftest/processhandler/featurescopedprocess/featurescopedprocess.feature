Uses: Processes
Uses: Remoting

Feature: Feature Scoped Process

  Test that processes started in a scenario are still running in a subsequent scenario if set to feature scope
  Test we can start a process in Feature-Start: section

  Feature-Start: 
    First I start a featurescopedone process
  
  #! Remoting connect featurescopedone
  Scenario: I can call the process in scenario one
    I say hello in featurescopedone
    Then I start a featurescopedtwo process
    And I stop the process named featurescopedone
    
  #! Remoting connect featurescopedtwo
  Feature-End:
    I say hello in featurescopedtwo
    #two should be stopped at the end of this section
    
    
  








