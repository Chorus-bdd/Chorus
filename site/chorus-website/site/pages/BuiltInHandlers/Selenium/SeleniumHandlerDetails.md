---
layout: page
title: Selenium Handler Details
section: Selenium
sectionIndex: 30
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


  
<br/>
<a name="steps"/>
## Steps available in the Selenium Handler:
  
<br/>
<table>
    <tr>
        <th>Step</th><th>Example</th><th>Deprecated</th><th>Description</th><th>Retry Duration (wait for step to pass)</th>
    </tr>
    <tr>
        <td>.*open Chrome</td>
        <td>Given I open Chrome</td>
        <td>Deprecated</td>
        <td>Open a window in a local Chrome browser using the chromedriver installed on the system. Deprecated - use 'open the Chrome browser' which allows configuration</td>
        <td></td>
    </tr>
    <tr>
        <td>.*open the ([a-zA-Z0-9-_]+) browser</td>
        <td>Given I open the myChrome browser</td>
        <td>No</td>
        <td>Open a window in a named browser which is defined in handler properties</td>
        <td></td>
    </tr>
    <tr>
        <td>.*navigate the ([a-zA-Z0-9-_]+) browser to (.*)</td>
        <td>When I navigate the myChrome browser to http://www.bbc.co.uk</td>
        <td>No</td>
        <td>Open an provided URL in the window of the named browser</td>
        <td></td>
    </tr>
    <tr>
        <td>.*navigate to (.*)</td>
        <td>When I navigate to http://www.bbc.co.uk</td>
        <td>No</td>
        <td>Open the provided URL in the most recently opened browser window</td>
        <td></td>
    </tr>
    <tr>
        <td>.*refresh the page</td>
        <td>And I refresh the page</td>
        <td>No</td>
        <td>Refresh the current page in the most recently opened browser window</td>
        <td></td>
    </tr>
    <tr>
        <td>.*refresh the page in the ([a-zA-Z0-9-_]+) browser</td>
        <td>And I refresh the page in the myChrome browser</td>
        <td>No</td>
        <td>Refresh the current page in the named browser window</td>
        <td></td>
    </tr>
    <tr>
        <td>.*the url is (.*)</td>
        <td>Then the url is http://www.bbc.co.uk</td>
        <td>No</td>
        <td>Test the URL in the most recently opened browser matches the provided URL</td>
        <td>2 SECONDS</td>
    </tr>
    <tr>
        <td>.*the url in the ([a-zA-Z0-9-_]+) browser is (.*)</td>
        <td>Then the url in the myChrome browser is http://www.bbc.co.uk</td>
        <td>No</td>
        <td>Test the URL in the named browser matches the provided URL</td>
        <td>2 SECONDS</td>
    </tr>
    <tr>
        <td>.*close the browser</td>
        <td>Then I close the browser</td>
        <td>No</td>
        <td>Close the most recently opened browser window</td>
        <td></td>
    </tr>
    <tr>
        <td>.*close the ([a-zA-Z0-9-_]+) browser</td>
        <td>Then I close the myChrome browser</td>
        <td>No</td>
        <td>Close the open window in the named browser</td>
        <td></td>
    </tr>
    <tr>
        <td>.*execute the script (.*) in the browser</td>
        <td>Then I execute the script myJavascript.js in the browser</td>
        <td>No</td>
        <td>Execute a script at the file path relative to the feature directory within the window of the last opened browser</td>
        <td></td>
    </tr>
    <tr>
        <td>.*execute the script (.*) in the ([a-zA-Z0-9-_]+) browser</td>
        <td>Then I execute the script myJavascript.js in the myChrome browser</td>
        <td>No</td>
        <td>Execute a script at the file path relative to the feature directory within the window of the named browser</td>
        <td></td>
    </tr>

</table>
  

<br/>
<a name="properties"/>
## Configuration properties for the Selenium Handler:
  
<br/>
<table>
    <tr>
        <th>Property</th><th>Is Mandatory</th><th>Description</th><th>Default</th><th>Validation</th>
    </tr>
    <tr>
        <td>driverType</td>
        <td>yes</td>
        <td>Defines the selenium driver type, e.g. CHROME or REMOTE_WEB_DRIVER</td>
        <td>REMOTE_WEB_DRIVER</td>
        <td>One of: CHROME, REMOTE_WEB_DRIVER</td>
    </tr>
    <tr>
        <td>chromeDriver.arguments</td>
        <td>no</td>
        <td>Arguments to pass to the chrome browser if using CHROME driver type</td>
        <td></td>
        <td></td>
    </tr>
    <tr>
        <td>remoteWebDriver.browserType</td>
        <td>no</td>
        <td>If using REMOTE_WEB_DRIVER, a value to pass to the remote selenium web driver to request a browser type, e.g. chrome, firefox, safari</td>
        <td>chrome</td>
        <td></td>
    </tr>
    <tr>
        <td>remoteWebDriver.URL</td>
        <td>no</td>
        <td>If using REMOTE_WEB_DRIVER, the URL to use to make the connection to the remote web driver or selenium grid</td>
        <td>http://localhost:4444/wd/hub</td>
        <td></td>
    </tr>
    <tr>
        <td>scope</td>
        <td>yes</td>
        <td>Defines whether a browser connection should be closed at the end of a feature, or after each scenario This will be set automatically to FEATURE for connections established during 'Feature-Start:' if not provided, otherwise Scenario</td>
        <td>SCENARIO</td>
        <td>One of: SCENARIO, FEATURE</td>
    </tr>

</table>
