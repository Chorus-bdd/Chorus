---
layout: page
title: Starting Processes
---

Chorus has a [built in handler](/pages/BuiltInHandlers/BuiltInHandlers) class which allows you to start and stop local processes during a test.
 
For example, using the Processes handler would allow you to do the following:
 
    Uses: Processes

    Feature: Start a local publisher and subscriber
        Given I start a publisher process named pub
        And I start a subscriber process named sub

Usually you don't want you test to contain steps which do something technical like the this.
Instead, you can also use [directives](/pages/LanguageExtensions/Directives) to start a process:

    Uses: Processes

    #! Processes start pub, sub
    Feature: Start a local publisher and subscriber
        Given I ...
        When I ...

        
If you wish to connect and run steps on the process you started, you can tell the processes handler to 'connect':

    Uses: Processes

    #! Processes start pub, sub
    #! Processes connect pub, sub
    Feature: Send from publisher process and receive in subscriber process
        When I send 10 messages
        Then I receive 10 messages

To get this work you just need to supply a remotingPort in the configuration for each process. Then you can then [export test steps](/pages/BuiltInHandlers/Remoting/RemotingHandlerQuickStart) from the processes and call them during the scenario


[Processes Handler Quick Start](/pages/BuiltInHandlers/Processes/ProcessesHandlerQuickStart)




