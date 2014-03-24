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
import org.chorusbdd.chorus.handlers.util.config.loader.PropertiesConfigLoader;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.util.ChorusException;
import org.chorusbdd.chorus.util.NamedExecutors;
import org.chorusbdd.chorus.util.assertion.ChorusAssert;
import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by: Steve Neal
 * Date: 07/11/11
 */
@Handler(value = "Processes", scope= Scope.FEATURE)
@SuppressWarnings("UnusedDeclaration")
public class ProcessesHandler {

    private static ScheduledExecutorService processesHandlerExecutor = NamedExecutors.newSingleThreadScheduledExecutor("ProcessesHandlerScheduler");

    private static ChorusLog log = ChorusLogFactory.getLog(ProcessesHandler.class);

    @ChorusResource("feature.dir")
    private File featureDir;

    @ChorusResource("feature.file")
    private File featureFile;

    @ChorusResource("feature.token")
    private FeatureToken featureToken;

    private final Map<String, ChorusProcess> processes = new HashMap<String, ChorusProcess>();

    private final HashMap<String, AtomicLong> processCounters = new HashMap<String, AtomicLong>();

    private Map<String, ProcessesConfig> configMap;

    private Map<String, String> processNameToConfigName = new HashMap<String,String>();

    private final CleanupShutdownHook cleanupShutdownHook = new CleanupShutdownHook();
    
    private ChorusProcessFactory chorusProcessFactory = new ChorusProcessFactory();

    public ProcessesHandler() {
        addShutdownHook();
    }

    /**
     * Starts a new Java process using properties defined in a properties file alongside the feature file
     *
     * @throws Exception
     */
    @Step(".*start a (.*) process")
    public void startJava(String process) throws Exception {
        startJavaNamed(process, nextProcessName(process));
    }

    /**
     * Starts a new Java process using properties defined in a properties file alongside the feature file
     *
     * @throws Exception
     */
    @Step(".*start an? (.*) process named ([a-zA-Z0-9-_]*).*?")
    public void startJavaNamed(String configName, String processName) throws Exception {
        ChorusAssert.assertFalse("There is already a process with the name " + processName, processes.containsKey(processName));
        
        processNameToConfigName.put(processName, configName);
        ProcessesConfig processesConfig = getProcessesConfig(configName);

        //get the log output containing logging configuration for this process
        ProcessLogOutput logOutput = new ProcessLogOutput(featureToken, featureDir, featureFile, processesConfig, processName);
        String logFileBaseName = logOutput.getLogFileBaseName();

        AbstractCommandLineBuilder b = processesConfig.isJavaProcess() ? 
                new JavaProcessCommandLineBuilder(featureDir, processesConfig, logFileBaseName) : 
                new NativeProcessCommandLineBuilder(processesConfig, featureDir);
        
        List<String> commandLineTokens = b.buildCommandLine();
        startProcess(processName, commandLineTokens, logOutput, processesConfig.getProcessCheckDelay());
    }

    @Step(".*start a process using script '(.*)'$")
    public void startScript(String script) throws Exception {
        startScript(script, nextProcessName(), false);
    }

    @Step(".*start a process using script '(.*)' named (.*)$")
    public void startScript(String script, String name) throws Exception {
        startScript(script, name, false);
    }

    @Step(".*start a process using script '(.*)' with logging$")
    public void startScriptWithLogging(String script) throws Exception {
        startScript(script, nextProcessName(), true);
    }

    @Step(".*start a process using script '(.*)' named (.*) with logging$")
    public void startScriptWithLogging(String script, String name) throws Exception {
        startScript(script, name, true);
    }

