---
layout: page
title: Chorus Context Handler Details
section: Chorus Context
sectionIndex: 30
---



* [Handler Steps](#steps)  
* [Handler Properties](#properties)


<a name="steps"/>
## Steps available in the Chorus Context Handler:


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



<a name="properties"/>
## Configuration properties for the Chorus Context Handler:

<table>
    <tr>
        <th>Property</th><th>Is Mandatory</th><th>Description</th><th>Default</th><th>Validation</th>
    </tr>

</table>
