  
<br/>
<a name="steps"/>
## Steps available in the ${handler.name} Handler:
  
<br/>
<table>
    <tr>
        <th>Step</th><th>Example</th><th>Deprecated</th><th>Description</th><th>Retry Duration (wait for step to pass)</th>
    </tr>
    <#list handler.steps as step>
    <tr>
        <td>${step.value}</td>
        <td>${step.example}</td>
        <td>${step.deprecated?string('Deprecated', 'No')}</td>
        <td>${step.description}</td>
        <td>${step.retryDuration}</td>
    </tr>
    </#list>

</table>
  

<br/>
<a name="properties"/>
## Configuration properties for the ${handler.name} Handler:
  
<br/>
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