    /**
     *  @deprecated Since 1.6.1 the preferred way to launch e a non-java process is to set the new processes handler property 'pathToExecutable' 
     */
    public void startScript(String script, final String name, final boolean logging) throws Exception {

        String command = NativeProcessCommandLineBuilder.getPathToExecutable(featureDir, script);
        
        // We have a small problem since we lack a ProcessesConfig when running processes this way
        // So presently we have to mock up a ProcessesConfig
        // Recommended solution is to use the process handler property pathToExecutable instead and then you can set all
        // the other config properties appropriately
        ProcessesConfig c = new ProcessesConfig() {
            
            public String getGroupName() {
                return name;
            }
            
            public boolean isLogging() {
                return logging;
            }
        };

        processNameToConfigName.put(name, name);
        configMap.put(name, c);

        //get the log output containing logging configuration and out and err streams for this script
        ProcessLogOutput logOutput = new ProcessLogOutput( featureToken, featureDir, featureFile, c, name);

        log.debug("About to run script: " + command);
        startProcess(name, Collections.singletonList(command), logOutput, 250);
    }

    @Step(".*stop (?:the )?process (?:named )?([a-zA-Z0-9-_]+).*?")
    public void stopProcess(String processName) {
        ChorusProcess p = processes.get(processName);
        if (p != null) {
            try {
                p.destroy();
                log.debug("Stopped process: " + processName);
            } catch (Exception e) {
                log.warn("Failed to destroy process", e);
            } finally {
                processes.remove(processName);
            }
        } else {
            throw new ChorusException("There is no process named '" + processName + "' to stop");
        }
    }

    @Step(".*?(?:the process )?(?:named )?([a-zA-Z0-9-_]+) (?:is |has )(?:stopped|terminated).*?")
    public void checkProcessHasStopped(String processName) {
        ChorusProcess p = processes.get(processName);
        if ( p == null ) {
            throw new ChorusException("There is no process named '" + processName + "' to check is stopped");
        }
        ChorusAssert.assertTrue("The process " + processName + " was not stopped", p.isStopped());
    }

    @Step(".*?(?:the process )?(?:named )?([a-zA-Z0-9-_]+) is running")
    public void checkProcessIsRunning(String processName) {
        ChorusProcess p = processes.get(processName);
        if ( p == null ) {
            throw new ChorusException("There is no process named '" + processName + "' to check is running");
        }
        ChorusAssert.assertTrue("Check the process " + processName + " is running", ! p.isStopped());
    }

    @Step(".*wait for (?:up to )?(\\d+) seconds for (?:the process )?(?:named )?([a-zA-Z0-9-_]+) to (?:stop|terminate).*?")
    public void waitXSecondsForProcessToTerminate(int waitSeconds, String processName) {
        waitForProcessToTerminate(processName, waitSeconds);
    }

    @Step(".*wait for (?:the process )?(?:named )?([a-zA-Z0-9-_]*) to (?:stop|terminate).*?")
    public void waitForProcessToTerminate(String processName) {
        ProcessesConfig c = getConfigForProcessName(processName);
        int waitTime = c.getTerminateWaitTime();
        waitForProcessToTerminate(processName, waitTime);
    }

    private ProcessesConfig getConfigForProcessName(String processName) {
        String configName = getConfigNameForProcess(processName);
        return getProcessesConfig(configName);
    }

    @Step(".*read the line '(.*)' from (?:the )?([a-zA-Z0-9-_]*) process")
    public void readLineFromProcess(String pattern, String processName) {
        ChorusProcess p = getAndCheckProcessByName(processName);
        p.waitForMatchInStdOut(pattern, false);
    }

    @Step(".*read the line '(.*)' from (?:the )?([a-zA-Z0-9-_]*) process std error")
    public void readLineFromProcessStdError(String pattern, String processName) {
        ChorusProcess p = getAndCheckProcessByName(processName);
        p.waitForMatchInStdErr(pattern, false);
    }

    @Step(".*read the line '(.*)' from (?:the )?([a-zA-Z0-9-_]*) process within (\\d+) second(?:s)?")
    public void readLineFromProcessWithinNSeconds(String pattern, String processName, int seconds) {
        ChorusProcess p = getAndCheckProcessByName(processName);
        p.waitForMatchInStdOut(pattern, false, TimeUnit.SECONDS, seconds);
    }

    @Step(".*read the line '(.*)' from (?:the )?([a-zA-Z0-9-_]*) process std error within (\\d+) second(?:s)?")
    public void readLineFromProcessStdErrorWithinNSeconds(String pattern, String processName, int seconds) {
        ChorusProcess p = getAndCheckProcessByName(processName);
        p.waitForMatchInStdErr(pattern, false, TimeUnit.SECONDS, seconds);
    }

