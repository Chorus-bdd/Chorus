---
layout: page
title: Remoting Handler Properties
---

The remoting handler allows you to set the following properties for each of your remote components

You can also set defaults and override them locally for a specific feature, see [Handler Configuration](/pages/Handlers/HandlerConfiguration)

These properties can be set in a chorus.properties file in the same directory as your feature file:

	remoting.myComponent.connection=jmx:localhost:12345
	remoting.myComponent.connectionAttempts=40

<table>
<tr>
	<th>Property</th><th>Default</th><th>Description</th>
</tr>
<tr>
	<td>connection</td>
	<td>no default</td>
	<td>A shorthand way of setting protocol host and port properties delimited by colon, e.g. jmx:myHost:myPort</td>
</tr>
<tr>
	<td>protocol</td>
	<td>jmx</td>
	<td>Protocol to use for connection, only jmx supported presently</td>
</tr>
<tr>
	<td>host</td>
	<td>no default</td>
	<td>host where remote component is running</td>
</tr>
<tr>
	<td>port</td>
	<td>no default</td>
	<td>port where remote component is running its jmx service</td>
</tr>
<tr>
	<td>connectionAttempts</td>
	<td>40</td>
	<td>Number of times to attempt connection before giving up</td>
</tr>
<tr>
	<td>connectionAttemptMillis</td>
	<td>250</td>
	<td>Wait time between each connection attempt</td>
</tr>
</table>
