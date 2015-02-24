---
layout: page
title: Timers Handler
---

Chorus provides a [built in handler](/pages/BuiltInHandlers/BuiltInHandlers) called `Timers`

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

###Consider the @PassesWithin annotation###

In most cases, where you are considering using a sleep, you would be better off using Chrous' @PassesWithin annotation.
Using @PassesWithin you can annotate a step method to run repeatedly for a period of time, waiting for assertions to be satisfied.
This approach can make your features run more quickly, and be less prone to make your tests fail due to timing-related issues.

See [Handler Classes](/pages/Handlers/HandlerClasses) for examples of @PassesWithin

##Some other examples##

        ..
        I wait for 50 milliseconds for the message to be sent
        ..
        I wait half a second for a message to be received
        
        
