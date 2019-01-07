---
layout: page
title: Timers Handler
section: Timers
sectionIndex: 10
---

Chorus provides a [built in handler](/pages/BuiltInHandlers/BuiltInSteps) called `Timers`

You can use this by adding the following to the top of your feature file:

    Uses: Timers
    
Timers adds handling for scenarios in which you need to wait for an event to occur:

e.g.

    Scenario: My Scenario with Timers
        Given I submit a reservation for flight VS201 in bookingClient
        And there is a free seat on the flight VS201 in reservationsManager
        And I wait for 2 seconds for the reservation to be processed
        Then I have a seat reserved for flight VS201 in reservationsManager
         
In the above example, the step `I wait for 2 seconds .*` would be matched by Timers handler

### Consider Step Retry 

In most cases, where you are considering using a sleep, you would be better off using Chrous' Step Retry capability.
Using Step Retry you can annotate a step method to run repeatedly for a period of time, waiting for assertions to be satisfied.

See [Step Retry](/pages/DistributedTesting/StepRetry)

This approach can make your features run more quickly, and be less prone to make your tests fail due to timing-related issues.


## Some other examples

        ..
        I wait for 50 milliseconds for the message to be sent
        ..
        I wait half a second for a message to be received
        
        
