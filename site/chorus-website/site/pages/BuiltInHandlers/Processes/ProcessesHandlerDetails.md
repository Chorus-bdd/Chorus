---
layout: page
title: Processes Handler Details
section: Processes
sectionIndex: 30
---

The Processes handler provides step definitions which allow you to start and stop local processes, and check their standard output and error.  
You can also provide input to a running process. 

* [Handler Steps](#steps)  
* [Handler Properties](#properties)

## Using the Processes Handler

### Create a test feature file which `Uses: Processes`

The top of your feature file (e.g. myProcess.feature) may look like this:

    Uses: Processes

    Feature: Start a Process Feature
      
      Scenario: Check we can start a process
        Given I start a myProcess process 

The process will automatically get stopped at the end of the scenario
Initially this will fail, since you have not yet provided any config to tell Chorus how to start 'myProcess'

### Configure the 'myProcess' process

To configure the process, add a myProcess.properties file in the same directory as your processTest.feature.

In the properties file you can configure processes.
For a Java process, you must set a property for the main class of your process as below:

    processes.myProcess.mainclass=com.mycom.MyProcess

The new process will use the same JVM and classpath as the chorus interpreter, but you can override these by setting other properties.

For a full list of processes properties scroll down to the bottom of this page
For full details on chorus properties files see [Handler Configuration](/pages/Handlers/HandlerConfiguration)


### Starting the same process under different names

In the example above, the configuration is called 'myProcess' but the process is named 'pub'
This distinction allows you to start multiple instances of the myProcess process under different logical names:

    I start a myProcess process named pub1
    I start a myProcess process named pub2


### Stopping the process

You generally don't need to stop processes explicitly using 'I stop the process' steps.  
By default any processes started during a scenario will be terminated by Chorus when that scenario is completed.

### Setting a process to feature scope

Processes started within a scenario are usually stopped automatically at the end of a scenario. The process is then 'scenario scoped'

If you wish to run a process throughout a feature, and reuse it between scenarios, you can configure it to be scoped at feature level, by setting the `scope` property:

e.g. `processes.myProcess.scope=feature`

Then the process will not be stopped until feature end.

If you start a process during the special [Feature-Start: scenario](/pages/GherkinExtensions/FeatureStartAndEnd) then it will automatically be scoped to 'feature scope' unless you configure it otherwise.


### Waiting for a process to terminate

If you need to wait for a process to terminate there are steps for this..

    I wait for the process pub to terminate
    or
    I wait for up to 10 seconds for the process pub to terminate


### Matching output from processes

If you set a process stdOutMode or stdErrMode to 'FILE' then you can match regular expressions against its output

    #file: myfeature.properties
    processes.myProcess.stdOutMode=captured

    I read the line 'my .* pattern' from the pub process
    or
    I read the line 'my .* pattern' from the pub process std error
    
    or if you are not expecting a line terminator: 
    I read 'my .* pattern' from the pub process
    
These steps will block for a period waiting to read the output from the process.
If the output is not detected, eventually the step will timeout and fail
The timeout length can be configured as a property.
    
### Writing to a process std input

You can also write input to a process which processes handler has started:

    I write the line 'my text here' to the pub process
    or
    I write 'my text no line terminator to be appended' to the pub process

### Logging from your processes

The processes you are starting may write to their standard output and standard error streams.

By default any output will written to a log file placed into a log directory under the feature file directory

Alternatively you can write the process output to console (inline) with the interpreter output but this can make the test output hard to read

Logging is configured independently for the process std out and error streams, using an output mode.
Output mode is one of

- inline
- file (log to a file) (the default)

The config below will write the std out of the myProcess process process to a log file, and show error output inline
n.b. the log file name will be calculated automatically from the feature and process name

    processes.myProcess.stdOutMode=file
    processes.myProcess.stdErrMode=inline
    processes.myProcess.logDirectory=${user.dir}/test/chorus/logs
    processes.myProcess.appendToLogs=false


### Log4j support

If the ProcessesHandler finds a file called **log4j.xml** in the same directory as your feature file, then it will set the appropriate log4j system property when starting your processes.

    -Dlog4j.configuration=${path to your log4j.xml

It will also set the following system properties which can be useful in your log4j configuration:

    -Dfeature.dir=${path to your feature directory}
    -Dfeature.process.name=${name of current feature}


### Setting defaults for all your processes

If you want all your processes to log to the same directory, you could set a default for this. The easiest way is to add a chorus.properties file at the top level on your test classpath. Use the special 'default' configuration group to specify the default property values:

    processes.default.logging=true
    processes.default.logDirectory=${user.dir}/test/chorus/logs

Now all processes will log into the above directory, unless you override them with a configuration specific property.

For full details on chorus properties files see [Handler Configuration](/pages/Handlers/HandlerConfiguration)

### Calling exported steps on the processes you start

Processes started by the process handler are often Chorus-enabled JVM processes. These can publish their own step
definitions using Chorus' Remoting featurs and the ChorusHandlerJmxExporter utility. It's a very common requirement
 to start a process using the Processes Handler and then call exported test steps on it

To do this you need to turn the jmx management service on for any processes you start (and have your processes export handler classes).
You can then connect to the process by setting the remotingPort property below to the correct port for the JMX service
started by the process under test.

    processes.myProcess.remotingPort=1234

For more details on remoting see [Remoting Handler Quick Start](/pages/BuiltInHandlers/Remoting/RemotingHandlerQuickStart)


### Starting a process which is not a java process

Instead of setting main class, you can set the property `pathToExecutable` to point to a script or native process.
This may be either an absolute path or a path which is relative to the feature file directory

  
<br/>
<a name="steps"/>
## Steps available in the Processes Handler:
  
<br/>
<table>
    <tr>
        <th>Step</th><th>Example</th><th>Deprecated</th><th>Description</th><th>Retry Duration (wait for step to pass)</th>
    </tr>
    <tr>
        <td>.*start a (.*) process</td>
        <td>Given I start a myProcess process</td>
        <td>No</td>
        <td>Start a process which is configured in handler properties</td>
        <td></td>
    </tr>
    <tr>
        <td>.*start an? (.+) process named ([a-zA-Z0-9-_]+).*?</td>
        <td>Given I start a myProcess process named myProcess_A</td>
        <td>No</td>
        <td>Start a process which is configured in handler properties, given it a name/alias. This allows the same configuration to be used for several named process instances</td>
        <td></td>
    </tr>
    <tr>
        <td>.*stop (?:the )?process (?:named )?([a-zA-Z0-9-_]+).*?</td>
        <td>Then I stop the process myProcess</td>
        <td>No</td>
        <td>Stop the process with the given name</td>
        <td></td>
    </tr>
    <tr>
        <td>.*?(?:the process )?(?:named )?([a-zA-Z0-9-_]+) (?:is |has )(?:stopped|terminated).*?</td>
        <td>And the process myProcess has stopped</td>
        <td>No</td>
        <td>Check the process with the given name which was running is now stopped</td>
        <td>5 SECONDS</td>
    </tr>
    <tr>
        <td>.*?(?:the process )?(?:named )?([a-zA-Z0-9-_]+) is running</td>
        <td>And the process myProcess is running</td>
        <td>No</td>
        <td>Check the process with the given name is running</td>
        <td></td>
    </tr>
    <tr>
        <td>.*?(?:the process )?(?:named )?([a-zA-Z0-9-_]+) is not running</td>
        <td>Then the process myProcess is not running</td>
        <td>No</td>
        <td>Check the process with the given name is not running</td>
        <td>5 SECONDS</td>
    </tr>
    <tr>
        <td>.*wait for (?:up to )?(\d+) seconds for (?:the process )?(?:named )?([a-zA-Z0-9-_]+) to (?:stop|terminate).*?</td>
        <td>When I wait for up to 10 seconds for the process named myProcess to stop</td>
        <td>No</td>
        <td>Wait for a running process to terminate for up to the specified number of seconds</td>
        <td></td>
    </tr>
    <tr>
        <td>.*wait for (?:the process )?(?:named )?([a-zA-Z0-9-_]+) to (?:stop|terminate).*?</td>
        <td>When I wait for myProcess to stop</td>
        <td>No</td>
        <td>Wait for a running process to terminate for up to the terminate wait time specified in the process config</td>
        <td></td>
    </tr>
    <tr>
        <td>.*read the line '(.*)' from (?:the )?([a-zA-Z0-9-_]+) process</td>
        <td>Then I read the line 'user \w+ logged in' from the myProcess process</td>
        <td>No</td>
        <td>Read a line of standard output from the named process which matches the pattern specified, this will be stored into the Chorus Context variable 'ProcessesHandler.match', waiting for the read timeout specified in the process config</td>
        <td></td>
    </tr>
    <tr>
        <td>.*read the line '(.*)' from (?:the )?([a-zA-Z0-9-_]+) process std error</td>
        <td>Then I read the line 'user \w+ logged in' from the myProcess process std error</td>
        <td>No</td>
        <td>Read a line of standard error from the named process which matches the pattern specified, this will be stored into the Chorus Context variable 'ProcessesHandler.match', waiting for the read timeout specified in the process config</td>
        <td></td>
    </tr>
    <tr>
        <td>.*read the line '(.*)' from (?:the )?([a-zA-Z0-9-_]+) process within (\d+) second(?:s)?</td>
        <td>Then I read the line 'user \w+ logged in' from the myProcess process within 5 seconds</td>
        <td>No</td>
        <td>Read a line of standard output from the named process which matches the pattern specified, this will be stored into the Chorus Context variable 'ProcessesHandler.match', waiting for the number of seconds specified</td>
        <td></td>
    </tr>
    <tr>
        <td>.*read the line '(.*)' from (?:the )?([a-zA-Z0-9-_]+) process std error within (\d+) second(?:s)?</td>
        <td>Then I read the line 'user \w+ logged in' from the myProcess process within 5 seconds</td>
        <td>No</td>
        <td>Read a line of standard error from the named process which matches the pattern specified, this will be stored into the Chorus Context variable 'ProcessesHandler.match', waiting for the number of seconds specified</td>
        <td></td>
    </tr>
    <tr>
        <td>.*read '(.*)' from (?:the )?([a-zA-Z0-9-_]+) process</td>
        <td>Then I read 'user \w+ logged in' from the myProcess process</td>
        <td>No</td>
        <td>Read standard output from the named process which matches the pattern specified, matching within lines. This will be stored into the Chorus Context variable 'ProcessesHandler.match'. Wait for up to the read timeout specified in the process config</td>
        <td></td>
    </tr>
    <tr>
        <td>.*read '(.*)' from (?:the )?([a-zA-Z0-9-_]+) process std error</td>
        <td>Then I read 'user \w+ logged in' from the myProcess process std errorjj</td>
        <td>No</td>
        <td>Read standard error from the named process which matches the pattern specified, matching within lines. This will be stored into the Chorus Context variable 'ProcessesHandler.match'. Wait for up to the read timeout specified in the process config</td>
        <td></td>
    </tr>
    <tr>
        <td>.*read '(.*)' from (?:the )?([a-zA-Z0-9-_]+) process within (\d+) second(?:s)?</td>
        <td>Then I read 'user \w+ logged in' from the myProcess process</td>
        <td>No</td>
        <td>Read standard output from the named process which matches the pattern specified, matching within lines. This will be stored into the Chorus Context variable 'ProcessesHandler.match'. Wait for the specified number of seconds</td>
        <td></td>
    </tr>
    <tr>
        <td>.*read '(.*)' from (?:the )?([a-zA-Z0-9-_]+) process std error within (\d+) second(?:s)?</td>
        <td>Then I read 'user \w+ logged in' from the myProcess process</td>
        <td>No</td>
        <td>Read standard error from the named process which matches the pattern specified, matching within lines. This will be stored into the Chorus Context variable 'ProcessesHandler.match'. Wait for the specified number of seconds</td>
        <td></td>
    </tr>
    <tr>
        <td>.*write the line '(.*)' to (?:the )?([a-zA-Z0-9-_]+) process</td>
        <td>When I write the line 'hello hello' to the myProcess process</td>
        <td>No</td>
        <td>Write the supplied text followed by a line terminator to the named process standard input</td>
        <td></td>
    </tr>
    <tr>
        <td>.*write '(.*)' to (?:the )?([a-zA-Z0-9-_]+) process</td>
        <td>When I write the line 'hello hello' to the myProcess process</td>
        <td>No</td>
        <td>Write the supplied text to the named process standard input, no line terminator will be appended</td>
        <td></td>
    </tr>
    <tr>
        <td>Processes start ([a-zA-Z0-9-_, ]+)</td>
        <td>#! Processes start myProcess) mySecondProcess, myThirdProcess</td>
        <td>No</td>
        <td>Start the list of processes (as a directive)</td>
        <td></td>
    </tr>
    <tr>
        <td>Processes connect ([a-zA-Z0-9-_, ]+)</td>
        <td>#! Processes connect myProcess) mySecondProcess, myThirdProcess</td>
        <td>No</td>
        <td>Connect to the list of processes using Chorus' remoting features, a remoting port must have been specified in the process config and the processes must be exporting step definitions</td>
        <td></td>
    </tr>

</table>
  

<br/>
<a name="properties"/>
## Configuration properties for the Processes Handler:
  
<br/>
<table>
    <tr>
        <th>Property</th><th>Is Mandatory</th><th>Description</th><th>Default</th><th>Validation</th>
    </tr>
    <tr>
        <td>mainclass</td>
        <td>no</td>
        <td>The class containing the main method which starts up your component (java processes only)</td>
        <td></td>
        <td></td>
    </tr>
    <tr>
        <td>classpath</td>
        <td>no</td>
        <td>The classpath to use when executing a Java process. If not set, the Chorus interpreter's classpath will be used</td>
        <td></td>
        <td></td>
    </tr>
    <tr>
        <td>jre</td>
        <td>no</td>
        <td>Path to the JRE to be used when executing a Java process. If not set, the Chorus interpreter's JVM will be used</td>
        <td></td>
        <td></td>
    </tr>
    <tr>
        <td>jvmargs</td>
        <td>no</td>
        <td>System properties (-D switches) to use when executing a Java process</td>
        <td></td>
        <td></td>
    </tr>
    <tr>
        <td>args</td>
        <td>no</td>
        <td>Arguments to pass to the process</td>
        <td></td>
        <td></td>
    </tr>
    <tr>
        <td>pathToExecutable</td>
        <td>no</td>
        <td>Path to a native executable process or script, the path may be absolute or relative to the feature directory. This property and the mainclass property are mutually exclusive - you should either one but not both</td>
        <td></td>
        <td></td>
    </tr>
    <tr>
        <td>stdOutMode</td>
        <td>no</td>
        <td>What do to with standard output stream from started process, one of INLINE (combine with interpreter stdout), FILE (write output to file). Other values are deprecated</td>
        <td></td>
        <td>One of: FILE, INLINE, CAPTURED, CAPTUREDWITHLOG</td>
    </tr>
    <tr>
        <td>stdErrMode</td>
        <td>no</td>
        <td>What do to with standard error stream from started process, one of INLINE (combine with interpreter stderr) or FILE (write output to file). Other values are deprecated</td>
        <td></td>
        <td>One of: FILE, INLINE, CAPTURED, CAPTUREDWITHLOG</td>
    </tr>
    <tr>
        <td>logging</td>
        <td>no</td>
        <td>If this property is set true, it will switch stdOutMode and stdErrorMode to FILE. If false then both will be INLINE. Leave it unset if you wish to set the stdOutMode and stdErrorMode individually</td>
        <td></td>
        <td>One of: true, false</td>
    </tr>
    <tr>
        <td>remotingPort</td>
        <td>yes</td>
        <td>Port on which to start the JMX remoting service. This is required when you want to use Chorus' Remoting features to connect to the process being started using JMX. Setting this property will add java system properties to turn on the JMX platform service. (java processes only), -1 to disable</td>
        <td>-1</td>
        <td>(-?)\d+</td>
    </tr>
    <tr>
        <td>debugPort</td>
        <td>yes</td>
        <td>Enable the debugger when starting the jvm and set it up to listen for connections on the port specified (java processes only), -1 to disable</td>
        <td>-1</td>
        <td>(-?)\d+</td>
    </tr>
    <tr>
        <td>terminateWaitTime</td>
        <td>yes</td>
        <td>Maximum time to wait for a process to terminate in seconds</td>
        <td>30</td>
        <td>\d+</td>
    </tr>
    <tr>
        <td>logDirectory</td>
        <td>no</td>
        <td>If you turn logging on, use this property to set the log directory. If not specified a logs directory will be created in the same directory as the feature file. May be an absolute path or a path relative to the working directory</td>
        <td></td>
        <td></td>
    </tr>
    <tr>
        <td>appendToLogs</td>
        <td>yes</td>
        <td>Whether to append to or overwrite log files</td>
        <td>false</td>
        <td>One of: true, false</td>
    </tr>
    <tr>
        <td>createLogDir</td>
        <td>yes</td>
        <td>Whether to auto-create the log directory if it does not exist</td>
        <td>true</td>
        <td>One of: true, false</td>
    </tr>
    <tr>
        <td>processCheckDelay</td>
        <td>yes</td>
        <td>Milliseconds after which to check started process is still running or fail the start process step. Longer values add better detection of immediate process start failures but incur an increased delay before subsequent steps run</td>
        <td>500</td>
        <td>(-?)\d+</td>
    </tr>
    <tr>
        <td>readTimeoutSeconds</td>
        <td>yes</td>
        <td>When matching a pattern against process output set the max time to wait for a match</td>
        <td>10</td>
        <td>\d+</td>
    </tr>
    <tr>
        <td>scope</td>
        <td>no</td>
        <td>Whether the process should be shut down at the end of the scenario or the end of the feature. this will be set automatically to FEATURE for processes started during 'Feature-Start:' if not provided, otherwise Scenario</td>
        <td>SCENARIO</td>
        <td>One of: SCENARIO, FEATURE</td>
    </tr>
    <tr>
        <td>enabled</td>
        <td>yes</td>
        <td>This property can be set to true to disable process start up when running in certain profiles</td>
        <td>true</td>
        <td>One of: true, false</td>
    </tr>

</table>
