---
layout: page
title: What is Chorus?
---

Chorus is a versatile BDD (Behaviour Driven Development) test interpreter

To use Chorus you write test features in the popular ['Given, When, Then' format Gherkin](https://docs.cucumber.io/gherkin/), popularised by JBehave and Cucumber

Once you have written your features you then invoke Chorus, which will find and run the test scenarios in your feature files.
Chorus will match each test step in your feature files to a step definition (code which which can be executed to implement the step). 

What makes Chorus different is that the interpreter can connect to other processes in your testing environment to discover step definitions at runtime.  

Why would you want to do this?   
See [Why do we need Chorus?](/pages/DistributedTesting/WhyDoWeNeedChorus)

Chorus provides 

* Built in step definitions which allow you to connect to remote components and discover steps at the start of a test feature 
* Client libraries which can be used within your components to publish test step definitions to Chorus. (In Java and Javascript).

Chorus also provides some other built in Handler classes to help with common tasks, such as process control (starting and 
stopping local processes), interacting with browsers via Selenium and connecting to SQL databases to execute statements.

In addition, it provides several [language extensions](/pages/GherkinExtensions/GherkinExtensions) to Gherkin

See [Getting Started](/pages/GettingStarted/GettingStarted) for more information

Chorus is open source!
Here's where to find the project on [Github](https://github.com/Chorus-bdd/) 

![Chorus Overview](/public/ChorusOverview.png)







