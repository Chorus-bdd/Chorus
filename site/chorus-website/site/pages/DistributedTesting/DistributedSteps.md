---
layout: page
title: Distributed Steps
section: Distributed Testing
sectionIndex: 10
---

Usually when you write BDD tests, you need to provide an implementation for each test step locally.

Unlike other frameworks, Chorus provides client libraries which allow you to 'publish' test steps from the components you wish to test.
These components can be running locally or deployed on remote servers.

Chorus provides libraries which allow step publication from Java (JVM) and Javascript components


### Publishing steps from Java / JVM components

The Chorus java library can publish steps to Chorus from a JVM-based component.
The 'ChorusHandlerJmxExporter' utility publishes an MBean on the JMX platform MBean server, which the Chorus interpreter can connect to, in order to discover the test steps.

To publish steps to the Chorus interpreter from a Java/JVM component, you can use the Chorus utility class `ChorusHandlerJmxExporter` within the component.

The `Remoting` handler provides the built in test steps which allow Chorus to connect to a remote component using JMX.

This capability works well with Java/JVM services which are running in a test environment as daemon processes, but it is 
also possible for Chorus to [start a process](/pages/BuiltInHandlers/Processes/ProcessesHandlerQuickStart) and connect to it as part of a feature

See also:

[Remoting Handler Quick Start](/pages/BuiltInHandlers/Remoting/RemotingHandlerQuickStart)  
[Remoting Handler Example](/pages/BuiltInHandlers/Remoting/RemotingHandlerExample)



### Publishing steps from Javascript / Browser

You can use the `chorus-js` library to publish steps to Chorus from a Javascript component (e.g. a single page app in a Browser)
This library can publish step definitions to the Chorus interpreter over a web socket connection.

The `Web Sockets` handler provides built in steps which allow the Chorus interpreter to initiate the web socket and wait for step publication to complete.
The `Selenium` handler provides steps which allow Chorus to open a browser and interact with it, and inject scripts.

When testing browser-based apps, the Javascript which publishes the step defintions can either be built into the app directly, or can be injected via Selenium

See also:

[chorus-js](/pages/DistributedTesting/ChorusJS)
