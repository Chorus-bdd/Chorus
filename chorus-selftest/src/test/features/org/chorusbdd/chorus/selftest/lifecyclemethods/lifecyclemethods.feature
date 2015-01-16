Uses: Lifecycle Methods Scenario Scoped
Uses: Lifecycle Methods Feature Scoped
Uses: Lifecycle Methods Multiple Methods
Uses: Simple Handler

Feature: Lifecycle Methods

  Test that lifecycle methods are called correctly on both feature and scenario scoped handlers
  The feature start and end count as handlers and trigger scenario start and end lifecycle callbacks
  
  Feature-Start: 
    Given I can run a feature start scenario
  
  Scenario: Simple Scenario
    Check Chorus is working properly
    
  Feature-End:
    Given I can run a feature end scenario

