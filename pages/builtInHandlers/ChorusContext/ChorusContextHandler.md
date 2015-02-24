---
layout: page
title: Chorus Context Handler
---

Chorus provides a [built in handler](/pages/BuiltInHandlers/BuiltInHandlers) called `ChorusContext`

This handler is used to view or manipulate variables stored within the [Chorus Context](/pages/BuiltInHandlers/ChorusContext/ChorusContext)

To use the handler, add `Uses: ChorusContext` to the top of your feature file.

You can then make use of the steps provided:

    Uses: ChorusContext

      Feature: Show Chorus Context Steps
      
        Scenario: Create a Context variable
          
          Given the context has no values in it
          When I create a context variable myVar with value 2
          Then the context variable myVar exists
          And the context variable myVar has the value 2
          And I show context variable myVar 
          #the last step above will show the value of the variable in Chorus' output
           
n.b. Most often Context variables are set or updated by the step implementation methods in handler classes.
The ChorusContext handler gives you the ability to view and manipulate these directly in your scenario.

## Chorus Context with Feature-Start: ##

The ChorusContext handler has some special support for [Feature-Start: scenarios](/pages/LanguageExtensions/FeatureStartAndEnd)

It is often useful to pre-populate the context with variables during Feature-Start: and have these made available
to all subsequent scenarios.

Provided you are using ChorusContext handler for your feature (`Uses: Chorus Context`), the handler will take a snapshot of variable state
at the end of the Feature-Start: section, and restore this snapshot into the context at the start of each Scenario.

*n.b. this snapshot contains a shallow copy of the ChorusContext, so any mutable values it in will not have their values reset if changed during a scenario.
If this is a concern, make sure you only store immutable values in the context*



