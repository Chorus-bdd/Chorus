---
layout: page
title: Distributed Testing
---

Chorus has some unique capabilities for distributed testing:

To help test distributed systems, the Remoting handler allows you to export test steps from remote components, and have the interpreter call those components over the network to run test steps.

Usually when you run a test with Chorus, the interpreter will look for your `@Handler` classes in its local classpath. In order to have steps executed remotely, it is necessary for the remote components to export `@Handler` classes, and for the Chorus interpreter to connect up at runtime over the network and find them.

Chorus provides a mechanism to allow you to export handlers easily - the class `ChorusHandlerJmxExporter`. This class can wrap your Handler classes and export them as a JMX bean. The Chorus interpreter can then connect up using JMX/IIOP remoting and discover all the @Step methods your component exports. You can then write a feature which contains both locally and remotely executed steps. 

See  
[Remoting Handler Quick Start](/pages/BuiltInHandlers/Remoting/RemotingHandlerQuickStart)  
[Remoting Handler Example](/pages/BuiltInHandlers/Remoting/RemotingHandlerExample)

**ChorusContext**

Another feature which makes Chorus remoting powerful is the `ChorusContext`. 

[Chorus Context](/pages/BuiltInHandlers/ChorusContext/ChorusContext) is a Map containing variables which is propagated to your handler classes (both local and remote) when steps are executed. This means that your `@Step` methods can set variables in the context which are then available in subsequent steps, no matter whether those steps are executed locally or remotely.

