---
layout: page
title: Interpreter Switches
section: Running Chorus
sectionIndex: 40
---

When running the chorus interpreter, the following parameters are supported. 
 
Each parameter can be set as a command line argument. It also has a System property equivalent

Where a parameter value is set as a System property this will generally override any value set as a command line switch.

<br/>

<table>
<tr>
  <th>Switch (Short Form, System Property)</th><th>Default</th><th>Example</th><th>Description</th>
</tr>
    <tr>
        <td>-featurePaths (-f, chorusFeaturePaths)</td>
        <td>No Default - Must be set</td>
        <td>-f c:\my\path ..\my\path  ..\my\path\myfeature.feature</td>
        <td>One or more relative or absolute paths to the directories containing your feature files or paths to specific feature files. Directories will be searched recursively</td>
    </tr>
    <tr>
        <td>-handlerPackages (-h, chorusHandlerPackages)</td>
        <td></td>
        <td>-h com.mycompany.mypkg</td>
        <td>Packages to scan for Handler classes. Subpackages will also be scanned</td>
    </tr>
    <tr>
        <td>-stepMacroPaths (-m, chorusStepMacroPaths)</td>
        <td></td>
        <td>-m c:\my\path ..\my\path  ..\my\path\mymacros.stepmacro</td>
        <td>Relative or absolute paths to the directories containing your stepmacro files or paths to specific stepmacro files. If not specified featurePaths will be used</td>
    </tr>
    <tr>
        <td>-dryrun (-d, chorusDryRun)</td>
        <td>false</td>
        <td>-d (false|true)</td>
        <td>Whether to actually execute steps or just detect and log the discovery of handlers and step definitions</td>
    </tr>
    <tr>
        <td>-showsummary (-s, chorusShowSummary)</td>
        <td>true</td>
        <td>-s (false|true)</td>
        <td>Whether to show the closing summary of pass/fail information</td>
    </tr>
    <tr>
        <td>-tagExpression (-t, chorusTagExpression)</td>
        <td></td>
        <td>-t @MyTagName</td>
        <td>One or more tags which can be used to restrict features which are executed</td>
    </tr>
    <tr>
        <td>-jmxListener (-j, chorusJmxListener)</td>
        <td></td>
        <td>-j myhost.mydomain:1001</td>
        <td>Network address of an agent which will receive execution events as the interpreter runs</td>
    </tr>
    <tr>
        <td>-suiteName (-n, chorusSuiteName)</td>
        <td>Test Suite</td>
        <td>-n My Suite Name</td>
        <td>Name for the test suite to be run</td>
    </tr>
    <tr>
        <td>-showErrors (-e, chorusShowErrors)</td>
        <td>false</td>
        <td>-e (false|true)</td>
        <td>Whether stack traces should be shown in the interpreter output (rather than just a message) when step implementations throws exceptions</td>
    </tr>
    <tr>
        <td>-logLevel (-l, chorusLogLevel)</td>
        <td>warn</td>
        <td>-l (trace|debug|info|warn|error|fatal)</td>
        <td>The log level to be used by Chorus' built in log provider</td>
    </tr>
    <tr>
        <td>-logProvider (-v, chorusLogProvider)</td>
        <td></td>
        <td>-v org.chorusbdd.chorus.logging.ChorusCommonsLogProvider</td>
        <td>ChorusLogProvider implementation used to instantiate Chorus Logger instances. Can redirect supplementary logging but not primary output. Set a custom OutputWriter if you want to redirect primary test output as well</td>
    </tr>
    <tr>
        <td>-scenarioTimeout (-o, chorusScenarioTimeout)</td>
        <td>360</td>
        <td>360</td>
        <td>Number of seconds after which a scenario will timeout</td>
    </tr>
    <tr>
        <td>-executionListener (-x, chorusExecutionListener)</td>
        <td></td>
        <td>com.mycom.MyListener</td>
        <td>One or more user specified ExecutionListener classes</td>
    </tr>
    <tr>
        <td>-outputWriter (-w, chorusOutputWriter)</td>
        <td>org.chorusbdd.chorus.output.PlainOutputWriter</td>
        <td>-w org.myorg.MyWriter</td>
        <td>The output writer used to write primary test output for Chorus, if specified without a classname places Chorus in console mode</td>
    </tr>
    <tr>
        <td>-console (-c, chorusConsoleMode)</td>
        <td>false</td>
        <td>-c</td>
        <td>Enable chorus console mode which is best when displaying output in a console</td>
    </tr>
    <tr>
        <td>-profile (-p, chorusProfile)</td>
        <td>base</td>
        <td>-p myProfile</td>
        <td>The configured profile for use in selecting Handler properties. A Handler might load diffent configuration based on the current profile</td>
    </tr>
    <tr>
        <td>-showStepCatalogue (-b, chorusShowStepCatalogue)</td>
        <td>false</td>
        <td>-b (false|true)</td>
        <td>Show metadata on steps supported by local handler classes and discovered by Chorus during the test run, includes invocation counts and cumulative time</td>
    </tr>
</table>
