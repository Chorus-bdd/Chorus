---
layout: page
title: Chorus Context Handler
section: Chorus Context
sectionIndex: 20
---

Chorus provides a [built in handler](/pages/BuiltInHandlers/BuiltInSteps) called `ChorusContext`

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

## Loading variables into the context

You can define [handler properties](/pages/Handlers/HandlerConfiguration) which will be loaded into the ChorusContext at the start of each Scenario

These properties need to be prefixed with 'context.'

    context.myVariable=myValue
    context.myVariable2=myValue2

So that the Chorus Context handler will load these, you need to add `Uses: Chorus Context` at the top of your feature file

At present these variables are loaded into the context as String values







