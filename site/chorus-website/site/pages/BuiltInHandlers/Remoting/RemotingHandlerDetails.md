---
layout: page
title: Remoting Handler Details
section: Built In Handlers
sectionIndex: 30
---

The Remoting handler allows the chorus to connect to remote JVM-based processes and discover step definitions they publish using the ChorusHandlerJmxExporter utility

* [Handler Steps](#steps)  
* [Handler Properties](#properties)


<a name="steps"/>
## Steps available in the Remoting Handler:


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



<a name="properties"/>
## Configuration properties for the Remoting Handler:

<table>
    <tr>
        <th>Property</th><th>Is Mandatory</th><th>Description</th><th>Default</th><th>Validation</th>
    </tr>
    <tr>
        <td>connectionAttemptMillis</td>
        <td>yes</td>
        <td>Wait time between each connection attempt</td>
        <td>250</td>
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
        <td>protocol</td>
        <td>yes</td>
        <td>Protocol to make connection (only JMX supported at present)</td>
        <td>jmx</td>
        <td>jmx</td>
    </tr>
    <tr>
        <td>scope</td>
        <td>yes</td>
        <td>Whether the remoting connection is closed at the end of the scenario or at the end of the feature. This will be set automatically to FEATURE for connections established during 'Feature-Start:' if not provided, otherwise Scenario</td>
        <td>SCENARIO</td>
        <td>One of: SCENARIO, FEATURE</td>
    </tr>
    <tr>
        <td>connection</td>
        <td>no</td>
        <td>A shorthand way of setting protocol host and port properties delimited by colon, e.g. jmx:myHost:myPort</td>
        <td></td>
        <td>jmx:\S+:\d+</td>
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

</table>