    @Step(".*read '(.*)' from (?:the )?([a-zA-Z0-9-_]*) process")
    public void readFromProcess(String pattern, String processName) {
        ChorusProcess p = getAndCheckProcessByName(processName);
        p.waitForMatchInStdOut(pattern, true);
    }

    @Step(".*read '(.*)' from (?:the )?([a-zA-Z0-9-_]*) process std error")
    public void readFromProcessStdError(String pattern, String processName) {
        ChorusProcess p = getAndCheckProcessByName(processName);
        p.waitForMatchInStdErr(pattern, true);
    }

    @Step(".*read '(.*)' from (?:the )?([a-zA-Z0-9-_]*) process within (\\d+) second(?:s)?")
    public void readFromProcessWithinNSeconds(String pattern, String processName, int seconds) {
        ChorusProcess p = getAndCheckProcessByName(processName);
        p.waitForMatchInStdOut(pattern, true, TimeUnit.SECONDS, seconds);
    }

    @Step(".*read '(.*)' from (?:the )?([a-zA-Z0-9-_]*) process std error within (\\d+) second(?:s)?")
    public void readFromProcessStdErrorWithinNSeconds(String pattern, String processName, int seconds) {
        ChorusProcess p = getAndCheckProcessByName(processName);
        p.waitForMatchInStdErr(pattern, true, TimeUnit.SECONDS, seconds);
    }

    @Step(".*write the line '(.*)' to (?:the )?([a-zA-Z0-9-_]*) process") 
    public void writeLineToProcess(String line, String processName) {
        ChorusProcess p = getAndCheckProcessByName(processName);
        p.writeToStdIn(line, true);
    }

    @Step(".*write '(.*)' to (?:the )?([a-zA-Z0-9-_]*) process")
    public void writeToProcess(String line, String processName) {
        ChorusProcess p = getAndCheckProcessByName(processName);
        p.writeToStdIn(line, false);
    }

    private ChorusProcess getAndCheckProcessByName(String processName) {
        ChorusProcess p = processes.get(processName);
        if ( p == null ) {
            ChorusAssert.fail("Could not find the process " + processName);
        }
        return p;
    }    

    private String getConfigNameForProcess(String processName) {
        String configName = processNameToConfigName.get(processName);
        if ( configName == null) {
            throw new ChorusException("Could not find a config name for process " + processName);
        }
        return configName;
    }

    private void waitForProcessToTerminate(String processName, int waitTimeSeconds) {
        ChorusProcess p = processes.get(processName);
        if ( p == null ) {
            throw new ChorusException("There is no process named '" + processName + "' to wait for");
        }

        InterruptWaitTask t = new InterruptWaitTask(Thread.currentThread(), processName);
        processesHandlerExecutor.schedule(t, waitTimeSeconds, TimeUnit.SECONDS);

        try {
            p.waitFor();
        } catch (InterruptedException e) {
            log.warn("Interrupted while waiting for process " + processName + " to terminate");
            throw new ChorusException("Process " + processName + " failed to terminate after " + waitTimeSeconds + " seconds");
        }
        t.setWaitFinished(); //prevent the interrupt, process finished naturally
    }

    class InterruptWaitTask implements Runnable {
       private Thread waitingThread;
       private String processName;
       private volatile boolean isWaitFinished;

       InterruptWaitTask(Thread waitingThread, String processName) {
           this.waitingThread = waitingThread;
           this.processName = processName;
       }

       public void setWaitFinished() {
           isWaitFinished = true;
       }

       public void run() {
           if ( ! isWaitFinished) {
               log.warn("The process named " + processName + " appears not to have terminated, will stop waiting");
               waitingThread.interrupt();
           }
       }
   }

    @Destroy(scope= Scope.SCENARIO)
    //by default stop any processes which were started during a scenario
    public void destroyScenario() {
        destroyProcessesForScope(Scope.SCENARIO);
        processCounters.clear();  //the automatic counters for process names are scenario scoped and reset for backwards compatibility
    }

