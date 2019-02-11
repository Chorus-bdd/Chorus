---
layout: page
title: What is Chorus?
---

Chorus is a versatile BDD (Behaviour Driven Development) test interpreter

To use Chorus you write feature files in the popular 'Given, When, Then' BDD testing format '[Gherkin](https://cukes.info/gherkin.html)'

When you have written your features you then invoke Chorus, which will match each test step in your feature files to a step definition which can be executed.

A step definition consists of a regular expression which is matched against test steps in the feature files, 
alongside some code to execute the step. Just like other BDD interpreters, you can provide step definitions locally.
To do this, you can write java classes which are on your local test classpath and are labelled with Chorus' `@Handler` and `@Step` annotations.

What makes Chorus different from other BDD testing tools is that the interpreter can also connect to other processes or services 
running in your testing environment, to discover test steps which they publish. This step discovery is performed at runtime, as part of
executing a test feature. To make this work, Chorus provides some built in handlers with test steps which allow you to connect to remote 
components - and client APIs which can be used within your components to allow them to publish test steps to Chorus. 

Chorus provides client APIs to allow step definitions to be published from remote components written in Java (or in JVM languages
which can call into Java APIs). It also provides a Javascript library (chorus-js) which can publish step definitons over a web socket from
browser-based apps

Chorus also has built in hander classes to perform process control, and interact with Browser via Selenium.
In addition, it provides several [language extensions](/pages/LanguageExtensions/LanguageExtensions) to Gherkin

Chorus is open source!
Here's where to find the project on [Github](https://github.com/Chorus-bdd/) 

![Chorus Overview](/public/ChorusOverview.png)

See [Getting Started](/pages/GettingStarted/GettingStarted) for more information






