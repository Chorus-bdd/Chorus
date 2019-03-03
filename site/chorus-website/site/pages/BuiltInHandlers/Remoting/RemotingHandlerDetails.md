---
layout: page
title: Remoting Handler Details
section: Remoting
sectionIndex: 30
---

### Overview

The `Remoting` Handler defines steps that allow the interpreter to connect to remote Java/JVM components and discover test step definitions they publish.
See [Distributed Tests](/pages/DistributedTesting/DistributedTests) for an overview of distributed test steps.

* [Handler Steps](#steps)  
* [Handler Properties](#properties)


### Using the Processes Handler

The processes Chorus connects to can be services deployed within a testing environment or may be running locally. 
The interpreter can [start up local processes](/pages/BuiltInHandlers/Processes/ProcessesHandlerQuickStart) and then connect to them.

Starting processes and connecting to them often occurs at the start of a feature, within Chorus' custom [Feature-Start:](/pages/GherkinExtensions/FeatureStartAndEnd) section.

#### Example

Let's pretend we have an integration test environment all ready to run some tests. We wish to write a feature which tests 
components are interacting in the way we expect.

One component is a user interface which traders user to Buy stocks. This component is called *traderUI*. We want Chorus 
to remotely click the 'Buy' button as a step in our test feature

The first thing we need to do is export a handler class with a step definition 'click the buy button' from the user 
interface component. 

##### 1. Create and export a Handler class to define steps which run within the UI component 

First create a handler class with a 'click the buy button' step.
This handler will be added to the source code for the UI component.

The BuyButtonHandler class may look like this:

    @Handler("Buy Button")
    public class BuyButtonHandler {
     
        @Step("I click the buy button")
        public void clickBuyButton() {
            ..here write some code to simulate a user clicking the buy button
        }
    }

Once created, add some code to export the handler class when the UI component starts up:

	BuyButtonHandler buyHandler = new BuyButtonHandler();
	new ChorusHandlerJmxExporter(buyHandler).export();


##### 2. Run the component, starting the JMX management service

We need to make sure our user interface component has the jmx management service enabled when it is started up in UAT - 
this provides the network connectivity. 
Chorus' exporter will be discoverable as a JMX bean within the jmx container. 
 
This can be done by setting the following system properties when you start the java process:

	java -Dcom.sun.management.jmxremote
	-Dcom.sun.management.jmxremote.ssl=false
	-Dcom.sun.management.jmxremote.authenticate=false
	-Dcom.sun.management.jmxremote.port=${choose_a_port_number}

You also need to set the following system property on your remote component to turn on chorus handler export. 
This would generally be turned *on* in your testing environment, but *off* in production.

	-Dorg.chorusbdd.chorus.jmxexporter.enabled=true

n.b. You could use the [Processes Handler](/pages/BuiltInHandlers/Processes/ProcessesHandlerQuickStart) to start up the 
process at the start of the feature, before you connect to it


##### 3. Calling Remote Steps from Chorus

We are now going to run the Chorus interpreter, adding add a feature which calls the 'I click the buy button' step on 
the user interface component which is running remotely. 

At the top of our feature file we need to add `Uses: Remoting`, to tell Chorus that we will use the built in 
Remoting Handler. The feature may start like this:

	Uses: Remoting

	  Feature: Buy Button 

             #! Remoting connect traderUI
             Scenario: Buy stocks in traderUI
                When I click the buy button
                ...

##### 4. Adding a 'Connect Directive' or a Connect step

Note that the scenario above is prefixed with the following directive:

    #! Remoting connect traderUI

This tells Chorus' Remoting handler to connect to the traderUI so that we can match and run the steps which it exports.
Alternatively if you prefer you could also use a built in step `Given I connect to the traderUI process` to accomplish this

Initially this scenario will fail, since we haven't yet told the Remoting handler where the traderUI process is running.


##### 5. Telling chorus where to connect to the remote process**

At present Chorus does not know how to connect to the traderUI process, so the connect directive will fail.

We need to add a property which will tell Chorus' Remoting Handler where the `traderUI` component is running and how to connect to it.

Do this by adding a properties file in the same directory as the feature.
 
e.g. For a feature file named `clickbuy.feature`, we would add a `clickbuy.properties`
This needs to contain a connection property for each networked component.

If the traderUI was running on server myserver.mydomain on port 18806 then we'd need to add the following property:

	remoting.traderUI.connection=jmx:myserver.mydomain:18806

Note the `remoting` prefix to the property name - this tells Chorus that this is Remoting handler property.


##### 6. More about Remoting properties

For all the supported RemotingHandler properties see [Remoting Handler Properties](/pages/BuiltInHandlers/Remoting/RemotingHandlerProperties)

If you want to share remoting properties between all your features you can add their connectivity details to a chorus.properties 
at the top level on your classpath. Here you could list all your UAT components, for example, so you don't need to keep repeating yourself.

  
<br/>
<a name="steps"/>
## Steps available in the Remoting Handler:
  
<br/>
<table>
    <tr>
        <th>Step</th><th>Example</th><th>Deprecated</th><th>Description</th><th>Retry Duration (wait for step to pass)</th>
    </tr>
    <tr>
        <td>Remoting connect ([a-zA-Z0-9-_, ]+)</td>
        <td>#! Remoting connect myServiceA) myServiceB</td>
        <td>No</td>
        <td>Connect to one or more remote processes (as a Directive) at the hostnames and ports specified in the handler properties. The remote processes must be exporting steps using Chorus JMX remoting utilities, ChorusHandlerJmxExporter. The number of connection attempts and wait time between each attempt are configured in the handler properties</td>
        <td></td>
    </tr>
    <tr>
        <td>.*connect to the process(?:es)? (?:named )?([a-zA-Z0-9-_, ]+)</td>
        <td>Given I connect to the processes named myProcessA) myProcessB</td>
        <td>No</td>
        <td>Connect to one or more remote processes at the hostnames and ports specified in the handler properties. The remote processes must be exporting steps using Chorus JMX remoting utilities, ChorusHandlerJmxExporter. The number of connection attempts and wait time between each attempt are configured in the handler properties</td>
        <td></td>
    </tr>
    <tr>
        <td>.*connect to the ([a-zA-Z0-9-_]+) process</td>
        <td>Given I connect to the myProcessA</td>
        <td>No</td>
        <td>Connect to a remote process at the hostname and port specified in the handler properties. The number of connection attempts and wait time between each attempt are configured in the handler properties</td>
        <td></td>
    </tr>

</table>
  

<br/>
<a name="properties"/>
## Configuration properties for the Remoting Handler:
  
<br/>
<table>
    <tr>
        <th>Property</th><th>Is Mandatory</th><th>Description</th><th>Default</th><th>Validation</th>
    </tr>
    <tr>
        <td>connection</td>
        <td>no</td>
        <td>A shorthand way of setting protocol host and port properties delimited by colon, e.g. jmx:myHost:myPort</td>
        <td></td>
        <td>jmx:\S+:\d+</td>
    </tr>
    <tr>
        <td>protocol</td>
        <td>yes</td>
        <td>Protocol to make connection (only JMX supported at present)</td>
        <td>jmx</td>
        <td>jmx</td>
    </tr>
    <tr>
        <td>host</td>
        <td>no</td>
        <td>host where remote component is running</td>
        <td></td>
        <td></td>
    </tr>
    <tr>
        <td>port</td>
        <td>no</td>
        <td>port on which remote component's jmx service is listening for connections</td>
        <td></td>
        <td>\d+</td>
    </tr>
    <tr>
        <td>connectionAttempts</td>
        <td>yes</td>
        <td>Number of times to attempt connection</td>
        <td>40</td>
        <td>\d+</td>
    </tr>
    <tr>
        <td>connectionAttemptMillis</td>
        <td>yes</td>
        <td>Wait time between each connection attempt</td>
        <td>250</td>
        <td>\d+</td>
    </tr>
    <tr>
        <td>scope</td>
        <td>yes</td>
        <td>Whether the remoting connection is closed at the end of the scenario or at the end of the feature. This will be set automatically to FEATURE for connections established during 'Feature-Start:' if not provided, otherwise Scenario</td>
        <td>SCENARIO</td>
        <td>One of: SCENARIO, FEATURE</td>
    </tr>

</table>
