package org.chorusbdd.chorus.core.interpreter.interpreter;

import org.chorusbdd.chorus.core.interpreter.subsystem.processes.ProcessManager;
import org.chorusbdd.chorus.core.interpreter.subsystem.remoting.RemotingManager;
import org.chorusbdd.chorus.executionlistener.ExecutionListener;

import java.util.List;

/**
 * Created by nick on 14/10/2014.
 */
public interface SubsystemManager {
    ProcessManager getProcessManager();

    RemotingManager getRemotingManager();

    List<ExecutionListener> getExecutionListeners();
}
