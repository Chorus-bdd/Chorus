
Uses: SQL

Feature: Sql Handler Simple Db

  Test that we can load handler configs from the database if jdbc properties defined

  Feature-Start:
    First I create a derbydb database
    And I connect to the derbydb database

  Scenario: 
    Given the derbydb database is connected
    Then I execute the statement 'select * from ProcessProperties' on the derbydb database


