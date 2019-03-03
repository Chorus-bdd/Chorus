---
layout: page
title: Web Sockets Handler Details
section: Web Sockets
sectionIndex: 30
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


  
<br/>
<a name="steps"/>
## Steps available in the Web Sockets Handler:
  
<br/>
<table>
    <tr>
        <th>Step</th><th>Example</th><th>Deprecated</th><th>Description</th><th>Retry Duration (wait for step to pass)</th>
    </tr>
    <tr>
        <td>.*start (?:a|the) web socket server</td>
        <td>Given I start a web socket server</td>
        <td>No</td>
        <td>Start a web socket server. The listening port will be 9080 if not specified in properties.</td>
        <td></td>
    </tr>
    <tr>
        <td>.*stop (?:a|the) web socket server</td>
        <td>Then I stop the web socket server</td>
        <td>No</td>
        <td>Stop a web socket server.</td>
        <td></td>
    </tr>
    <tr>
        <td>.*wait for (?:the )web socket clients? ([a-zA-Z0-9-_, ]+)</td>
        <td>And I wait for the web socket client singlePageApp</td>
        <td>No</td>
        <td>Wait for one or more named web socket clients to connect to the web socket. If more than one name is specified the list is comma delimited</td>
        <td></td>
    </tr>
    <tr>
        <td>.*(?:the )?web socket clients? ([a-zA-Z0-9-_, ]+) (?:is|are) connected</td>
        <td>Then the web socket clients singlePageApp1, singlePageApp2 are connected</td>
        <td>No</td>
        <td>Check that the named web socket clients are connected. If more than one name is specified the list is comma delimited</td>
        <td></td>
    </tr>
    <tr>
        <td>.*show all the steps published by connected web socket clients</td>
        <td>THen I show all the steps published by connected web socket clients</td>
        <td>No</td>
        <td>Show the steps published by all connected web socket clients in Chorus' interpreter's output</td>
        <td></td>
    </tr>
    <tr>
        <td>Web Sockets start</td>
        <td>#! Web Sockets start</td>
        <td>No</td>
        <td>Directive to start a web socket server. The listening port will be 9080 if not specified in properties.</td>
        <td></td>
    </tr>
    <tr>
        <td>Web Sockets stop</td>
        <td>#! Web Sockets stop</td>
        <td>No</td>
        <td>Directive to stop a web socket server.</td>
        <td></td>
    </tr>

</table>
  

<br/>
<a name="properties"/>
## Configuration properties for the Web Sockets Handler:
  
<br/>
<table>
    <tr>
        <th>Property</th><th>Is Mandatory</th><th>Description</th><th>Default</th><th>Validation</th>
    </tr>
    <tr>
        <td>port</td>
        <td>yes</td>
        <td>Which local port the web socket server should listen on</td>
        <td>9080</td>
        <td>\d+</td>
    </tr>
    <tr>
        <td>stepTimeoutSeconds</td>
        <td>yes</td>
        <td>How long the Chorus interpreter should wait for a result after executing a step on a web socket client before failing the step</td>
        <td>60</td>
        <td>\d+</td>
    </tr>
    <tr>
        <td>clientConnectTimeoutSeconds</td>
        <td>yes</td>
        <td>How long the Chorus interpreter should wait to receive a connection from a client before failing the connection step</td>
        <td>60</td>
        <td>\d+</td>
    </tr>
    <tr>
        <td>scope</td>
        <td>yes</td>
        <td>Whether the web socket should be closed at the end of each scenario, or at the end of the feature</td>
        <td>SCENARIO</td>
        <td>One of: SCENARIO, FEATURE</td>
    </tr>

</table>
