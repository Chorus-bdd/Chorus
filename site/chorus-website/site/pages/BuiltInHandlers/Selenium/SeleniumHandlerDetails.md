---
layout: page
title: Selenium Handler Details
section: Built In Handlers
sectionIndex: 30
---



* [Handler Steps](#steps)  
* [Handler Properties](#properties)


<a name="steps"/>
## Steps available in the Selenium Handler:


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



<a name="properties"/>
## Configuration properties for the Selenium Handler:

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
        <td>scope</td>
        <td>yes</td>
        <td>Defines whether a browser connection should be closed at the end of a feature, or after each scenario This will be set automatically to FEATURE for connections established during 'Feature-Start:' if not provided, otherwise Scenario</td>
        <td>SCENARIO</td>
        <td>One of: SCENARIO, FEATURE</td>
    </tr>
    <tr>
        <td>chromeDriver.arguments</td>
        <td>no</td>
        <td>Arguments to pass to the chrome browser if using CHROME driver type</td>
        <td></td>
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
        <td>remoteWebDriver.browserType</td>
        <td>no</td>
        <td>If using REMOTE_WEB_DRIVER, a value to pass to the remote selenium web driver to request a browser type, e.g. chrome, firefox, safari</td>
        <td>chrome</td>
        <td></td>
    </tr>

</table>
