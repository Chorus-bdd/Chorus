---
layout: page
title: ${handler.name} Handler Details
section: ${site.section}
sectionIndex: ${site.sectionIndex}
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


<#include "./handlerDetailsPageTemplate.ftl">