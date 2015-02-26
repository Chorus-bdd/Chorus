---
layout: page
title: Built In Handlers
---

The Chorus interpreter supplies some built in [handler classes](/pages/Handlers/HandlerClasses) of its own to provide some extra capabilities:

* [Connecting to run steps on distributed components](/pages/BuiltInHandlers/Remoting/DistributedTesting) - The `Remoting` Handler
* [Starting and stopping processes](/pages/BuiltInHandlers/Processes/StartingProcesses) - The `Processes` Handler
* [Manipulate a map of variables within each Scenario](/pages/BuiltInHandlers/ChorusContext/ChorusContextHandler) - The `ChorusContext` handler
* [Timing and sleeping](/pages/BuiltInHandlers/Timers/TimersHandler) - The `Timers` Handler


These handler classes match steps in your feature files, just like your own handler classes.  

You do not need to use them, unless you want to leverage the capabilities they provide.

You need to use the Chorus keyword `Uses:` to indicate you want to use the steps in a built in handler, just as you 
would with your own handler classes.

e.g.  

    Uses: Processes  
    Uses: Remoting 
    
    Feature: My feature using both processes and remoting
    
    Scenario: Scenario one
    
    