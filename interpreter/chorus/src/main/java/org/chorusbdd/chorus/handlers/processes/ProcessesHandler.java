/**
 * MIT License
 *
 * Copyright (c) 2019 Chorus BDD Organisation.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.chorusbdd.chorus.handlers.processes;

import org.chorusbdd.chorus.annotations.*;
import org.chorusbdd.chorus.handlerconfig.ConfigPropertySource;
import org.chorusbdd.chorus.handlerconfig.ConfigurationManager;
import org.chorusbdd.chorus.handlerconfig.HandlerConfigLoader;
import org.chorusbdd.chorus.handlerconfig.configproperty.ConfigBuilderException;
import org.chorusbdd.chorus.handlerconfig.configproperty.ConfigPropertyParser;
import org.chorusbdd.chorus.handlerconfig.configproperty.ConfigurationProperty;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.processes.manager.ProcessManager;
import org.chorusbdd.chorus.processes.manager.config.ProcessConfigBean;
import org.chorusbdd.chorus.remoting.manager.RemotingManager;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.results.ScenarioToken;
import org.chorusbdd.chorus.util.ScopeUtils;
import org.chorusbdd.chorus.util.handler.HandlerPatterns;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.chorusbdd.chorus.util.assertion.ChorusAssert.fail;

/**  A handler for starting, stopping and communicating with processes */
@Handler(value = "Processes", scope= Scope.FEATURE)
@Documentation(description = "The Processes Handler provides steps which allow Chorus to start and stop local processes, check their standard output and error and provide input to a started process. ")
@SuppressWarnings("UnusedDeclaration")
public class ProcessesHandler implements ConfigPropertySource {

    private ChorusLog log = ChorusLogFactory.getLog(ProcessesHandler.class);

    @ChorusResource("feature.dir")
    private File featureDir;

    @ChorusResource("feature.file")
    private File featureFile;

    @ChorusResource("feature.token")
    private FeatureToken featureToken;

    @ChorusResource("scenario.token")
    private ScenarioToken scenarioToken;

    @ChorusResource("subsystem.processManager")
    private ProcessManager processManager;

    @ChorusResource("subsystem.remotingManager")
    private RemotingManager remotingManager;

    @ChorusResource("subsystem.configurationManager")
    private ConfigurationManager configurationManager;

    // -------------------------------------------------------------- Steps

    @Step(".*start a (.*) process")
    @Documentation(order = 10, description = "Start a process which is configured in handler properties", example = "Given I start a myProcess process")
    public void startProcessFromConfig(String configName) throws Exception {
        //no user process name was supplied so use the config name,
        //or the config name with a count appended if this config has already been started during the scenario
        Properties config = getConfig(configName);
        String processName = nextProcessName(configName);
        processManager.startProcess(configName, processName, config);
    }

    @Step(".*start an? (.+) process named " + HandlerPatterns.namePattern + ".*?")
    @Documentation(order = 20, description = "Start a process which is configured in handler properties, given it a name/alias. This allows the same configuration to be used for several named process instances", example = "Given I start a myProcess process named myProcess_A")
    public void startNamedProcessFromConfig(String configName, String processName) throws Exception {
        Properties config = getConfig(configName);
        processManager.startProcess(configName, processName, config);
    }

    @Step(".*stop (?:the )?process (?:named )?" + HandlerPatterns.namePattern + ".*?")
    @Documentation(order = 30, description = "Stop the process with the given name", example = "Then I stop the process myProcess")
    public void stopProcess(String processName) {
        processManager.stopProcess(processName);
    }

    @Step(value = ".*?(?:the process )?(?:named )?" + HandlerPatterns.namePattern + " (?:is |has )(?:stopped|terminated).*?", retryDuration = 5)
    @Documentation(order = 40, description = "Check the process with the given name which was running is now stopped", example = "And the process myProcess has stopped")
    public void checkProcessHasStopped(String processName) {
        processManager.checkProcessHasStopped(processName);
    }

    @Step(".*?(?:the process )?(?:named )?" + HandlerPatterns.namePattern + " is running")
    @Documentation(order = 50, description = "Check the process with the given name is running", example = "And the process myProcess is running")
    public void checkProcessIsRunning(String processName) {
        processManager.checkProcessIsRunning(processName);
    }

    @Step(value = ".*?(?:the process )?(?:named )?" + HandlerPatterns.namePattern + " is not running", retryDuration = 5)
    @Documentation(order = 60, description = "Check the process with the given name is not running", example = "Then the process myProcess is not running")
    public void checkProcessIsNotRunning(String processName) {
        processManager.checkProcessIsNotRunning(processName);
    }

