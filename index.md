---
layout: page
---

## What is Chorus?

Chorus is a BDD testing tool which makes it easier to write BDD tests for complex systems

Chorus can help orchestrate tests across components in a distributed architecture.

## Why is it needed?

It can be very difficult to write full-system integration tests using conventional BDD techniques.

This is particularly true in cases where there are several user interfaces involved, or for reactive systems in which components need to respond to external events.

At some point while testing a system like this, you're bound to need to do some of the following:

* Bootstrap or tear down an environment used for integration testing
* Connect to a remote component to trigger an event or action
* Send a mock message from one component to another
* Start or stop a process

Chorus can help with all of this

## How does Chorus work

Chorus can work as a test interpreter for 'standard' Cucumber-style BDD tests written in Gherkin

On top of this, it provides several unique language extensions

One of the most important language extensions is the ability to embed 'Directives' in your feature files
Directives can provide technical instructions which indicate how a test should run, but keep these separate from the business language.

Chorus provides some built in directives, but if these are not sufficient you can easily supply [handler classes](/pages/Handlers/HandlerClasses) to implement your own













