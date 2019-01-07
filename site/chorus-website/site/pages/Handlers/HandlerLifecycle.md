---
layout: page
title: Handler Lifecycle
section: Handlers
sectionIndex: 30
---

The default scope for a Handler class is SCENARIO.

Each time a new scenario starts all the required Handler classes which are SCENARIO scoped are created afresh.
This helps to ensure that there are no side effects left over from previous scenarios.

However there are times when it is helpful to share a Handler instance between all scenarios in a feature.
To cater for this, Chorus supports the concept of FEATURE Scope

Declaring a Handler as `Feature` scoped will ensure that only one Handler instance is created during a feature.
This instance is then used by all the scenarios that run.

### How to set the scope for a Handler class

To declare a Handler as Scenario scoped, you don't need to do anything, since this is the default scope.
To declare a Handler class Feature scoped, add the scope to the `@Handler` annotation as follows:

    @Handler( value="My Handler", scope=Scope.FEATURE ) (
    public class MyHandler {
    
    }
    

### When is this useful?

In some situations, where there is expensive initialization or tear down to do as part of a feature,
it makes sense to do this work once rather than before every scenarios.

An example might be starting up a process or establishing a connection and logging in to a server.

The Handler scoping mechanism is especially useful in conjunction with Chorus' [Feature-Start and Feature-End scenarios](/pages/GherkinExtensions/FeatureStartAndEnd)


### Lifecycle Methods on Handler classes

Chorus provides two annotations which can be used on methods of your Handler classes in order to perform setup and tear down work 
at either Scenario or Feature scope.

On a Scenario scoped handler only Scope.SCENARIO is valid for lifecycle methods
Methods annotated @Initialize or @Destroy will be called one at the start and end of the scenarios.

For Feature scoped handlers, lifecycle calls are possible both on Feature start and end, and on the start and end of each Scenario:

    @Handler( value="My Handler", scope=Scope.FEATURE ) (
    public class MyHandler {
    
        @Initialize(scope=Scope.FEATURE)
        public void initForFeature() {
          //this method will get run when the feature starts
        }
    
        @Initialize(scope=Scope.SCENARIO)
        public void initForScenario() {
          //this method will get run when each scenario starts
        }
        
        @Destroy(scope=Scope.SCENARIO)
        public void destroyForScenario() {
          //this method will get run when each scenario has finished
        }
        
        @Destroy(scope=Scope.FEATURE)
        public void destroyForFeature() {
          //this method will get run when the feature has finished
        }
        
        //Scenario scope is the default where scope is not specified:
        @Destroy()
        public void destroyForScenario() {
          //this method will get run when the scenario has finished
        }
        
    }
    
    












 