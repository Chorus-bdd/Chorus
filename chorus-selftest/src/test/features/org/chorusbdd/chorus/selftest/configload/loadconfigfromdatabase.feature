
Uses: Processes

Feature: Load Config From Database

  Test that we can load handler configs from the database if jdbc properties defined

  Feature-Start:
    First I create a javadb database and insert processes config

  Scenario: Start a process using db config
    When I start a myProcess process
    Then the process named myProcess is running



