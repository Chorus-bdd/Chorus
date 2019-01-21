---
layout: page
title: ${handler.name} Handler Properties
section: ${site.section}
sectionIndex: ${site.sectionIndex}
---

The supported configuration properties for the ${handler.name} Handler:

<table>
    <tr>
        <th>Property</th><th>Is Mandatory</th><th>Default</th><th>Description</th>
    </tr>
    <#list handler.properties as prop>
    <tr>
        <td>${prop.name}</td>
        <td>${prop.mandatory?string('yes', 'no')}</td>
        <td>${prop.defaultValue}</td>
        <td>${prop.description}</td>
    </tr>
    </#list>

</table>
