---
layout: page
title: Remoting Handler Example
---

Chorus is a futuristic test framework, so let's go with a fun and futuristic example..

Let's assume I'm trying to test the systems on a spacecraft.
Various distributed components control aspects of the flight.  
Unlike Battlestar Galactica, all those components are networked! 

Let's see if we can use Chorus to test our spacecraft's battle readiness:

	Uses: Remoting

	Feature: Cylon Base Ship Attack
	
	    Scenario:  Shields up when Cylon Ship Detected
		    Given the spacecraft is undocked in navigation
		    When a Cylon ship is detected in tactical
		    Then shields go up in weaponsControl
		    
		
This feature involves three distributed components:

* the step ending **in navigation** will run on the **navigation** component 
* the step ending **in tactical** will run on the **tactical** component
* the step ending **in weaponsControl** will run on the **weaponsControl** component

To call steps on each of these components we are using the built in Chorus 'Remoting' handler, `Uses: Remoting`

### What do we need to do to make this work? ###

The first thing is to [write Handler classes](/pages/Handlers/HandlerClasses) for each component, [and export them](/pages/BuiltInHandlers/Remoting/RemotingHandlerQuickStart) 

a) Write a NavigationHandler for the navigation process, and export it:

    #File: NavigationHandler.java
	@Handler("Navigation")
	public class NavigationHandler {
		
		@Step("the spacecraft is undocked")
		public void undock() {
			... publish a message to simulate undocking
		}
	}
	
	#File: Navigation.java
	new ChorusHandlerJMXExporter(new NavigationHandler()).export();
	...

b) Do the same for tactical:

	@Handler("Tactical")
	public class TacticalHandler {

		@Step("a Cylon ship is detected")
		public void setBaseShipDetected() {
			... publish a message to simulate detecting a Cylon ship
		}
	}

c) For weapons control, we can use the `@PassesWithin` annotation to allow up to 1 second for the shields to go up after the messages are sent. 

	@Hander("WeaponsControl")
	public class WeaponsControlHandler {
	
		@Step("shields go up")
		@PassesWithin( length=1, timeUnit=TimeUnit.SECONDS )
		public void checkSheildsAreUp(){
            ChorusAssert.assertTrue(getShieldStatus() == UP);
		}
	}

### Getting this to run ###

First we need to export the handlers from each of our components, and start up all the components in our space ship simulation environment (UAT). Then, in order to run the tests, we need to provide a feature file, and a properties file next to the .feature file which will tell the chorus interpreter how to connect to these remote components. 

What we will end up with is the feature file (e.g. cylonBaseShipAttack.feature) next to a properties file (e.g. cylonBaseShipAttack.properties). 

See [Remoting Handler Quick Start](/pages/BuiltInHandlers/Remoting/RemotingHandlerQuickStart) for the details of how to set this up

The properties file contains three properties which tell the Chorus interpreter which server and port to connect to for each of our distributed components:

	remoting.navigation.connection=jmx:myserver1.myorg:18806
	remoting.tactical.connection=jmx:myserver2.myorg:18807
	remoting.weaponsControl.connection=jmx:myserver3.myorg:18808


### Some observations about the architecture ###

This is a real-time distributed system we are testing and we assume there are message feeds between each of the components. We assume that the Weapons Control component is subscribing to messages published by the navigation and tactical computers in our integration testing environment. It uses these message feeds to make strategic responses to certain situations (e.g. shields up). 

In our integration testing environment, we can test these responses are correct by sending a message to the navigation and tactical components, and asking them to temporarily change their published state. i.e. We are asking these components to publish mock data, to simulate a real condition occurring, so that we can test the outcome in the weapons control process.

Since we are using message feeds there are latencies involved. The messages from the navigation and tactical components may take some time to arrive at weaponsControl. That's why we have used the [@PassesWithin](/pages/BuiltInHandlers/Remoting/PassesWithinAnnotation) annotation in the weapons control step. This polls the method for a limited period, waiting for the assertion (shields are up) to be satisfied, instead of failing immediately if there is a small delay.

   