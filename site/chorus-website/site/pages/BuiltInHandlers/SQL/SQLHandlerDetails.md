---
layout: page
title: SQL Handler Details
section: SQL
sectionIndex: 30
---

### Overview 

The SQL handler can connect to a database to execute statements or SQL scripts during a test scenario, using a Java JDBC driver

* [Handler Steps](#steps)  
* [Handler Properties](#properties)

## How to use the SQL Handler

You can use the SQL Handler by adding 'Uses: SQL' to the top of your feature file:
There are steps provided to run a statement inline, or to run a sequence of statements from a file with a path relative 
to the feature directory

    Uses: SQL

    Feature: Sql Handler Simple Db

      Test that we can run statements on a SQL database

      Feature-Start:
        First I connect to the mydb database

      Scenario: I can run a statement inline
        Given the mydb database is connected
        Then I execute the statement 'truncate table ExampleTable' on the mydb database

      Scenario: I can run a statement from a script file
        Given the mydb database is connected
        Then I execute the script mySqlStatements.sql on the mydb database    
         

You will need to ensure the chorus-sql extension is on your classpath if using the JUnit Suite Runner, e.g. for a Maven project:

    <dependency>
        <groupId>org.chorusbdd</groupId>
        <artifactId>chorus-sql</artifactId>
        <version>3.1.0</version>
        <scope>test</scope>
    </dependency>

### Configuring the SQL Handler

You can configure named databases in your feature properties
e.g. to configure a database named 'mydb' using the derby JDBC driver

sql.mydb.driverClassName=org.apache.derby.jdbc.EmbeddedDriver
sql.mydb.url=jdbc:derby:/derby/sampleDB;

See [Handler Configuration](/pages/Handlers/HandlerConfiguration) for more details on configuring handlers

### Closing the Database connection

The database connection will be automatically closed at the end of the test feature (if FEATURE scope is used) or at the end of 
each scenario (if SCENARIO scope is used). Scope will default to SCENARIO unless the database is created in the 
special Feature-Start: scenario, in which case it will default to FEATURE


### For more details of the Web Sockets Handler in use with the chorus-js library

See [Chorus JS](/pages/DistributedTesting/ChorusJS) 


  
<br/>
<a name="steps"/>
## Steps available in the SQL Handler:
  
<br/>
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
  

<br/>
<a name="properties"/>
## Configuration properties for the SQL Handler:
  
<br/>
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
        <td>url</td>
        <td>yes</td>
        <td>URL to establish JDBC connection</td>
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
    <tr>
        <td>password</td>
        <td>no</td>
        <td>JDBC connection password</td>
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

</table>
