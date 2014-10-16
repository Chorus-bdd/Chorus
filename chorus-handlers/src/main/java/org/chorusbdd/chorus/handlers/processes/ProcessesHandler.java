/**
 *  Copyright (C) 2000-2014 The Software Conservancy and Original Authors.
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

import org.chorusbdd.chorus.annotations.*;
import org.chorusbdd.chorus.processes.manager.ProcessManager;
import org.chorusbdd.chorus.processes.manager.ProcessManagerConfig;
import org.chorusbdd.chorus.handlerconfig.ConfigurableHandler;
import org.chorusbdd.chorus.handlerconfig.PropertiesFileAndDbConfigLoader;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.util.ChorusException;

import java.io.File;
import java.util.Map;

/**  A handler for starting, stopping and communicating with processes */
@Handler(value = "Processes", scope= Scope.FEATURE)
@SuppressWarnings("UnusedDeclaration")
public class ProcessesHandler implements ConfigurableHandler<ProcessesConfig>{

    private static ChorusLog log = ChorusLogFactory.getLog(ProcessesHandler.class);
    
    private Map<String, ProcessesConfig> processConfigTemplates;

    @ChorusResource("feature.dir")
    private File featureDir;

    @ChorusResource("feature.file")
    private File featureFile;

    @ChorusResource("feature.token")
    private FeatureToken featureToken;

    @ChorusResource("subsystem.processes")
    private ProcessManager processManager;

    // -------------------------------------------------------------- Steps

    @Step(".*start a (.*) process")
    public void startProcessFromConfig(String configName) throws Exception {
        //no user process name was supplied so use the config name,
        //or the config name with a count appended if this config has already been started during the scenario
        ProcessesConfig config = getConfig(configName);
        String processName = nextProcessName(configName, config);
        startProcess(config, processName);
    }

    //for first process just use the config processName with no suffix
    //this is so if we just start a single myconfig process, it will be called myconfig
    //the second will be called myconfig-2
    private synchronized String nextProcessName(String processName, ProcessesConfig config) {
        int instancesStarted = config.getInstancesStarted();
        return instancesStarted == 0 ? processName : String.format("%s-%d", processName, instancesStarted + 1);
    }

    @Step(".*start an? (.+) process named ([a-zA-Z0-9-_]*).*?")
    public void startNamedProcessFromConfig(String configName, String processName) throws Exception {
        ProcessesConfig config = getConfig(configName);
        startProcess(config, processName);
    }

    private void startProcess(ProcessesConfig config, String processName) throws Exception {
        ProcessManagerConfig processInfo = config.buildProcessManagerConfig();
        processManager.startProcess(processName, processInfo);
    }

    @Step(".*stop (?:the )?process (?:named )?([a-zA-Z0-9-_]+).*?")
    public void stopProcess(String processName) {
        processManager.stopProcess(processName);
    }

    @Step(".*?(?:the process )?(?:named )?([a-zA-Z0-9-_]+) (?:is |has )(?:stopped|terminated).*?")
    public void checkProcessHasStopped(String processName) {
        processManager.checkProcessHasStopped(processName);
    }

    @Step(".*?(?:the process )?(?:named )?([a-zA-Z0-9-_]+) is running")
    public void checkProcessIsRunning(String processName) {
        processManager.checkProcessIsRunning(processName);
    }

    @Step(".*wait for (?:up to )?(\\d+) seconds for (?:the process )?(?:named )?([a-zA-Z0-9-_]+) to (?:stop|terminate).*?")
    public void waitXSecondsForProcessToTerminate(int waitSeconds, String processName) {
        processManager.waitForProcessToTerminate(processName, waitSeconds);
    }

    @Step(".*wait for (?:the process )?(?:named )?([a-zA-Z0-9-_]*) to (?:stop|terminate).*?")
    public void waitForProcessToTerminate(String processName) {
        processManager.waitForProcessToTerminate(processName);
    }

    @Step(".*read the line '(.*)' from (?:the )?([a-zA-Z0-9-_]*) process")
    public void readLineFromProcess(String pattern, String processName) {
        processManager.readFromProcess(pattern, processName, false);
    }

