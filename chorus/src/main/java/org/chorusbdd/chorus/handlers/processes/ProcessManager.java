package org.chorusbdd.chorus.handlers.processes;

import org.chorusbdd.chorus.annotations.Scope;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.util.ChorusException;
import org.chorusbdd.chorus.util.NamedExecutors;
import org.chorusbdd.chorus.util.assertion.ChorusAssert;
import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class ProcessManager  {

    static class ProcessInfo {
        final String processName;
        final ProcessesConfig config;
        final ChorusProcess process;

        ProcessInfo(String processName, ProcessesConfig config, ChorusProcess process) {
            this.processName = processName;
            this.config = config;
            this.process = process;
        }

        String getName() {
            return processName;
        }

        ProcessesConfig getConfig() {
            return config;
        }

        ChorusProcess getProcess() {
            return process;
        }
    }

    private static ScheduledExecutorService processesHandlerExecutor = NamedExecutors.newSingleThreadScheduledExecutor("ProcessesHandlerScheduler");

    private static ChorusLog log = ChorusLogFactory.getLog(ProcessesHandler.class);

    private final Map<String, ProcessInfo> processes = new ConcurrentHashMap<String, ProcessInfo>();

    private final CleanupShutdownHook cleanupShutdownHook = new CleanupShutdownHook();

    private ChorusProcessFactory chorusProcessFactory = new ChorusProcessFactory();

    private volatile File featureDir;
    private volatile File featureFile;
    private volatile FeatureToken featureToken;

    private ProcessManager() {
        addShutdownHook();
    }

    private static final ProcessManager INSTANCE = new ProcessManager();

    public static ProcessManager getInstance() {
        return INSTANCE;
    }

    public void setFeatureDetails(final File featureDir, final File featureFile, final FeatureToken featureToken) {
        this.featureDir = featureDir;
        this.featureFile = featureFile;
        this.featureToken = featureToken;
    }

    // ----------------------------------------------------- Start/Stop Process

    /**
     * Starts a record Java process using properties defined in a properties file alongside the feature file
     *
     * @throws Exception
     */
    public synchronized void startJava(ProcessesConfig processesConfig, String processName) throws Exception {
        ChorusAssert.assertFalse("There is already a process with the processName " + processName, processes.containsKey(processName));

        //get the log output containing logging configuration for this process
        ProcessLogOutput logOutput = new ProcessLogOutput(featureToken, featureDir, featureFile, processesConfig, processName);
        String logFileBaseName = logOutput.getLogFileBaseName();

        AbstractCommandLineBuilder b = processesConfig.isJavaProcess() ?
                new JavaProcessCommandLineBuilder(featureDir, processesConfig, logFileBaseName) :
                new NativeProcessCommandLineBuilder(processesConfig, featureDir);

        List<String> commandLineTokens = b.buildCommandLine();
        startProcess(processName, commandLineTokens, logOutput, processesConfig.getProcessCheckDelay(), processesConfig);
    }

    /**
     *  @deprecated Since 1.6.1 the preferred way to launch e a non-java process is to set the record processes handler property 'pathToExecutable'
     */
    @Deprecated
    public synchronized void startScript(ProcessesConfig processesConfig, final String script, final String processName) throws Exception {

        String command = NativeProcessCommandLineBuilder.getPathToExecutable(featureDir, script);

        //get the log output containing logging configuration and out and err streams for this script
        ProcessLogOutput logOutput = new ProcessLogOutput( featureToken, featureDir, featureFile, processesConfig, processName);

        log.debug("About to run script: " + command);
        startProcess(processName, Collections.singletonList(command), logOutput, 250, processesConfig);
    }

    public synchronized void startProcess(String name, List<String> commandLineTokens, ProcessLogOutput logOutput, int processCheckDelay, ProcessesConfig processesConfig) throws Exception {
        log.info("Starting a java process: " + name);
        ChorusAssert.assertFalse("There is already a process with the processName " + name, processes.containsKey(name));
        ChorusProcess child = chorusProcessFactory.createChorusProcess(name, commandLineTokens, logOutput);
        processes.put(name, new ProcessInfo(name, processesConfig, child));
        child.checkProcess(processCheckDelay);
    }

    public synchronized void stopProcess(String processName) {
        ChorusProcess p = processes.get(processName).getProcess();
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

    public synchronized void stopProcessesRunningWithinScope(Scope scope) {
        for (final ProcessInfo pInfo : processes.values()) {
            if (pInfo.getConfig().getProcessScope() == scope ) {
                log.debug("Stopping process named " + pInfo.getName() + " scoped to " + scope);
                try {
                    stopProcess(pInfo.getName());
                } catch (Exception e) {
                    log.warn("Error when stopping process named " + pInfo.getName(), e);
                }
            }
        }
    }

    public synchronized void stopAllProcesses() {
        final Set<String> processNames = new HashSet<String>(processes.keySet());
        for (final String name : processNames) {
            stopProcess(name);
        }
    }

    // --------------------------------------------------------- Process Status

    public synchronized void checkProcessHasStopped(String processName) {
        ProcessInfo p = processes.get(processName);
        if ( p == null ) {
            throw new ChorusException("There is no process named '" + processName + "' to check is stopped");
        }
        ChorusAssert.assertTrue("The process " + processName + " was not stopped", p.getProcess().isStopped());
    }

    public synchronized void checkProcessIsRunning(String processName) {
        ProcessInfo p = processes.get(processName);
        if ( p == null ) {
            throw new ChorusException("There is no process named '" + processName + "' to check is running");
        }
        ChorusAssert.assertTrue("Check the process " + processName + " is running", ! p.getProcess().isStopped());
    }

    public synchronized void waitForProcessToTerminate(String processName) {
        ProcessInfo p = processes.get(processName);
        if ( p == null ) {
            throw new ChorusException("There is no process named '" + processName + "' to wait to terminate");
        }
        ProcessesConfig c = p.getConfig();
        int waitTime = c.getTerminateWaitTime();
        waitForProcessToTerminate(processName, waitTime);
    }

    // ---------------------------------------------------------- Process Comms

    public synchronized void readFromProcess(String pattern, String processName, boolean searchWithinLines) {
        ChorusProcess p = getAndCheckProcessByName(processName);
        p.waitForMatchInStdOut(pattern, searchWithinLines);
    }

    public synchronized void readFromProcessWithinNSeconds(String pattern, String processName, boolean searchWithinLines, int seconds) {
        ChorusProcess p = getAndCheckProcessByName(processName);
        p.waitForMatchInStdOut(pattern, searchWithinLines, TimeUnit.SECONDS, seconds);
    }

    public synchronized void readFromProcessStdError(String pattern, String processName, boolean searchWithinLines) {
        ChorusProcess p = getAndCheckProcessByName(processName);
        p.waitForMatchInStdErr(pattern, true);
    }

    public synchronized void readFromProcessStdErrorWithinNSeconds(String pattern, String processName, boolean searchWithinLines, int seconds) {
        ChorusProcess p = getAndCheckProcessByName(processName);
        p.waitForMatchInStdErr(pattern, true, TimeUnit.SECONDS, seconds);
    }

    public synchronized void writeToProcess(String line, String processName, boolean newLine) {
        ChorusProcess p = getAndCheckProcessByName(processName);
        p.writeToStdIn(line, newLine);
    }

    // -------------------------------------------------------- Private Methods


    private synchronized ChorusProcess getAndCheckProcessByName(String processName) {
        ProcessInfo p = processes.get(processName);
        if ( p == null ) {
            ChorusAssert.fail("Could not find the process " + processName);
        }
        return p.getProcess();
    }

    public synchronized void waitForProcessToTerminate(String processName, int waitTimeSeconds) {
        ProcessInfo p = processes.get(processName);
        if ( p == null ) {
            throw new ChorusException("There is no process named '" + processName + "' to wait for");
        }

        InterruptWaitTask t = new InterruptWaitTask(Thread.currentThread(), processName);
        processesHandlerExecutor.schedule(t, waitTimeSeconds, TimeUnit.SECONDS);

        try {
            p.getProcess().waitFor();
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

    private void addShutdownHook() {
        log.trace("Adding shutdown hook for ProcessHandler " + this);
        Runtime.getRuntime().addShutdownHook(cleanupShutdownHook);
    }

    /**
     * If shut down before a scenario completes, try as hard as we can to
     * stop any processes under test which Chorus started, since these do not appear
     * to die automatically with the parent in all environments
     */
    private class CleanupShutdownHook extends Thread {
        public void run() {
            log.debug("Running Cleanup on shutdown for ProcessHandler " + this);
            stopAllProcesses();
        }
    }
}