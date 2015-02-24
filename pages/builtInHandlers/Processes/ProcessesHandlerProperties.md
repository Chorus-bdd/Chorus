---
layout: page
title: Processes Handler Properties
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
	<td>For Java, defaults to interpreter classpath</td>
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
	<td>false</td>
	<td>DEPRECATED - use stdOutMode and stdErrMode to configure logging process output to log files</td>
</tr>
<tr>
	<td>stdOutMode</td>
	<td>inline</td>
	<td>What do to with standard output stream from started process, one of inline, file, captured, capturedwithlog</td>
</tr>
<tr>
	<td>stdErrMode</td>
	<td>inline</td>
	<td>What do to with standard error stream from started process, one of inline, file, captured, capturedwithlog</td>
</tr>
<tr>
	<td>readAheadBufferSize</td>
	<td>65536</td>
	<td>Maximum length in bytes of process output to buffer when a process stdOut or stdErr is in 'captured' mode</td>
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
	<td>500</td>
	<td>miiliseconds after which to check started processes are still running or fail the start process step</td>
</tr>
<tr>
	<td>jmxport</td>
	<td>no default</td>
	<td>Port on which to start the jmx management service for your process. This is required when you are using Remoting handler. Setting this property will also cause the system properties which turn on the jmx management service to be set. (java processes only)</td>
</tr>
<tr>
	<td>debugport</td>
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
	<td>scenario</td>
	<td>The scope for the process, scenario or feature, feature scoped processes will be terminated at the end of the feature, scenario at the end of each scenario</td>
</tr>

</table>