    @Step(".*read the line '(.*)' from (?:the )?([a-zA-Z0-9-_]*) process std error")
    public void readLineFromProcessStdError(String pattern, String processName) {
        processManager.readFromProcessStdError(pattern, processName, false);
    }

    @Step(".*read the line '(.*)' from (?:the )?([a-zA-Z0-9-_]*) process within (\\d+) second(?:s)?")
    public void readLineFromProcessWithinNSeconds(String pattern, String processName, int seconds) {
        processManager.readFromProcessWithinNSeconds(pattern, processName, false, seconds);
    }

    @Step(".*read the line '(.*)' from (?:the )?([a-zA-Z0-9-_]*) process std error within (\\d+) second(?:s)?")
    public void readLineFromProcessStdErrorWithinNSeconds(String pattern, String processName, int seconds) {
        processManager.readFromProcessStdErrorWithinNSeconds(pattern, processName, false, seconds);
    }

    @Step(".*read '(.*)' from (?:the )?([a-zA-Z0-9-_]*) process")
    public void readFromProcess(String pattern, String processName) {
        processManager.readFromProcess(pattern, processName, true);
    }

    @Step(".*read '(.*)' from (?:the )?([a-zA-Z0-9-_]*) process std error")
    public void readFromProcessStdError(String pattern, String processName) {
        processManager.readFromProcessStdError(pattern, processName, true);
    }

    @Step(".*read '(.*)' from (?:the )?([a-zA-Z0-9-_]*) process within (\\d+) second(?:s)?")
    public void readFromProcessWithinNSeconds(String pattern, String processName, int seconds) {
        processManager.readFromProcessWithinNSeconds(pattern, processName, true, seconds);
    }

    @Step(".*read '(.*)' from (?:the )?([a-zA-Z0-9-_]*) process std error within (\\d+) second(?:s)?")
    public void readFromProcessStdErrorWithinNSeconds(String pattern, String processName, int seconds) {
        processManager.readFromProcessStdErrorWithinNSeconds(pattern, processName, true, seconds);
    }

    @Step(".*write the line '(.*)' to (?:the )?([a-zA-Z0-9-_]*) process")
    public void writeLineToProcess(String line, String processName) {
        processManager.writeToProcess(line, processName, true);
    }

    @Step(".*write '(.*)' to (?:the )?([a-zA-Z0-9-_]*) process")
    public void writeToProcess(String line, String processName) {
        processManager.writeToProcess(line, processName, false);
    }


    ////////////////////////////////////////////////////////////////
    // Lifecycle events 


    @Initialize(scope= Scope.FEATURE)
    public void setup() {
        processConfigTemplates = loadProcessConfig();
    }

    @Destroy(scope= Scope.SCENARIO)
    //by default stop any processes which were started during a scenario
    public void destroyScenario() {
        processManager.stopProcessesRunningWithinScope(Scope.SCENARIO);
        resetScenarioScopedPortsInConfigTemplates();
    }

    private void resetScenarioScopedPortsInConfigTemplates() {
        for (ProcessesConfig config : processConfigTemplates.values()) {
            if (Scope.SCENARIO.equals(config.getProcessScope())) {
                config.resetPorts();
            }
        }
    }

    @Destroy(scope= Scope.FEATURE)
    public void destroyFeature() {
        processManager.stopAllProcesses();
    }


    ///////////////////////////////////////////////////////////////
    // Config load

    private ProcessesConfig getConfig(final String configName) {
        ProcessesConfig c = processConfigTemplates.get(configName);
        if (c == null) {
            throw new ChorusException("No configuration found for process: " + configName);
        }
        return c;
    }

    private Map<String, ProcessesConfig> loadProcessConfig() {
        PropertiesFileAndDbConfigLoader<ProcessesConfig> l = new PropertiesFileAndDbConfigLoader<ProcessesConfig>(
            new ProcessesConfigFactory(),
            "Processes",
            "processes",
            featureToken
        );
        return l.loadConfigs();
    }

    public void addConfiguration(ProcessesConfig handlerConfig) {
        processConfigTemplates.put(handlerConfig.getConfigName(), handlerConfig);
    }
}