---
layout: page
title: Built In Handlers
section: Built In Handlers
sectionIndex: 10
---

The Chorus interpreter includes some built in [Handler Classes](/pages/Handlers/HandlerClasses)

Each of these provides step definitions which can be used to accomplish various common tasks. 
These handler classes provide definitions for steps in your feature files, just like your own handler classes.

### Extension Handlers

In addition, some extra Handler Classes are provided as Chrous extensions. These are included automatically when you install the interpreter as a command line tool, or via the Docker image
  
When you use Chorus within a Java project as a JUnit Suite, you will need to declare an additional dependency on these extension libraries.
Using them may bring some additional transitive dependencies onto your test classpath.


### Built in and Extension Handlers

<table>
<tr>
	<th>Handler Name</th><th>Description</th><th>Type</th>
</tr>
<tr>
	<td>Remoting</td>
	<td><a href='/pages/BuiltInHandlers/Remoting/RemotingHandlerDetails'>Connecting to run steps on Java/JVM components</a></td>
	<td>Built In</td>
</tr>
<tr>
	<td>Processes</td>
	<td><a href='/pages/BuiltInHandlers/Processes/ProcessesHandlerDetails'>Starting and stopping processes</a></td>
    <td>Built In</td>
</tr>
<tr>
	<td>Chorus Context</td>
	<td><a href='/pages/BuiltInHandlers/ChorusContext/ChorusContextHandlerDetails'>Manipulate a map of variables within each Scenario</a></td>
    <td>Built In</td>
</tr>
<tr>
	<td>Timers</td>
	<td><a href='/pages/BuiltInHandlers/Timers/TimersHandlerDetails'>Timing and sleeping</a></td>
    <td>Built In</td>
</tr>
<tr>
	<td>Web Sockets</td>
	<td><a href='/pages/BuiltInHandlers/WebSockets/WebSocketsHandlerDetails'>Web Sockets support for Browser clients</a></td>
    <td>Extension</td>
</tr>
<tr>
	<td>Selenium</td>
	<td><a href='/pages/BuiltInHandlers/Selenium/SeleniumHandlerDetails'>Using Selenium to interact with Browsers</a></td>
    <td>Extension</td>
</tr>
<tr>
	<td>SQL</td>
	<td><a href='/pages/BuiltInHandlers/SQL/SQLHandlerDetails'>Running SQL Scripts on Databases</a></td>
    <td>Extension</td>
</tr>
</table>

### Using provided Handler Classes

You need to use the Chorus keyword `Uses:` to indicate you want to use the steps in a built in handler, just as you 
would with your own handler classes:

    Uses: Processes  
    Uses: Remoting 
    
    Feature: My feature using both processes and remoting
    
    Scenario: Scenario one
    
    
