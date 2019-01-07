---
layout: page
title: What is Chorus?
---

Chorus is a versatile BDD (Behaviour Driven Development) test interpreter

Conventional BDD tools require you to provide step definitions locally. 
This is possible with Chorus too. 
 
However, Chorus also provides client libraries which you can use to publish step definitions from your microservices or components under test.
When the interpreter runs it can connect over the network to your components, to find these step definitons and then execute them.  

Chorus supports both Java (JVM-based) and Javascript components (enabling a direct connection to test Web apps in the browser)

Chorus is open source!
Here's where to find the project on [Github](https://github.com/Chorus-bdd/) 

![Chorus Overview](/public/ChorusOverview.png)


## How does Chorus work

Chorus works as a test interpreter for standard Cucumber-style BDD tests written in [Gherkin](https://cukes.info/gherkin.html).
In addition, it provides several [language extensions](/pages/LanguageExtensions/LanguageExtensions) to Gherkin

See [Getting Started](/pages/GettingStarted/GettingStarted) for more information






