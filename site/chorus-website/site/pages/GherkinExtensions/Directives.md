---
layout: page
title: Directives
section: Language Extensions
sectionIndex: 20
---

While testing a complex system, you often have to carry out technical actions in order to allow the tests to run.

For example, you may need to start a process, log in to a server and run a script, or even do something to bootstrap or tear down a testing environment.

Including these technical concerns as test steps can make the scenarios less expressive. Features are supposed to be written by a BA (ideally), and they should  describe requirements in business language, without technical implementation details getting in the way.

Directives in Chorus allows the BA to write the tests, and the developer to annotate them with technical Directives later.

### What are Directives?

Directives allow you to embed technical actions into features and scenarios, separately from the test steps

A directive is simply a statement preceded by a Unix 'shebang' (#!)

They are valid in two places:

1. Preceding a `Scenario:` or a `Scenario-Outline:` statement
2. At the end of a step (like a comment)

The following example shows how to place a Directive before a scenario:

    #! Remoting connect myBrowser, myServer
    Scenario: My Scenario
        Given I navigate to myAccount page
        When I click log out
        Then I am logged out from the server

You can place more than one Directive before a scenario in this way:

    #! Processes start myBrowser
    #! Remoting connect myBrowser, myServer
    Scenario: My Scenario
        Given I navigate to myAccount page
        When I click log out
        Then I am logged out from the server


You can also append a directive to a step:

    Scenario: My Scenario
        Given I navigate to myAccount page      #! Remoting connect myBrowser
        When I click log out
        Then I am logged out from the server    #! Remoting connect myServer

If you do this the directive will get executed before the step is run

### How are Directives executed?

Directives are really just a syntactic sugar for standard steps

The purpose of directives is to discriminate between technical actions and business steps within the feature file, so that your features read more cleanly, without adding more complexity to the framework.

The Chorus parser will generate a step from each directive, and the step is executed in the normal way.


### How a Directive is parsed

This example shows how a scenario containing directives is parsed

    #! Technical Directive One
    Scenario: My Scenario
       Given I navigate to myAccount page
       When I click log out                     #! Technical Directive Two
       Then I am logged out from the server

becomes the following steps when parsed:

    Scenario: My Scenario
       #! Technical Directive One
       Given I navigate to myAccount page
       #! Technical Directive Two
       When I click log out
       Then I am logged out from the server

The #! shebang is the step 'type' (equivalent to the Given, Then, When prefix) for steps which are directives


### How to implement a Directive within a `handler` class

Your handler classes support directives using the Chorus' standard @Step annotation

The only difference to a standard step is the convention that the pattern should start with the Handler name

For example:

    public class ProcessesHandler {

        @Step("Processes connect (.*)")
        public void connectProcesses(String processNames) {
            ....
        }
    }


