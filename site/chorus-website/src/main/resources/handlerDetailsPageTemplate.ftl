---
layout: page
title: ${handler.name} Handler Properties
section: ${site.section}
sectionIndex: ${site.sectionIndex}
---

The steps available in the ${handler.name} Handler:

<table>
    <tr>
        <th>Step</th><th>Description</th><th>Retry Duration</th>
    </tr>
    <#list handler.steps as step>
    <tr>
        <td>${step.value}</td>
        <td></td>
        <td></td>
    </tr>
    </#list>

</table>



The supported configuration properties for the ${handler.name} Handler:

<table>
    <tr>
        <th>Property</th><th>Is Mandatory</th><th>Description</th><th>Default</th><th>Validation</th>
    </tr>
    <#list handler.configProperties as prop>
    <tr>
        <td>${prop.name}</td>
        <td>${prop.mandatory?string('yes', 'no')}</td>
        <td>${prop.description}</td>
        <td>${prop.defaultValue}</td>
        <td>${prop.validationPattern}</td>
    </tr>
    </#list>

</table>
