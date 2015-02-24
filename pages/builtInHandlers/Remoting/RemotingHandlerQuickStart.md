---
layout: page
title: Remoting Handler Quick Start
---

Let's pretend we have an integration test environment all ready to run some tests. We wish to write a feature which tests components are interacting in the way we expect.

One component is a user interface which traders user to Buy stocks. This component is called *traderUI*. We want Chorus to remotely click the 'Buy' button as a step in our test feature

The first thing we need to do is export a handler class with a step definition 'click the buy button' from the user interface component. 

**Create and Export Handler classes**

First create a handler class with a 'click the buy button' step

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


**Run the component, starting the JMX management service**

We need to make sure our user interface component has the jmx management service enabled when it is started up in UAT - this provides the network connectivity. Chorus' exporter will be discoverable as a JMX bean within the jmx container. 
 
This can be done by setting the following system properties when you start the java process:

	java -Dcom.sun.management.jmxremote
	-Dcom.sun.management.jmxremote.ssl=false
	-Dcom.sun.management.jmxremote.authenticate=false
	-Dcom.sun.management.jmxremote.port=${choose_a_port_number}

You also need to set the following system property on your remote component to turn on chorus handler export. This would generally be turned *on* in your testing environment, but *off* in production.

	-Dorg.chorusbdd.chorus.jmxexporter.enabled=true


**Calling Remote Steps from Chorus**

We are now going to run the Chorus interpreter, adding add a feature which calls the 'I click the buy button' step on the user interface component which is running remotely. 

At the top of our feature file we need to add `Uses: Remoting`, to tell Chorus that we will use the built in Remoting Handler. The feature may start like this:

	Uses: Remoting

	  Feature: Buy Button 

             #! Remoting connect traderUI
             Scenario: Buy stocks in traderUI
                When I click the buy button
                ...

**Adding a 'Connect Directive'**

Note that the scenario above is prefixed with the following directive:

    #! Remoting connect traderUI

This tells Chorus' Remoting handler to connect to the traderUI so that we can match and run the steps which it exports.

Initially this scenario will fail, since we haven't yet told the Remoting handler where the traderUI process is running.

**Telling chorus where to connect to the remote process**

We need to add a property which will tell Chorus' Remoting Handler where the `traderUI` component is running and how to connect to it.

Do this by adding a properties file in the same directory as the feature.
 
e.g. For a feature file named `clickbuy.feature`, we would add a `clickbuy.properties`

This needs to contain a connection property for each networked component.

If the traderUI was running on server myserver.mydomain on port 18806 then we'd need to add the following property:

	remoting.traderUI.connection=jmx:myserver.mydomain:18806


**More about Remoting properties**

For all the supported RemotingHandler properties see [Remoting Handler Properties](/pages/BuiltInHandlers/Remoting/RemotingHandlerProperties)

If you want to share remoting properties between all your features you can add their connectivity details to a chorus.properties at the top level on your classpath. Here you could list all your UAT components, for example, so you don't need to keep repeating yourself.


 

 


