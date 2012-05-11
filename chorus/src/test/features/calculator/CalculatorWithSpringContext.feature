Uses: Calculator

Configurations: MKV, RMDS

Feature: Spring Context Fixture
  Checks that the interpreter creates a Spring context and injects the references
  to some basic Java objects.

  Scenario: Check the values where injected
    Assert the value of the injected Integer is '999'
    Assert the value of the injected String is 'Hello Calculator'
