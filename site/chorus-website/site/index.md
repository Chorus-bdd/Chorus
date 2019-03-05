---
layout: page
title: What is Chorus?
---

Chorus is a versatile BDD (Behaviour Driven Development) test interpreter

To use Chorus you write feature files in the popular 'Given, When, Then' BDD testing format [Gherkin](https://cukes.info/gherkin.html)

Once you have written your features you then invoke Chorus, which will find and run the test scenarios in your feature files.
Chorus will match each test step in your feature files to a step definition - code which which can be executed to implement the step 
- and then invoke the step.

What makes Chorus different from other BDD testing tools is that the interpreter can connect to other processes 
running in your testing environment, to discover and execute step definitions they publish. 

Chorus provides 

* Some built in Handler classes with step definitions which allow you to connect to remote components and discover steps, 
* Client libraries which can be used within your components to allow them to publish test step definitions to Chorus. 

APIs are provided in Java (for Java and JVM components) and Javascript (chorus-js, for browser-based apps)

Chorus also provides some other built in Handler classes to help with common tasks, such as process control (starting and 
stopping local processes), interacting with browsers via Selenium and connecting to SQL databases to execute statements.

In addition, it provides several [language extensions](/pages/GherkinExtensions/GherkinExtensions) to Gherkin

See [Getting Started](/pages/GettingStarted/GettingStarted) for more information

Chorus is open source!
Here's where to find the project on [Github](https://github.com/Chorus-bdd/) 

![Chorus Overview](/public/ChorusOverview.png)







