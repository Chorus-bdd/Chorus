---
layout: page
title: ${handler.name} Handler Details
section: ${site.section}
sectionIndex: ${site.sectionIndex}
---

### Overview 

The Web Sockets handler provides steps which allow Chorus to open a web socket to listen for connections for client processes wishing to 
publish step definitions. The chorus-js library provides a way for an app running in a browser to connect to the web socket and publish steps. 

Typically test scenario will open a web socket and then start a browser (using Selenium Handler). The browser will then be navigated  
to a URL which loads a web app which we want to test. The test scenario will then wait for the web app to be loaded and the browser to make a connection.

The web app will use the chorus-js javascript library to open a web socket connection to the Chrous interpreter
and will publish its step definitions. Once the step definitions have been received, the interpreter will then proceed with the test
and may invoke steps within the browser-based app by invoking them over the web socket connection.

* [Handler Steps](#steps)  
* [Handler Properties](#properties)

## How to use the Web Sockets Handler

You can use this by adding 'Uses: Web Sockets' to the top of your feature file:

    Uses: Web Sockets
         

### Using the Web Sockets Handler in a test feature

#### A typical feature file may start like this:

    Uses: Web Sockets
    Uses: Selenium

      Feature-Start:
        Given I start a web socket server
        And I open the RemoteWebDriver browser
        And I navigate to http://mywebapp-url
        And I wait for the web socket client myWebAppPublisher
      
      Scenario: I can use steps exported from myWebAppPublisher
        When I enter a user name
        And I enter a password
        Then I can log in to the myWebAppPublisher app

You will need to ensure the chorus-websockets extension is on your classpath if using the JUnit Suite Runner, e.g. for a Maven project:

    <dependency>
        <groupId>org.chorusbdd</groupId>
        <artifactId>chorus-websockets</artifactId>
        <version>3.1.0</version>
        <scope>test</scope>
    </dependency>

### Configuring Web Sockets Handler

At present chorus only supports opening a single web socket server during each test feature
This has the name 'default' 
You can configure the port for the 'default' web socket in the feature properties:

    # chorus.properties:
    websockets.default.port=9099

If left unspecified, the port will default to 9080

See [Handler Configuration](/pages/Handlers/HandlerConfiguration) for more details on configuring handlers

### Closing the web socket

The web socket will be automatically closed at the end of the test feature (if FEATURE scope is used) or at the end of 
each scenario (if SCENARIO scope is used). Scope will default to SCENARIO unless the web socket is created in the 
special Feature-Start: scenario, in which case it will default to FEATURE


### For more details of the Web Sockets Handler in use with the chorus-js library

See [Chorus JS](/pages/DistributedTesting/ChorusJS) 


<#include "./handlerDetailsPageTemplate.ftl">