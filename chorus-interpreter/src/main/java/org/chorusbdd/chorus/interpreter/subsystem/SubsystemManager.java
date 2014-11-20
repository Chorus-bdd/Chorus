package org.chorusbdd.chorus.interpreter.subsystem;

import org.chorusbdd.chorus.executionlistener.ExecutionListener;
import org.chorusbdd.chorus.subsystem.Subsystem;

import java.util.List;

/**
 * Created by nick on 14/10/2014.
 */
public interface SubsystemManager {

    Object getProcessManager();

    Object getRemotingManager();

    Subsystem getSubsystemById(String id);

    List<ExecutionListener> getExecutionListeners();
}
