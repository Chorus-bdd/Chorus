---
layout: page
title: Getting Started
---


The Chorus interpreter is a Java project.

To run it, you need just the chorus.jar - see the [Download Page for this](/pages/Resources/Download)

Chorus requires a jdk 1.7+ jvm

The best way to get started is to [download and run the tests in the demo project](https://github.com/Chorus-bdd/Chorus-demo)

See here for [examples of running the Chorus interpreter](/pages/RunningChorus/RunningChorus)

#### Here is a general overview of the process to write Chorus tests:

1. Write tests in plain English (as .feature files [following the standard Gherkin syntax](https://github.com/cucumber/cucumber/wiki/Gherkin).
2. [Run the Chorus interpreter](/pages/RunningChorus/RunningChorus), giving it a path to your feature files.

At this point your tests will run, but they will fail because you have not yet provided an implementation.

To make the features pass, supply java classes which implement the test steps in your feature files.
These classes are called ['Handler' classes](/pages/Handlers/HandlerClasses)

Chorus also provides some [Built In Handlers](/pages/BuiltInHandlers/BuiltInHandlers).
These provide support for local process control and connecting to run steps on components across the network

You will need to provide your own `Handler` classes to implement your own test steps.




