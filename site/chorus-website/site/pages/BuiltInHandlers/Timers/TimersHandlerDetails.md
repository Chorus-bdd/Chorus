---
layout: page
title: Timers Handler Details
section: Timers
sectionIndex: 30
---

### Overview 

Chorus provides a [built in handler](/pages/BuiltInHandlers/BuiltInHandlers) called `Timers`
This can provide a quick way to wait for a period of time between two steps in a scenario

* [Handler Steps](#steps)  
* [Handler Properties](#properties)

## How to use the Timers Handler

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
        
        
  
<br/>
<a name="steps"/>
## Steps available in the Timers Handler:
  
<br/>
<table>
    <tr>
        <th>Step</th><th>Example</th><th>Deprecated</th><th>Description</th><th>Retry Duration (wait for step to pass)</th>
    </tr>
    <tr>
        <td>.*wait (?:for )?([0-9]*) seconds?.*</td>
        <td>And I wait for 6 seconds</td>
        <td>No</td>
        <td>Wait for a number of seconds</td>
        <td></td>
    </tr>
    <tr>
        <td>.*wait (?:for )?([0-9]*) milliseconds?.*</td>
        <td>And I wait for 100 milliseconds</td>
        <td>No</td>
        <td>Wait for a number of milliseconds</td>
        <td></td>
    </tr>
    <tr>
        <td>.*wait (?:for )?half a second.*</td>
        <td>And I wait half a second</td>
        <td>No</td>
        <td>Wait for half a second</td>
        <td></td>
    </tr>

</table>
  

<br/>
<a name="properties"/>
## Configuration properties for the Timers Handler:
  
<br/>
<table>
    <tr>
        <th>Property</th><th>Is Mandatory</th><th>Description</th><th>Default</th><th>Validation</th>
    </tr>

</table>
