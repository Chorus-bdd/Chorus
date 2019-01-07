---
layout: page
title: Starting Processes
section: Processes
sectionIndex: 10
---

Chorus has a [built in handler](/pages/BuiltInHandlers/BuiltInSteps) class which allows you to start and stop local processes during a test.

You can also search for patterns in a process' standard output, and send standard input to a running process

Both Java processes and other executable processes are supported

For example, using the Processes handler would allow you to do the following:
 
    Uses: Processes

    Feature: Start a local publisher and subscriber
        Given I start a publisher process named pub
        And I start a subscriber process named sub
        ...

If preferred, you can also use [directives](/pages/GherkinExtensions/Directives) to start a process:

    Uses: Processes

    #! Processes start pub, sub
    Feature: Start a local publisher and subscriber
        Given I ...
        When I ...

        
If you wish to connect and run steps on the process you started, you can tell the processes handler to 'connect':

    Uses: Processes

    #! Processes start bookingService, quoteEngine
    #! Processes connect bookingService, quoteEngine
    Feature: I can book a trade when my quote is lifted
        When I quote a Bid Offer of 99.9 / 100.1 for T 5.5% 22/05/2020
        And my Bid price is lifted
        Then a SELL trade is booked for 99.9

To get this work you just need to supply a remotingPort in the configuration for each process. Then you can then [export test steps](/pages/BuiltInHandlers/Remoting/RemotingHandlerQuickStart) from the processes and call them during the scenario


[Processes Handler Quick Start](/pages/BuiltInHandlers/Processes/ProcessesHandlerQuickStart)




