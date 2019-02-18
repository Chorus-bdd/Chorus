---
layout: page
title: Chorus Context Handler Details
section: Chorus Context
sectionIndex: 30
---

### Overview

Chorus provides a [built in handler](/pages/BuiltInHandlers/BuiltInHandlers) called `Chorus Context`

This handler is used to view or manipulate variables stored within the [Chorus Context](/pages/BuiltInHandlers/ChorusContext/ChorusContext)

* [Handler Steps](#steps)  
* [Handler Properties](#properties)


## How to use the Chorus Context Handler

To use the handler, add `Uses: Chorus Context` to the top of your feature file.

You can then make use of the steps provided:

    Uses: Chorus Context

      Feature: Show Chorus Context Steps
      
        Scenario: Create a Context variable
          
          Given the context has no values in it
          When I create a context variable myVar with value 2
          Then the context variable myVar exists
          And the context variable myVar has the value 2
          And I show context variable myVar 
          #the last step above will show the value of the variable in Chorus' output
           
n.b. Most often Context variables are set or updated by the step implementation methods in handler classes.
The ChorusContext handler gives you the ability to view and manipulate these directly in your scenario.

## Loading variables into the context

You can define [handler properties](/pages/Handlers/HandlerConfiguration) which will be loaded into the ChorusContext at the start of each Scenario

These properties need to be prefixed with 'context.'

    context.myVariable=myValue
    context.myVariable2=myValue2

So that the Chorus Context handler will load these, you need to add `Uses: Chorus Context` at the top of your feature file

At present these variables are loaded into the context as String values

  
<br/>
<a name="steps"/>
## Steps available in the Chorus Context Handler:
  
<br/>
<table>
    <tr>
        <th>Step</th><th>Example</th><th>Deprecated</th><th>Description</th><th>Retry Duration (wait for step to pass)</th>
    </tr>
    <tr>
        <td>.*the context is empty</td>
        <td>Given then context is empty</td>
        <td>No</td>
        <td>Check there are no variables set in the Chorus Context</td>
        <td></td>
    </tr>
    <tr>
        <td>.*create a context variable (.*) with (?:the )?value (.*)</td>
        <td>When I create a context variable foo with the value bar</td>
        <td>No</td>
        <td>Create a variable within the Chorus Context with the value provided</td>
        <td></td>
    </tr>
    <tr>
        <td>.*context variable (.*) has (?:the )?value (.*)</td>
        <td>Then the context variable foo has the value bar</td>
        <td>No</td>
        <td>Check the named context variable has the value specified</td>
        <td></td>
    </tr>
    <tr>
        <td>.*context variable (.*) exists</td>
        <td>Then the context variable foo exists</td>
        <td>No</td>
        <td>Check the named Chorus Context variable exists</td>
        <td></td>
    </tr>
    <tr>
        <td>.*show (?:the )?context variable (.*)</td>
        <td>And I show the context variable foo</td>
        <td>No</td>
        <td>Show the current value of the context variable in Chorus' output</td>
        <td></td>
    </tr>
    <tr>
        <td>.*type of (?:the )?context variable (.*) is (.*)</td>
        <td>Then the type of the context variable foo is String</td>
        <td>No</td>
        <td>Check the type of the context variable (matching against the Java Class simple name)</td>
        <td></td>
    </tr>
    <tr>
        <td>.*add (?:the )?(?:value )?([\d\.]+) to (?:the )?context variable (.*)</td>
        <td>And I add 5 to the context variable myNumericValue</td>
        <td>No</td>
        <td>Add the value provided to the named context variable which must contain a numeric value</td>
        <td></td>
    </tr>
    <tr>
        <td>.*subtract (?:the )?(?:value )?([\d\.]+) from (?:the )?context variable (.*)</td>
        <td>And I subtract 5 from the context variable myNumericValue</td>
        <td>No</td>
        <td>Subtract the value provided to the named context variable which must contain a numeric value</td>
        <td></td>
    </tr>
    <tr>
        <td>.*multiply (?:the )?context variable (.*) by (?:the )?(?:value )?([\d\.]+)</td>
        <td>And I multiply the context variable myNumericValue by 10</td>
        <td>No</td>
        <td>Multiply the named context variable which must contain a numeric value by the specified number</td>
        <td></td>
    </tr>
    <tr>
        <td>.*divide (?:the )?context variable (.*) by (?:the )?(?:value )?([\d\.]+)</td>
        <td>And I divide the context variable myNumericValue by 10</td>
        <td>No</td>
        <td>Divide the named context variable which must contain a numeric value by the specified number</td>
        <td></td>
    </tr>
    <tr>
        <td>.*increment (?:the )?context variable (.*)</td>
        <td>When I increment the context variable myVariable</td>
        <td>No</td>
        <td>Add one to the named context variable which must contain a numeric value</td>
        <td></td>
    </tr>
    <tr>
        <td>.*decrement (?:the )?context variable (.*)</td>
        <td>When I decrement the context variable myVariable</td>
        <td>No</td>
        <td>Subtract one from the named context variable which must contain a numeric value</td>
        <td></td>
    </tr>
    <tr>
        <td>.*divide (?:the )?context variable (.*) by (.*) and take the remainder</td>
        <td>When I divide context variable myVar by 10 and take the remainder</td>
        <td>No</td>
        <td>Set the named context variable to the remainder after dividing it by the specified number</td>
        <td></td>
    </tr>
    <tr>
        <td>.*(?:the )?context variable (.*) is a (.*)</td>
        <td>Then the context variable myVar is a String</td>
        <td>No</td>
        <td>Assert the type of a context variable, by specifying the name of a concrete Java class.</td>
        <td></td>
    </tr>

</table>
  

<br/>
<a name="properties"/>
## Configuration properties for the Chorus Context Handler:
  
<br/>
<table>
    <tr>
        <th>Property</th><th>Is Mandatory</th><th>Description</th><th>Default</th><th>Validation</th>
    </tr>

</table>
