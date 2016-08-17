/**
 *  Copyright (C) 2000-2015 The Software Conservancy and Original Authors.
 *  All rights reserved.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to
 *  deal in the Software without restriction, including without limitation the
 *  rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 *  sell copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 *  IN THE SOFTWARE.
 *
 *  Nothing in this notice shall be deemed to grant any rights to trademarks,
 *  copyrights, patents, trade secrets or any other intellectual property of the
 *  licensor or any contributor except as expressly stated herein. No patent
 *  license is granted separate from the Software, for code that you delete from
 *  the Software, or for combinations of the Software with other software or
 *  hardware.
 */
package org.chorusbdd.chorus.handlers.processes;

import org.chorusbdd.chorus.annotations.ChorusResource;
import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Scope;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.handlerconfig.ConfigurationManager;
import org.chorusbdd.chorus.handlerconfig.HandlerConfigLoader;
import org.chorusbdd.chorus.handlers.utils.HandlerPatterns;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.processes.manager.ProcessManager;
import org.chorusbdd.chorus.processes.manager.config.ProcessesConfigBeanFactory;
import org.chorusbdd.chorus.remoting.manager.RemotingConfigBuilder;
import org.chorusbdd.chorus.remoting.manager.RemotingManager;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.results.ScenarioToken;
import org.chorusbdd.chorus.util.ScopeUtils;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.chorusbdd.chorus.util.assertion.ChorusAssert.fail;

/**  A handler for starting, stopping and communicating with processes */
@Handler(value = "Processes", scope= Scope.FEATURE)
@SuppressWarnings("UnusedDeclaration")
public class ProcessesHandler {

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
    public void startProcessFromConfig(String configName) throws Exception {
        //no user process name was supplied so use the config name,
        //or the config name with a count appended if this config has already been started during the scenario
        Properties config = getConfig(configName);
        String processName = nextProcessName(configName);
        processManager.startProcess(configName, processName, config);
    }

    @Step(".*start an? (.+) process named " + HandlerPatterns.processNamePattern + ".*?")
    public void startNamedProcessFromConfig(String configName, String processName) throws Exception {
        Properties config = getConfig(configName);
        processManager.startProcess(configName, processName, config);
    }

    @Step(".*stop (?:the )?process (?:named )?" + HandlerPatterns.processNamePattern + ".*?")
    public void stopProcess(String processName) {
        processManager.stopProcess(processName);
    }

    @Step(".*?(?:the process )?(?:named )?" + HandlerPatterns.processNamePattern + " (?:is |has )(?:stopped|terminated).*?")
    public void checkProcessHasStopped(String processName) {
        processManager.checkProcessHasStopped(processName);
    }

    @Step(".*?(?:the process )?(?:named )?" + HandlerPatterns.processNamePattern + " is running")
    public void checkProcessIsRunning(String processName) {
        processManager.checkProcessIsRunning(processName);
    }

    @Step(".*?(?:the process )?(?:named )?" + HandlerPatterns.processNamePattern + " is not running")
    public void checkProcessIsNotRunning(String processName) {
        processManager.checkProcessIsNotRunning(processName);
    }

    @Step(".*wait for (?:up to )?(\\d+) seconds for (?:the process )?(?:named )?" + HandlerPatterns.processNamePattern + " to (?:stop|terminate).*?")
    public void waitXSecondsForProcessToTerminate(int waitSeconds, String processName) {
        processManager.waitForProcessToTerminate(processName, waitSeconds);
    }

    @Step(".*wait for (?:the process )?(?:named )?" + HandlerPatterns.processNamePattern + " to (?:stop|terminate).*?")
    public void waitForProcessToTerminate(String processName) {
        processManager.waitForProcessToTerminate(processName);
    }

    @Step(".*read the line '(.*)' from (?:the )?" + HandlerPatterns.processNamePattern + " process")
    public void readLineFromProcess(String pattern, String processName) {
        processManager.readFromProcess(pattern, processName, false);
    }

