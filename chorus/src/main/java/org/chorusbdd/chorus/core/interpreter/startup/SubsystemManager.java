package org.chorusbdd.chorus.core.interpreter.startup;

import org.chorusbdd.chorus.core.interpreter.subsystem.processes.ProcessManager;
import org.chorusbdd.chorus.core.interpreter.subsystem.remoting.RemotingManager;
import org.chorusbdd.chorus.executionlistener.ExecutionListener;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.util.ChorusException;

import java.util.Collections;
import java.util.List;

/**
 * Some of Chorus' subsystems are pluggable, we depend only on the abstractions
 *
 * Created by nick on 26/09/2014.
 */
public class SubsystemManager {

    private final ProcessManager processManager;
    private final RemotingManager remotingManager;

    private ChorusLog log = ChorusLogFactory.getLog(SubsystemManager.class);

    public SubsystemManager() {
        processManager = initializeProcessManager();
        remotingManager = initializeRemotingManager();
    }

    public ProcessManager getProcessManager() {
        return processManager;
    }

    public RemotingManager getRemotingManager() {
        return remotingManager;
    }

    public List<ExecutionListener> getExecutionListeners() {
        return Collections.singletonList(processManager.getProcessManagerExecutionListener());
    }

    private ProcessManager initializeProcessManager() {
        return initializeSubsystem(
                "ProcessManager",
                "chorusProcessManager",
                "org.chorusbdd.chorus.processes.processmanager.ProcessManagerImpl"
        );

    }

    private RemotingManager initializeRemotingManager() {
        return initializeSubsystem(
            "RemotingManager",
            "chorusRemotingManager",
            "org.chorusbdd.chorus.remoting.jmx.remotingmanager.JmxRemotingManager"
        );
    }

    private <E> E initializeSubsystem(String description, String chorusProperty, String defaultImplementingClass) {
        String processManagerClass = System.getProperty(chorusProperty, defaultImplementingClass);
        log.debug("Implementation for " + description + " is " + processManagerClass);
        E instance = null;
        try {
            Class clazz = Class.forName(processManagerClass);
            instance = (E)clazz.newInstance();
        } catch (Exception e) {
            log.error("Failed to initialize  " + description +
                    " is class " + processManagerClass + " in the classpath, " +
                    "does it have a nullary constructor?", e);
            throw new ChorusException("Failed to initialize " + description, e);
        }
        return instance;
    }
}