    @Destroy(scope= Scope.FEATURE)
    public void destroyFeature() {
        Runtime.getRuntime().removeShutdownHook(cleanupShutdownHook);
        destroyProcessesForScope(Scope.FEATURE);
    }

    private void destroyProcessesForScope(Scope scope) {
        Set<String> processNames = new HashSet<String>(processes.keySet());
        for (String name : processNames) {
            ProcessesConfig config = getConfigForProcessName(name);
            if (config.getProcessScope() == scope ) {
                log.debug("Stopping process named " + name + " scoped to " + scope);
                try {
                    stopProcess(name);
                } catch (Exception e) {
                    log.warn("Error when stopping process named " + name, e);
                }
            }
        }
    }

    private void validateMainClass(String mainClassName) {
        Class mainClass;
        try {
            mainClass = Class.forName(mainClassName);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Class not found: " + mainClassName);
        }
        boolean foundMainMethod = false;
        for (Method m : mainClass.getMethods()) {
            if ("main".equals(m.getName())
                    && m.getParameterTypes().length == 1
                    && String[].class.equals(m.getParameterTypes()[0])
                    && void.class.equals(m.getReturnType())
                    && Modifier.isStatic(m.getModifiers())
                    && Modifier.isPublic(m.getModifiers())) {
                foundMainMethod = true;
                break;
            }
        }
        if (!foundMainMethod) {
            throw new IllegalStateException("Cannot run this class, main method not found");
        }
    }

    private String nextProcessName() {
        return nextProcessName("process");
    }

    private synchronized String nextProcessName(String prefix) {
        AtomicLong counter = processCounters.get(prefix);
        if ( counter == null) {
            counter = new AtomicLong();
            processCounters.put(prefix, counter);
        }
        int count = (int)counter.incrementAndGet();
        //for first process just use the config name with no suffix
        //this is so if we just start a single myconfig process, it will be called myconfig
        //the second will be called myconfig-2
        return count == 1 ? prefix : String.format("%s-%d", prefix, count);
    }

    private ChorusProcess startProcess(String name, List<String> commandLineTokens, ProcessLogOutput logOutput, int processCheckDelay) throws Exception {
        ChorusAssert.assertFalse("There is already a process with the name " + name, processes.containsKey(name));
        ChorusProcess child = chorusProcessFactory.createChorusProcess(name, commandLineTokens, logOutput);
        processes.put(name, child);
        child.checkProcess(processCheckDelay);
        return child;
    }

        
    //must be lazy created since featureToken dir and file will not be set on construction
    private ProcessesConfig getProcessesConfig(String processName) {
        if ( configMap == null ) {
            loadProperties();
        }
        ProcessesConfig c = configMap.get(processName);
        if (c == null) {
            throw new RuntimeException("No configuration found for process: " + processName);
        }
        return c;
    }

    private void loadProperties() {
        if ( configMap == null ) {
            PropertiesConfigLoader<ProcessesConfig> l = new PropertiesConfigLoader<ProcessesConfig>(
                    new ProcessesConfigBuilder(),
                    "Processes",
                    "processes",
                    featureToken,
                    featureDir,
                    featureFile
            );
            configMap = l.loadConfigs();
        }
    }

    private void addShutdownHook() {
        log.trace("Adding shutdown hook for ProcessHandler " + this);
        Runtime.getRuntime().addShutdownHook(cleanupShutdownHook);
    }

    private void removeShutdownHook() {
        log.trace("Removing shutdown hook for ProcessHandler " + this);
        Runtime.getRuntime().removeShutdownHook(cleanupShutdownHook);
    }

    /**
     * If shut down before a scenario completes, try as hard as we can to
     * stop any processes under test which Chorus started, since these do not appear
     * to die automatically with the parent in all environments
     */
    private class CleanupShutdownHook extends Thread {
        public void run() {
            log.debug("Running Cleanup on shutdown for ProcessHandler " + this);
            ProcessesHandler.this.destroyScenario();
            ProcessesHandler.this.destroyFeature();
        }
    }
}