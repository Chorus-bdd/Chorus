---
layout: page
title: SQL Handler Details
section: Built In Handlers
sectionIndex: 30
---



* [Handler Steps](#steps)  
* [Handler Properties](#properties)


<a name="steps"/>
## Steps available in the SQL Handler:


<table>
    <tr>
        <th>Step</th><th>Example</th><th>Deprecated</th><th>Description</th><th>Retry Duration (wait for step to pass)</th>
    </tr>
    <tr>
        <td>.*I connect to the ([a-zA-Z0-9-_]+) database</td>
        <td>Given I connect to the mySql database</td>
        <td>No</td>
        <td>Connect to the named database using the connection parameters configured in the handler properties</td>
        <td></td>
    </tr>
    <tr>
        <td>.*I execute the statement '(.*)' on the ([a-zA-Z0-9-_]+) database</td>
        <td>When I execute the statement 'insert into MyUsers values ("Bob")' on the mySql database</td>
        <td>No</td>
        <td>Execute the provided text as a statement against the connected database with given name</td>
        <td></td>
    </tr>
    <tr>
        <td>.*I execute the script (.*) on the ([a-zA-Z0-9-_]+) database</td>
        <td>When I execute the script mySqlScript.sql on the mySql database</td>
        <td>No</td>
        <td>Execute a SQL script from a file path relative to the feature directory against the connected database with given name. The script file may contain one or more semi-colon delimited SQL statements</td>
        <td></td>
    </tr>

</table>



<a name="properties"/>
## Configuration properties for the SQL Handler:

<table>
    <tr>
        <th>Property</th><th>Is Mandatory</th><th>Description</th><th>Default</th><th>Validation</th>
    </tr>
    <tr>
        <td>driverClassName</td>
        <td>yes</td>
        <td>Fully qualified Class name of the JDBC driver</td>
        <td></td>
        <td></td>
    </tr>
    <tr>
        <td>scope</td>
        <td>yes</td>
        <td>Whether the database connection is closed at the end of the scenario or at the end of the feature. This will be set automatically to FEATURE for connections established during 'Feature-Start:' if not provided, otherwise Scenario</td>
        <td>SCENARIO</td>
        <td>One of: SCENARIO, FEATURE</td>
    </tr>
    <tr>
        <td>url</td>
        <td>yes</td>
        <td>URL to establish JDBC connection</td>
        <td></td>
        <td></td>
    </tr>
    <tr>
        <td>password</td>
        <td>no</td>
        <td>JDBC connection password</td>
        <td></td>
        <td></td>
    </tr>
    <tr>
        <td>username</td>
        <td>no</td>
        <td>JDBC connection username</td>
        <td></td>
        <td></td>
    </tr>

</table>
