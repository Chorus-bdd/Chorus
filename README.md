###Chorus is a BDD testing framework targeted at distributed architectures###

Chorus is a Behaviour Driven Development (BDD) testing framework written in Java.

It is targeted at distributed systems, which are more complex to test than a single development stack. 

If you want to test a system with many networked components which need to collaborate, or has multiple User Interfaces which can interact, then Chorus may be for you. Testing such a system end to end can be tough with conventional frameworks. You'll end up doing a lot of the plumbing yourself.

Chorus allows a team to write plain English tests which can execute across components running remotely. 
For example in the test below, the first step may run on a web application, and the second may run on a fat client:

Scenario: A trade shows in the blotter once booked
  When a Sales User books a trade
  Then the trade shows up in the traders blotter

Chorus supports an extended version of the 'Gherkin' syntax popularised by Cucumber and JBehave.

Chorus is a Java framework, and presently provides a solution to connect to and test Java (or JVM-based) components.
It is very easy to extend. 
Need a handler for managing virtualized Docker components? You can add one, assuming we don't get there first!

We hope to add connectivity solutions for other languages in the near future

**Getting Started:**

####See [Chorus Demo Project](https://github.com/Chorus-bdd/Chorus-demo) ####
####See [The Chorus Wiki](http://github.com/Chorus-bdd/Chorus/wiki) for documentation ####
####The [User Forum](http://forum.chorusbdd.org/) for help ####
####The [Issue Tracking](https://github.com/Chorus-bdd/Chorus/issues?state=open) page, for issue tracking!####

Chorus is available under the open source MIT license

Contacts:  
 * Nick - email nick (at) objectdefinitions.com  
 * or Steve - via http://www.smartkey.co.uk/contact.html

