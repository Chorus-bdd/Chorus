---
layout: page
title: What is Chorus?
---

Chorus is a versatile BDD (Behaviour Driven Development) test interpreter

To use Chorus you write feature files in the popular 'Given, When, Then' BDD testing format [Gherkin](https://cukes.info/gherkin.html)

Once you have written your features you then invoke Chorus, which will find and run the test scenarios in your feauture files.
It will match each test step in your feature files to a step definition which can be executed.

A step definition consists of a search pattern, which is matched line by line against the test steps in the feature files, 
alongside some code to execute the step. Just like other BDD interpreters, you can provide step definitions locally.
To do this, you write Java 'Handler' classes labelled with Chorus' `@Handler` and `@Step` annotations, and make these available on your test classpath.

What makes Chorus different from other BDD testing tools is that the interpreter can also connect to other processes 
running in your testing environment, to discover and execute step definitions they publish. This step discovery is performed at runtime, as part of
executing a test feature. Chorus provides some built in Handler classes with test steps which allow you to connect to remote 
components and discover steps, and client APIs which can be used within your components to allow them to publish test step definitions to Chorus. APIs are provided
in Java (for Java and JVM components) and Javascript (chorus-js, for browser-based apps)

Chorus also provides some other built in hander classes to help with common tasks, such as process control (starting and 
stopping local processes), interacting with browsers via Selenium and connecting to SQL databases to execute statements.

In addition, it provides several [language extensions](/pages/GherkinExtensions/GherkinExtensions) to Gherkin

See [Getting Started](/pages/GettingStarted/GettingStarted) for more information

Chorus is open source!
Here's where to find the project on [Github](https://github.com/Chorus-bdd/) 

![Chorus Overview](/public/ChorusOverview.png)