    @Step(".*read the line '(.*)' from (?:the )?" + HandlerPatterns.processNamePattern + " process std error")
    public void readLineFromProcessStdError(String pattern, String processName) {
        processManager.readFromProcessStdError(pattern, processName, false);
    }

    @Step(".*read the line '(.*)' from (?:the )?" + HandlerPatterns.processNamePattern + " process within (\\d+) second(?:s)?")
    public void readLineFromProcessWithinNSeconds(String pattern, String processName, int seconds) {
        processManager.readFromProcessWithinNSeconds(pattern, processName, false, seconds);
    }

    @Step(".*read the line '(.*)' from (?:the )?" + HandlerPatterns.processNamePattern + " process std error within (\\d+) second(?:s)?")
    public void readLineFromProcessStdErrorWithinNSeconds(String pattern, String processName, int seconds) {
        processManager.readFromProcessStdErrorWithinNSeconds(pattern, processName, false, seconds);
    }

    @Step(".*read '(.*)' from (?:the )?" + HandlerPatterns.processNamePattern + " process")
    public void readFromProcess(String pattern, String processName) {
        processManager.readFromProcess(pattern, processName, true);
    }

    @Step(".*read '(.*)' from (?:the )?" + HandlerPatterns.processNamePattern + " process std error")
    public void readFromProcessStdError(String pattern, String processName) {
        processManager.readFromProcessStdError(pattern, processName, true);
    }

    @Step(".*read '(.*)' from (?:the )?" + HandlerPatterns.processNamePattern + " process within (\\d+) second(?:s)?")
    public void readFromProcessWithinNSeconds(String pattern, String processName, int seconds) {
        processManager.readFromProcessWithinNSeconds(pattern, processName, true, seconds);
    }

    @Step(".*read '(.*)' from (?:the )?" + HandlerPatterns.processNamePattern + " process std error within (\\d+) second(?:s)?")
    public void readFromProcessStdErrorWithinNSeconds(String pattern, String processName, int seconds) {
        processManager.readFromProcessStdErrorWithinNSeconds(pattern, processName, true, seconds);
    }

    @Step(".*write the line '(.*)' to (?:the )?" + HandlerPatterns.processNamePattern + " process")
    public void writeLineToProcess(String line, String processName) {
        processManager.writeToProcess(line, processName, true);
    }

    @Step(".*write '(.*)' to (?:the )?" + HandlerPatterns.processNamePattern + " process")
    public void writeToProcess(String line, String processName) {
        processManager.writeToProcess(line, processName, false);
    }

    //A Directive which can be used to start one or more processes using the config name
    @Step("Processes start " + HandlerPatterns.processNameListPattern)
    public void startProcessDirective(String processNameList) throws Exception {
        Map<String,String> processNames = HandlerPatterns.getProcessNamesWithAliases(processNameList);
        for ( Map.Entry<String,String> e : processNames.entrySet()) {
            startNamedProcessFromConfig(e.getValue(), e.getKey());
        }
    }

    //A Directive which can be used to connect to one or more processes using the config name so we an run steps on them
    @Step("Processes connect " + HandlerPatterns.processNameListPattern)
    public void remotingUseDirective(String processNameList) throws Exception {
        List<String> componentNames = HandlerPatterns.getProcessNames(processNameList);
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

        int remotingPort = Integer.parseInt(processProps.getProperty(ProcessesConfigBeanFactory.remotingPort));
        String scope = processProps.getProperty(ProcessesConfigBeanFactory.scope);

        if ( remotingPort == -1) {
            fail("Cannot connect " + processName + " unknown remoting port");
        }
        Properties remotingProps = new Properties();
        remotingProps.put("connection", "jmx:localhost:" + remotingPort);
        remotingProps.put("scope", scope);
        return remotingProps;
    }


}