    @Step(".*wait for (?:up to )?(\\d+) seconds for (?:the process )?(?:named )?" + HandlerPatterns.namePattern + " to (?:stop|terminate).*?")
    @Documentation(order = 70, description = "Wait for a running process to terminate for up to the specified number of seconds", example = "When I wait for up to 10 seconds for the process named myProcess to stop")
    public void waitXSecondsForProcessToTerminate(int waitSeconds, String processName) {
        processManager.waitForProcessToTerminate(processName, waitSeconds);
    }

    @Step(".*wait for (?:the process )?(?:named )?" + HandlerPatterns.namePattern + " to (?:stop|terminate).*?")
    @Documentation(order = 80, description = "Wait for a running process to terminate for up to the terminate wait time specified in the process config", example = "When I wait for myProcess to stop")
    public void waitForProcessToTerminate(String processName) {
        processManager.waitForProcessToTerminate(processName);
    }

    @Step(".*read the line '(.*)' from (?:the )?" + HandlerPatterns.namePattern + " process")
    @Documentation(order = 90, description = "Read a line of standard output from the named process which matches the pattern specified, this will be stored into the Chorus Context variable 'ProcessesHandler.match', waiting for the read timeout specified in the process config", example = "Then I read the line 'user \\w+ logged in' from the myProcess process")
    public void readLineFromProcess(String pattern, String processName) {
        processManager.readFromProcess(pattern, processName, false);
    }

    @Step(".*read the line '(.*)' from (?:the )?" + HandlerPatterns.namePattern + " process std error")
    @Documentation(order = 100, description = "Read a line of standard error from the named process which matches the pattern specified, this will be stored into the Chorus Context variable 'ProcessesHandler.match', waiting for the read timeout specified in the process config", example = "Then I read the line 'user \\w+ logged in' from the myProcess process std error")
    public void readLineFromProcessStdError(String pattern, String processName) {
        processManager.readFromProcessStdError(pattern, processName, false);
    }

    @Step(".*read the line '(.*)' from (?:the )?" + HandlerPatterns.namePattern + " process within (\\d+) second(?:s)?")
    @Documentation(order = 110, description = "Read a line of standard output from the named process which matches the pattern specified, this will be stored into the Chorus Context variable 'ProcessesHandler.match', waiting for the number of seconds specified", example = "Then I read the line 'user \\w+ logged in' from the myProcess process within 5 seconds")
    public void readLineFromProcessWithinNSeconds(String pattern, String processName, int seconds) {
        processManager.readFromProcessWithinNSeconds(pattern, processName, false, seconds);
    }

    @Step(".*read the line '(.*)' from (?:the )?" + HandlerPatterns.namePattern + " process std error within (\\d+) second(?:s)?")
    @Documentation(order = 120, description = "Read a line of standard error from the named process which matches the pattern specified, this will be stored into the Chorus Context variable 'ProcessesHandler.match', waiting for the number of seconds specified", example = "Then I read the line 'user \\w+ logged in' from the myProcess process within 5 seconds")
    public void readLineFromProcessStdErrorWithinNSeconds(String pattern, String processName, int seconds) {
        processManager.readFromProcessStdErrorWithinNSeconds(pattern, processName, false, seconds);
    }

    @Step(".*read '(.*)' from (?:the )?" + HandlerPatterns.namePattern + " process")
    @Documentation(order = 130, description = "Read standard output from the named process which matches the pattern specified, matching within lines. This will be stored into the Chorus Context variable 'ProcessesHandler.match'. Wait for up to the read timeout specified in the process config", example = "Then I read 'user \\w+ logged in' from the myProcess process")
    public void readFromProcess(String pattern, String processName) {
        processManager.readFromProcess(pattern, processName, true);
    }

    @Step(".*read '(.*)' from (?:the )?" + HandlerPatterns.namePattern + " process std error")
    @Documentation(order = 140, description = "Read standard error from the named process which matches the pattern specified, matching within lines. This will be stored into the Chorus Context variable 'ProcessesHandler.match'. Wait for up to the read timeout specified in the process config", example = "Then I read 'user \\w+ logged in' from the myProcess process std errorjj")
    public void readFromProcessStdError(String pattern, String processName) {
        processManager.readFromProcessStdError(pattern, processName, true);
    }

    @Step(".*read '(.*)' from (?:the )?" + HandlerPatterns.namePattern + " process within (\\d+) second(?:s)?")
    @Documentation(order = 150, description = "Read standard output from the named process which matches the pattern specified, matching within lines. This will be stored into the Chorus Context variable 'ProcessesHandler.match'. Wait for the specified number of seconds", example = "Then I read 'user \\w+ logged in' from the myProcess process")
    public void readFromProcessWithinNSeconds(String pattern, String processName, int seconds) {
        processManager.readFromProcessWithinNSeconds(pattern, processName, true, seconds);
    }

