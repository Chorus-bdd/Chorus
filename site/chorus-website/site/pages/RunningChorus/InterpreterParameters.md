---
layout: page
title: Interpreter Parameters
section: Running Chorus
sectionIndex: 20
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
  <td>none, must be set</td>
  <td>-f /myPath/mypath ../mypath ../mypath/myfeature.feature</td>
  <td>Relative or absolute paths to the directories containing your feature files or paths to specific feature files. Directories will be searched recursively</td>
</tr>
<tr>
  <td>-handlerPackages -(h, chorusHandlerPackages)</td>
  <td>none, must be set</td>
  <td>-h com.mycompany</td>
  <td>Handler package names to restrict the search for handler classes. Any packages which are subpackages/descendants of the package names specified are also included</td>
</tr>
<tr>
  <td>-stepMacroPaths (-m, chorusStepMacroPaths)</td>
  <td>Defaults to equal featurePaths</td>
  <td>-m c:\mystepmacros<br />chorus.util.logging.<br />StandardOutLogProvider</td>
  <td>Optionally allows your .stepmacro to be located in separate directory paths from you feature files. If not specified featurePaths will be used</td>
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
  <td>-t MyTagName</td>
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
  <td>-l (trace|debug|info|<br/>warn|error|fatal)</td>
  <td>The log level to be used by Chorus' built in log provider</td>
</tr>
<tr>
  <td>-logProvider (-v, chorusLogProvider)</td>
  <td></td>
  <td>-p org.chorusbdd.<br />chorus.util.logging.<br />StandardOutLogProvider</td>
  <td>The log provider which chorus uses for its log output and errors (n.b. the interpreter output is written to the interpreter process Standard out)</td>
</tr>
<tr>
  <td>-scenarioTimeout (-o, chorusScenarioTimeout)</td>
  <td>360</td>
  <td>-o 60</td>
  <td>Number of seconds after which a scenario will timeout. This should prevent a hung test case. Chorus will first try to interrupt the test thread. If this fails the chorus interpreter will be killed</td>
</tr>
<tr>
  <td>-executionListener (-x, chorusExecutionListener)</td>
  <td></td>
  <td>-x com.mycom.MyListener</td>
  <td>One or more classes which implements org.chorusbdd.chorus.executionlistener.ExecutionListener to receive lifecycle callbacks during the interpeter session. Must provide a nullary constructor.</td>
</tr>
<tr>
  <td>-outputFormatter (-r, chorusOutputFormatter)</td>
  <td></td>
  <td>--r com.mycom.MyFormatter</td>
  <td>A formatter which handles Chorus' output. A custom implementation can be supplied to change the way interpreter output and logging is displayedA</td>
</tr>
<tr>
  <td>-console (-c, chorusConsoleMode)</td>
  <td></td>
  <td>-c</td>
  <td>Turn on console output (better progress information). Use this switch when running Chorus in a console or IDE</td>
</tr>
<tr>
    <td>-profile</td>
    <td></td>
    <td>-p myProfile</td>
    <td>The profile to use when running Chorus. This can be used to change configuration.
Configuration properties may be included if they are prefixed by profile.profileName</td>
</tr>
<tr>
    <td>-showStepCatalogue (-b, chorusShowStepCatalogue)</td>
    <td>false</td>
    <td>-b (false|true)</td>
    <td>Show the catalogue of all step definitions encountered by the interpreter during the test run along with usage statistics</td>
</tr>
</table>
