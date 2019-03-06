---
layout: page
title: Why do we need Chorus?
section: Distributed Testing
sectionIndex: 40
---

Most developers are familiar with test driven development (TDD) principles and accept that design for testability affects the code we write. 

Done right, TDD results in code which structured in a way which makes it easy to unit test.   
For example, a code module might be written to depend on an abstraction or interface rather than a real data source, so that mock data or a mock service implementation can be injected.

However, unit testing is only one part of the picture.  
What about component and integration testing?   

#### Designing for integration testing

Techniques to 'design for testability' at integration test stage are less well understood.
These tests require starting up one or more complete components in a testing environment.
How might we design a component to be testable when running within an integration test environment?
Furthermore, how might we test collaborations between components or end to end flows?

Often integration testing is left to the end of the development process. In the worst case it is done by external 
team who have no input in the design or development process of the components they are charged with testing.  

If a component is not designed to be testable, the only way to to test it might be to 
observe visible aspects of its output. For example, by checking that a component has inserted data into a table in a database.

This has several drawbacks:

* **Expensive to write**  
  It is expensive to write tests which observe external effects. First a tester has to write code to trigger the test scenario (this in itself
  may be very hard, and even involved starting a UI and navigating a web interface using tools such as Selenium).
  Then a tester must to write code to observe output, which may mean connecting to a database and checking tables, 
  or consuming messages from message brokers. This is a significant amount of code to write and maintain, and it is hard 
  to ensure it is reliable and will run quickly enough to be practically useful.
  
* **Hard to maintain and Brittle**  
  Maintaining integration testing code such as this is time consuming and expensive, and so a 'test team' is set up to do it, since the 
  'development' team does not have enough time or resources. The test team probably maintains the test code separately from that of the 
  components whose interactions are being tested. What happens when the development team changes the database schema, or the message definitions which are emitted by the components?
  The tests then have to be separately (and expensively) updated. Too much implementation knowledge has leaked into the test code, which is brittle as a result. 
  
* **Can only test externally observable state**  
  Aspects of state we want to test may be memory-resident in a session and not externally observable, or prohibitively expensive to observe.
  
#### How Chorus solves the problem

Chorus presents a solution to the above problems by allowing the components to be developed with built in testing hooks which run within the components themselves.  

These take the form of test step definitions which can be embedded in the code and invoked over the network, from a BDD-style test.

This means the code to trigger actions during tests and check any resulting conditions is developed within the components themselves, 
and is developed and maintained alongside the components by the original developers.  

The role of the testing team (if indeed there still is one) is now to create test features by using the test steps which are 'published' by the various components which make up the integration testing environment.
The test team may also sit with the developers to assist with adding test steps into the component source code.

When a component's code changes, the test step implementations will naturally change along with it. Since the step definitions run within the 
components, they have access to the full in-memory runtime state, and nothing is inaccessible.

##### Isn't embedding test code within a deployable component a bad thing?

On the contrary, it's really the only sensible way to design for testability in integration tests. Additionally, the exporting of 
test steps is disabled in production environments, and with a little extra effort the test code can be removed in production
deployments if you so wish.












