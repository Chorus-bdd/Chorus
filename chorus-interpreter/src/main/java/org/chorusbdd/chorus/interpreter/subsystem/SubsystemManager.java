package org.chorusbdd.chorus.interpreter.subsystem;

import org.chorusbdd.chorus.executionlistener.ExecutionListener;

import java.util.List;

/**
 * Created by nick on 14/10/2014.
 */
public interface SubsystemManager {

    Object getProcessManager();

    Object getRemotingManager();

    List<ExecutionListener> getExecutionListeners();
}
