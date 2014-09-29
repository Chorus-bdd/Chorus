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
