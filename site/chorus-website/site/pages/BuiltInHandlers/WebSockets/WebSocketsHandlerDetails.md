---
layout: page
title: Web Sockets Handler Details
section: Web Sockets
sectionIndex: 30
---



* [Handler Steps](#steps)  
* [Handler Properties](#properties)


<a name="steps"/>
## Steps available in the Web Sockets Handler:


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



<a name="properties"/>
## Configuration properties for the Web Sockets Handler:

<table>
    <tr>
        <th>Property</th><th>Is Mandatory</th><th>Description</th><th>Default</th><th>Validation</th>
    </tr>
    <tr>
        <td>scope</td>
        <td>yes</td>
        <td>Whether the web socket should be closed at the end of each scenario, or at the end of the feature</td>
        <td>SCENARIO</td>
        <td>One of: SCENARIO, FEATURE</td>
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
        <td>port</td>
        <td>yes</td>
        <td>Which local port the web socket server should listen on</td>
        <td>9080</td>
        <td>\d+</td>
    </tr>

</table>