    @Step(".*read '(.*)' from (?:the )?" + HandlerPatterns.namePattern + " process std error within (\\d+) second(?:s)?")
    @Documentation(order = 160, description = "Read standard error from the named process which matches the pattern specified, matching within lines. This will be stored into the Chorus Context variable 'ProcessesHandler.match'. Wait for the specified number of seconds", example = "Then I read 'user \\w+ logged in' from the myProcess process")
    public void readFromProcessStdErrorWithinNSeconds(String pattern, String processName, int seconds) {
        processManager.readFromProcessStdErrorWithinNSeconds(pattern, processName, true, seconds);
    }

    @Step(".*write the line '(.*)' to (?:the )?" + HandlerPatterns.namePattern + " process")
    @Documentation(order = 170, description = "Write the supplied text followed by a line terminator to the named process standard input", example = "When I write the line 'hello hello' to the myProcess process")
    public void writeLineToProcess(String line, String processName) {
        processManager.writeToProcess(line, processName, true);
    }

    @Step(".*write '(.*)' to (?:the )?" + HandlerPatterns.namePattern + " process")
    @Documentation(order = 180, description = "Write the supplied text to the named process standard input, no line terminator will be appended", example = "When I write the line 'hello hello' to the myProcess process")
    public void writeToProcess(String line, String processName) {
        processManager.writeToProcess(line, processName, false);
    }

    //A Directive which can be used to start one or more processes using the config name
    @Step("Processes start " + HandlerPatterns.nameListPattern)
    @Documentation(order = 190, description = "Start the list of processes (as a directive)", example = "#! Processes start myProcess) mySecondProcess, myThirdProcess")
    public void startProcessDirective(String processNameList) throws Exception {
        Map<String,String> processNames = HandlerPatterns.getNamesWithAliases(processNameList);
        for ( Map.Entry<String,String> e : processNames.entrySet()) {
            startNamedProcessFromConfig(e.getValue(), e.getKey());
        }
    }

    //A Directive which can be used to connect to one or more processes using the config name so we an run steps on them
    @Step("Processes connect " + HandlerPatterns.nameListPattern)
    @Documentation(order = 200, description = "Connect to the list of processes using Chorus' remoting features, a remoting port must have been specified in the process config and the processes must be exporting step definitions", example = "#! Processes connect myProcess) mySecondProcess, myThirdProcess")
    public void remotingUseDirective(String processNameList) throws Exception {
        List<String> componentNames = HandlerPatterns.getNames(processNameList);
        for ( String componentName : componentNames) {
            processManager.checkProcessIsRunning(componentName);
            Properties remotingProperties = getRemotingConfig(componentName);
            remotingManager.connect(componentName, remotingProperties);
        }
    }


    //for first process just use the config processName with no suffix
    //this is so if we just start a single myconfig process, it will be called myconfig
    //the second will be called myconfig-2
    private synchronized String nextProcessName(String configName) {
        int instancesStarted = processManager.getNumberOfInstancesStarted(configName);
        return instancesStarted == 0 ? configName : String.format("%s-%d", configName, instancesStarted + 1);
    }

    private Properties getConfig(String configName) {
        Properties p = new HandlerConfigLoader().loadPropertiesForSubGroup(configurationManager, "processes", configName);
        new ScopeUtils().setScopeForContextIfNotConfigured(scenarioToken, p);
        return p;
    }

    /**
     * Generate a remoting config to connect to a running process
     */
    private Properties getRemotingConfig(String processName) {
        Properties processProps = processManager.getProcessProperties(processName);
        if ( processProps == null) {
            fail("Process " + processName + " is not running");
        }

        int remotingPort = Integer.parseInt(processProps.getProperty(ProcessConfigBean.REMOTING_PORT_PROPERTY));
        String scope = processProps.getProperty(ProcessConfigBean.SCOPE_PROPERTY);

        if ( remotingPort == -1) {
            fail("Cannot connect " + processName + " unknown remoting port");
        }
        Properties remotingProps = new Properties();
        remotingProps.put("connection", "jmx:localhost:" + remotingPort);
        remotingProps.put("scope", scope);
        return remotingProps;
    }


    @Override
    public List<ConfigurationProperty> getConfigProperties() throws ConfigBuilderException {
        return new ConfigPropertyParser().getConfigProperties(ProcessConfigBean.class);
    }
}