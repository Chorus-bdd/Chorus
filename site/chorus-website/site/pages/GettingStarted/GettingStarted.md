---
layout: page
title: Getting Started
section: Getting Started
sectionIndex: 10
---

To use Chorus, you must download and run the Chorus interpreter.   
This will parse the feature files containing your BDD tests, and execute them.

There are several ways to run the Chorus interpreter, depending on the needs of your project
  
* **Integrated with a Java project as a JUnit test suite:**   
  [Add Chorus as a maven/gradle dependency, or download it as a jar dependency](/pages/RunningChorus/RunningAsJUnitSuite)

* **As a Docker container in a Docker-enabled environment:**  
  [Use a Docker image downloadable from Docker hub](/pages/RunningChorus/RunningWithDocker) 
  
* **From the command line:**  
  [Download Chorus and install it as a standalone installable package](/pages/RunningChorus/RunningAsAStandaloneInstallable)

No matter how you run Chorus, the [same parameters](/pages/RunningChorus/InterpreterParameters) are supplied to the interpreter to invoke the tests  


### How to write Chorus tests:

1. Write tests in plain English (as .feature files [following the standard Gherkin syntax](https://github.com/cucumber/cucumber/wiki/Gherkin).
2. Run the Chorus interpreter, providing it with a path to find your feature files.

At this point your tests will run, but they will fail because you have not yet provided an implementation for your test steps  

There are several ways to provide implementations of test steps to Chorus:

1. **Supply java classes** on the interpreter's classpath which implement the test steps in your feature files. 
These classes are called ['Handler' classes](/pages/Handlers/HandlerClasses)  

2. **Make use of built in step definitions** from Chorus' [Built In Steps](/pages/BuiltInHandlers/BuiltInSteps).
These provide a library of generic test steps to solve various common problems

3. **Use Chorus' client libraries to [publish step definitions](/pages/DistributedTesting/DistributedSteps)** from components running in a test environment. 
Chorus can also start these components locally using its [Process Control](/pages/BuiltInHandlers/Processes/ProcessesHandlerQuickStart)