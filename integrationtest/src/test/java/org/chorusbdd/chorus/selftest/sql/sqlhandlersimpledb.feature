
Uses: SQL

Feature: Sql Handler Simple Db

  Test that we can load handler configs from the database if jdbc properties defined

  Feature-Start:
    First I create a derbydb database
    And I connect to the derbydb database

  Scenario: I can run a statement inline
    Given the derbydb database is connected
    Then I execute the statement 'select * from ProcessProperties' on the derbydb database

  Scenario: I can run a statement from a script file
    Given the derbydb database is connected
    Then I execute the script mySqlStatements.sql on the derbydb database    
    


