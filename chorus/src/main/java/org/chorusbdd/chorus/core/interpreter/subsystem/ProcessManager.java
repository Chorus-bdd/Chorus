package org.chorusbdd.chorus.core.interpreter.subsystem;

import org.chorusbdd.chorus.annotations.Scope;
import org.chorusbdd.chorus.executionlistener.ExecutionListener;
import org.chorusbdd.chorus.processes.processmanager.ProcessInfo;

/**
 * Created by nick on 26/09/2014.
 */
public interface ProcessManager {
    void startProcess(ProcessInfo processInfo) throws Exception;

    void stopProcess(String processName);

    void stopProcessesRunningWithinScope(Scope scope);

    void stopAllProcesses();

    ProcessInfo getProcessInfo(String processName);

    void checkProcessHasStopped(String processName);

    void checkProcessIsRunning(String processName);

    void waitForProcessToTerminate(String processName);

    void readFromProcess(String pattern, String processName, boolean searchWithinLines);

    void readFromProcessWithinNSeconds(String pattern, String processName, boolean searchWithinLines, int seconds);

    void readFromProcessStdError(String pattern, String processName, boolean searchWithinLines);

    void readFromProcessStdErrorWithinNSeconds(String pattern, String processName, boolean searchWithinLines, int seconds);

    void writeToProcess(String line, String processName, boolean newLine);

    void waitForProcessToTerminate(String processName, int waitTimeSeconds);

    ExecutionListener getProcessManagerExecutionListener();
}
