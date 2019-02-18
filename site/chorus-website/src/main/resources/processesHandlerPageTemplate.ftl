---
layout: page
title: ${handler.name} Handler Details
section: ${site.section}
sectionIndex: ${site.sectionIndex}
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
    processes.myProcess.logDirectory=${r"${user.dir}"}/test/chorus/logs
    processes.myProcess.appendToLogs=false


### Log4j support

If the ProcessesHandler finds a file called **log4j.xml** in the same directory as your feature file, then it will set the appropriate log4j system property when starting your processes.

    -Dlog4j.configuration=${r"${path to your log4j.xml"}

It will also set the following system properties which can be useful in your log4j configuration:

    -Dfeature.dir=${r"${path to your feature directory}"}
    -Dfeature.process.name=${r"${name of current feature}"}


### Setting defaults for all your processes

If you want all your processes to log to the same directory, you could set a default for this. The easiest way is to add a chorus.properties file at the top level on your test classpath. Use the special 'default' configuration group to specify the default property values:

    processes.default.logging=true
    processes.default.logDirectory=${r"${user.dir}"}/test/chorus/logs

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

<#include "./handlerDetailsPageTemplate.ftl">