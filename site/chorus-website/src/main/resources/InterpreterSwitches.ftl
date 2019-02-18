---
layout: page
title: Interpreter Switches
section: ${site.section}
sectionIndex: ${site.sectionIndex}
---

When running the chorus interpreter, the following parameters are supported. 
 
Each parameter can be set as a command line argument. It also has a System property equivalent

Where a parameter value is set as a System property this will generally override any value set as a command line switch.

<br/>

<table>
<tr>
  <th>Switch (Short Form, System Property)</th><th>Default</th><th>Example</th><th>Description</th>
</tr>
<#list interpreter.switches as parameter>
    <tr>
        <td>${parameter.hyphenatedSwitch} (-${parameter.switchShortName}, ${parameter.systemProperty})</td>
        <td>${parameter.defaultValue}</td>
        <td>${parameter.example}</td>
        <td>${parameter.description}</td>
    </tr>
</#list>
</table>
