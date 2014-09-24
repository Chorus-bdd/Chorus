package org.chorusbdd.chorus.processes.processmanager;

import org.chorusbdd.chorus.annotations.Scope;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.util.ChorusException;
import org.chorusbdd.chorus.util.NamedExecutors;
import org.chorusbdd.chorus.util.assertion.ChorusAssert;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class ProcessManager  {

    private static ScheduledExecutorService processesHandlerExecutor = NamedExecutors.newSingleThreadScheduledExecutor("ProcessesHandlerScheduler");

    private static ChorusLog log = ChorusLogFactory.getLog(ProcessManager.class);

    private final Map<String, ProcessInfo> processes = new ConcurrentHashMap<String, ProcessInfo>();

    private final CleanupShutdownHook cleanupShutdownHook = new CleanupShutdownHook();
    private final ProcessManagerConfigValidator processesConfigValidator = new ProcessManagerConfigValidator();

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
    public synchronized void startJava(ProcessInfo processInfo, String processName) throws Exception {
        checkConfigAndNotAlreadyStarted(processInfo, processName);

        //get the log output containing logging configuration for this process
        ProcessLogOutput logOutput = new ProcessLogOutput(featureToken, featureDir, featureFile, processInfo, processName);
        String logFileBaseName = logOutput.getLogFileBaseName();

        AbstractCommandLineBuilder b = processInfo.isJavaProcess() ?
                new JavaProcessCommandLineBuilder(featureDir, processInfo, logFileBaseName) :
                new NativeProcessCommandLineBuilder(processInfo, featureDir);

        List<String> commandLineTokens = b.buildCommandLine();
        startProcess(processName, commandLineTokens, logOutput, processInfo.getProcessCheckDelay(), processInfo);
    }

    private void checkConfigAndNotAlreadyStarted(ProcessInfo processInfo, String processName) {
        ChorusAssert.assertFalse("There is already a process with the processName " + processName, processes.containsKey(processName));
        ChorusAssert.assertTrue("The config for " + processName + " must be valid", processesConfigValidator.checkValid(processInfo));
    }

    /**
     *  @deprecated Since 1.6.1 the preferred way to launch e a non-java process is to set the record processes handler property 'pathToExecutable'
     */
    @Deprecated
    public synchronized void startScript(ProcessInfo processInfo, final String script, final String processName) throws Exception {
        checkConfigAndNotAlreadyStarted(processInfo, processName);

        String command = NativeProcessCommandLineBuilder.getPathToExecutable(featureDir, script);

        //get the log output containing logging configuration and out and err streams for this script
        ProcessLogOutput logOutput = new ProcessLogOutput( featureToken, featureDir, featureFile, processInfo, processName);

        log.debug("About to run script: " + command);
        startProcess(processName, Collections.singletonList(command), logOutput, 250, processInfo);
    }


    private void startProcess(String name, List<String> commandLineTokens, ProcessLogOutput logOutput, int processCheckDelay, ProcessInfo processInfo) throws Exception {
        ChorusAssert.assertFalse("There is already a process with the processName " + name, processes.containsKey(name));
        processes.put(name, processInfo);

        ChorusProcess chorusProcess = chorusProcessFactory.createChorusProcess(name, commandLineTokens, logOutput);
        processInfo.setProcess(chorusProcess);
        chorusProcess.checkProcess(processCheckDelay);
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
            if (pInfo.getProcessScope() == scope ) {
                log.debug("Stopping process named " + pInfo.getProcessName() + " scoped to " + scope);
                try {
                    stopProcess(pInfo.getProcessName());
                } catch (Exception e) {
                    log.warn("Error when stopping process named " + pInfo.getProcessName(), e);
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

    // ----------------------------------------------------- Process Properties

    public synchronized ProcessInfo getProcessInfo(final String processName) {
        final ProcessInfo processInfo = processes.get(processName);
        return processInfo;
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
        int waitTime = p.getTerminateWaitTime();
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