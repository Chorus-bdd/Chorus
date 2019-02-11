---
layout: page
title: Timers Handler Details
section: Timers
sectionIndex: 30
---



* [Handler Steps](#steps)  
* [Handler Properties](#properties)


<a name="steps"/>
## Steps available in the Timers Handler:


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



<a name="properties"/>
## Configuration properties for the Timers Handler:

<table>
    <tr>
        <th>Property</th><th>Is Mandatory</th><th>Description</th><th>Default</th><th>Validation</th>
    </tr>

</table>
