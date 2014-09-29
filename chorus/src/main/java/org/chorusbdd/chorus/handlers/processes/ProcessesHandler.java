/**
 *  Copyright (C) 2000-2013 The Software Conservancy and Original Authors.
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
import org.chorusbdd.chorus.core.interpreter.subsystem.processes.OutputMode;
import org.chorusbdd.chorus.core.interpreter.subsystem.processes.ProcessManager;
import org.chorusbdd.chorus.core.interpreter.subsystem.processes.ProcessManagerConfig;
import org.chorusbdd.chorus.handlerconfig.loader.PropertiesConfigLoader;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.processes.processmanager.NativeProcessCommandLineBuilder;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.util.ChorusException;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**  A handler for starting, stopping and communicating with processes */
@Handler(value = "Processes", scope= Scope.FEATURE)
@SuppressWarnings("UnusedDeclaration")
public class ProcessesHandler {

    private static ChorusLog log = ChorusLogFactory.getLog(ProcessesHandler.class);
    private Map<String, ProcessesConfig> processConfigTemplates;

    @ChorusResource("feature.dir")
    private File featureDir;

    @ChorusResource("feature.file")
    private File featureFile;

    @ChorusResource("feature.token")
    private FeatureToken featureToken;

    @ChorusResource("process.manager")
    private ProcessManager processManager;

    @Initialize(scope= Scope.FEATURE)
    public void setup() {
        processConfigTemplates = loadProcessConfig();
    }

    @Destroy(scope= Scope.SCENARIO)
    //by default stop any processes which were started during a scenario
    public void destroyScenario() {
        processManager.stopProcessesRunningWithinScope(Scope.SCENARIO);
        resetAutoGeneratedProcessNames();
        resetScenarioScopedPortsInConfigTemplates();
    }

    @Destroy(scope= Scope.FEATURE)
    public void destroyFeature() {
        processManager.stopAllProcesses();
    }

    private void resetAutoGeneratedProcessNames() {
        processCounters.clear();  //the automatic counters for process names are scenario scoped and reset for backwards compatibility
    }

    private ProcessesConfig getConfigTemplate(final String configName) {
        ProcessesConfig c = processConfigTemplates.get(configName);
        if (c == null) {
            throw new ChorusException("No configuration found for process: " + configName);
        }
        c.setProcessConfigName(configName);
        return c;
    }

    private Map<String, ProcessesConfig> loadProcessConfig() {
        PropertiesConfigLoader<ProcessesConfig> l = new PropertiesConfigLoader<ProcessesConfig>(
                new ProcessesConfigFactory(),
                "Processes",
                "processes",
                featureToken,
                featureDir,
                featureFile
        );
        return l.loadConfigs();
    }

    private String nextProcessName() {
        return nextProcessName("process");
    }

    private final HashMap<String, AtomicLong> processCounters = new HashMap<String, AtomicLong>();

    private synchronized String nextProcessName(String prefix) {
        AtomicLong counter = processCounters.get(prefix);
        if ( counter == null) {
            counter = new AtomicLong();
            processCounters.put(prefix, counter);
        }
        int count = (int)counter.incrementAndGet();
        //for first process just use the config processName with no suffix
        //this is so if we just start a single myconfig process, it will be called myconfig
        //the second will be called myconfig-2
        return count == 1 ? prefix : String.format("%s-%d", prefix, count);
    }

    private void resetScenarioScopedPortsInConfigTemplates() {
        for (ProcessesConfig config : processConfigTemplates.values()) {
            if (Scope.SCENARIO.equals(config.getProcessScope())) {
                config.resetInstancesStarted();
            }
        }
    }

    // -------------------------------------------------------------- Steps

    /**
     * Starts a record Java processName using properties defined in a properties file alongside the feature file
     *
     * @throws Exception
     */
    @Step(".*start a (.*) process")
    public void startJava(String processName) throws Exception {
        String uniqueName = nextProcessName(processName);
        ProcessesConfig config = getConfigTemplate(processName);
        ProcessManagerConfig processInfo = config.buildProcessConfig();
        processManager.startProcess(uniqueName, processInfo);
    }

    /**
     * Starts a record Java processName using properties defined in a properties file alongside the feature file
     *
     * @throws Exception
     */
    @Step(".*start an? (.+) process named ([a-zA-Z0-9-_]*).*?")
    public void startJavaProcessFromConfigNamed(String configName, String processName) throws Exception {
        ProcessesConfig config = getConfigTemplate(configName);
        ProcessManagerConfig processInfo = config.buildProcessConfig();
        processManager.startProcess(processName, processInfo);
    }


    @Step(".*start a process using script '(.*)'$")
    public void startScript(final String script) throws Exception {
        startTheScript(script, false);
    }

    @Step(".*start a process using script '(.*)' named (.*)$")
    public void startScript(String script, String processName) throws Exception {
        startTheScript(script, processName, false);
    }

    @Step(".*start a process using script '(.*)' with logging$")
    public void startScriptWithLogging(String script) throws Exception {
        startTheScript(script, true);
    }

    @Step(".*start a process using script '(.*)' named (.*) with logging$")
    public void startScriptWithLogging(String script, String processName) throws Exception {
        startTheScript(script, processName, true);
    }

    private void startTheScript(String script, boolean logging) throws Exception {
        startTheScript(script, nextProcessName(), logging);
    }

    private void startTheScript(String script, String processName, boolean logging) throws Exception {
        final ProcessesConfig config = getScriptConfig(processName, logging, script);
        ProcessManagerConfig processInfo = config.buildProcessConfig();
        processManager.startProcess(processName, processInfo);
    }

    // We have a small problem since we lack a ProcessesConfig when running processes this way
    // So presently we have to mock up a ProcessesConfig
    // Recommended solution is to use the process handler property pathToExecutable instead and then you can set all
    // the other config properties appropriately
    private ProcessesConfig getScriptConfig(final String groupName, final boolean logging, final String script) {
        return new ProcessesConfig() {
            @Override
            public String getConfigName() {
                return groupName;
            }

            @Override
            public OutputMode getStdErrMode() {
                return logging ? OutputMode.FILE : OutputMode.INLINE;
            }

            @Override
            public OutputMode getStdOutMode() {
                return logging ? OutputMode.FILE : OutputMode.INLINE;
            }

            @Override
            public int getProcessCheckDelay() { return 250; }

            @Override
            public String getPathToExecutable() {
                return NativeProcessCommandLineBuilder.getPathToExecutable(featureDir, script);
            }
        };
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
}