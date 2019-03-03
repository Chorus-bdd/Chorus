---
layout: page
title: ${handler.name} Handler Details
section: ${site.section}
sectionIndex: ${site.sectionIndex}
---

### Overview 

The Selenium handler provides a way to open and interact with browsers during Chorus tests.

At present steps are provided to open a browser and navigate to a URL, but the full capabilities of Selenoium are not exposed.
This is because the Selenium Handler is generally used in conjunction with the Web Sockets handler and the chorus-js javascript library.

The chorus-js project allows a web app to connect to a web socket opened by Chorus and publish its own step definitions, so that 
test steps can be integrated within a web app. This can either be done by building chorus-js into the main web app, or by injecting 
a Chorus script into a web app after the page loads, using the Selenium handler's execute script test steps.

* [Handler Steps](#steps)  
* [Handler Properties](#properties)

## How to use the Selenium Handler

You can use this by adding 'Uses: Selenium' to the top of your feature file:

    Uses: Selenium
    
You will need to ensure the chorus-selenium extension is on your classpath if using the JUnit Suite Runner, e.g. for a Maven project:

    <dependency>
        <groupId>org.chorusbdd</groupId>
        <artifactId>chorus-selenium</artifactId>
        <version>3.1.0</version>
        <scope>test</scope>
    </dependency>
         
If you don't configure a browser in the properties, the Selenium handler will default to using Chrome Driver, which must be installed
and in the PATH on the local system. Alternatively, you can add a named config for a Chrome Driver or Remote Web Driver (which enables the use of Selenium Grid)

### Using the default Chrome Driver

#### The feature file may start like this:

    Uses: Selenium

    Feature-Start:
        Given I open Chrome
        And I navigate to http://my-big-single-page-app
       
    Scenario: Scenario One
        ...scenario steps here

### Configuring a Remote Web Driver

You can add properties in chorus.properties or myFeatureName.properties to define a named browser for use with the Selenium Handler:

e.g. to configure a browser named 'myBrowserName' which connects to a Selenium Hub at the address seleniumHub:4444/wd/hub:
        
    # chorus.properties:
    selenium.myBrowserName.driverType=REMOTE_WEB_DRIVER
    selenium.myBrowserName.remoteWebDriver.URL=http://seleniumHub:4444/wd/hub

See [Handler Configuration](/pages/Handlers/HandlerConfiguration) for more details on configuring handlers

### Closing the browser

The browser window will be closed automatically at the end of the feauture (if the scope is FEATURE) or at the end of the scenario (for SCENARIO scoped browser configs)


### For more details of the Selenium Handler in use with the chorus-js library

See [Chorus JS](/pages/DistributedTesting/ChorusJS) 


<#include "./handlerDetailsPageTemplate.ftl">