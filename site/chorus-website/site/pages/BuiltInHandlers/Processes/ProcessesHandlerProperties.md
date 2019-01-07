---
layout: page
title: Processes Handler Properties
section: Processes
sectionIndex: 30
---

The processes handler allows you to set the following properties for each of your processes

You can also set defaults and override them locally for a specific feature, see [Handler Configuration](/pages/Handlers/HandlerConfiguration)

These properties can be set in a chorus.properties file (or myfeature.properties file) in the same directory as your feature file, e.g.:

	processes.myprocess.mainclass=org.myorg.myapp.Main
    processes.myprocess.args=arg1

<table>
<tr>
	<th>Property</th><th>Default</th><th>Description</th>
</tr>
<tr>
	<td>mainclass</td>
	<td>no default, must be set for Java processes</td>
	<td>The class containing the main method which starts up your component (java processes only)</td>
</tr>
<tr>
	<td>pathToExecutable</td>
    <td>no default, must be set for non-Java native processes</td>
	<td>Path to a native executable process or script, the path may be absolute or relative to the feature directory. This property and the mainclass property are mutually exclusive - you should either one but not both</td>
</tr>
<tr>
	<td>classpath</td>
	<td>For Java, will default to share the Chorus interpreter's classpath</td>
	<td>Set the classpath which will be used by your process (for java processes only)</td>
</tr>
<tr>
	<td>args</td>
	<td>no default</td>
	<td>Arguments to pass to your process</td>
</tr>
<tr>
	<td>jvmargs</td>
	<td>no default</td>
	<td>system properties (-D switches) to use when starting the jvm for your process (java processes only)</td>
</tr>
<tr>
	<td>logging</td>
	<td>no default</td>
	<td>If this property is set true, it will switch stdOutMode and stdErrorMode to FILE. If false then both will be INLINE. Leave it unset if you wish to set the stdOutMode and stdErrorMode individually</td>
</tr>
<tr>
	<td>stdOutMode</td>
	<td>FILE</td>
	<td>What do to with standard output stream from started process, one of INLINE or FILE</td>
</tr>
<tr>
	<td>stdErrMode</td>
	<td>FILE</td>
	<td>What do to with standard error stream from started process, one of INLINE or FILE</td>
</tr>
<tr>
	<td>readTimeoutSeconds</td>
	<td>10</td>
	<td>Max time to wait in steps which match against process output in 'captured' mode</td>
</tr>
<tr>
	<td>logDirectory</td>
	<td>no default</td>
	<td>If you turn logging on, use this property to set the log directory</td>
</tr>
<tr>
	<td>appendToLogs</td>
	<td>false</td>
	<td>Whether to append to or overwrite log files</td>
</tr>
<tr>
	<td>createLogDir</td>
	<td>true</td>
	<td>Whether to auto-create the log directory if it does not exist</td>
</tr>
<tr>
	<td>processCheckDelay</td>
	<td>750</td>
	<td>miiliseconds after which to check started processes are still running or fail the start process step</td>
</tr>
<tr>
	<td>remotingPort</td>
	<td>no default</td>
	<td>Port on which to start the remoting service. This is required when you want to 'connect' to run steps exported by the process. When start a java process using the default jmx protocol for remoting, setting this property will cause the system properties which turn on the jmx management service to be set.</td>
</tr>
<tr>
	<td>debugPort</td>
	<td>no default</td>
	<td>Enable the debugger when starting the jvm and set it up to listen for connections on the port specified (java processes only)</td>
</tr>
<tr>
	<td>terminateWaitTime</td>
	<td>30</td>
	<td>Maximum time to wait for a process to terminate in seconds</td>
</tr>
<tr>
	<td>scope</td>
	<td>SCENARIO</td>
	<td>The scope for the process, SCENARIO or FEATURE. Feature scoped processes will be terminated at the end of the feature, scenario scoped at the end of each scenario. Processes default to SCENARIO scope unless started during the special Feature-Start: scenario in which case they default to FEATURE scope</td>
</tr>

</table>
