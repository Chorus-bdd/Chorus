/**
 *  Copyright (C) 2000-2015 The Software Conservancy and Original Authors.
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
package org.chorusbdd.chorus.interpreter.subsystem;

import org.chorusbdd.chorus.executionlistener.ExecutionListener;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.stepinvoker.StepInvokerProvider;
import org.chorusbdd.chorus.subsystem.Subsystem;
import org.chorusbdd.chorus.util.ChorusException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Some of Chorus' subsystems are pluggable, we depend only on the abstractions
 *
 * Created by nick on 26/09/2014.
 *
 */
public class SubsystemManagerImpl implements SubsystemManager {

    private final List<Subsystem> subsystemList;
    private final List<StepInvokerProvider> stepProviderSubsystems;

    //Use Linked map to maintain ordering for consistent behaviour
    private Map<String, Subsystem> subsystems = new LinkedHashMap<>();

    private ChorusLog log = ChorusLogFactory.getLog(SubsystemManagerImpl.class);

    public SubsystemManagerImpl() {

        //initialize subsystems in order of priority
        initializeProcessManager();
        initializeRemotingManager();
        initializeConfigurationManager();
        initializeStepServerManager();

        subsystemList = Collections.unmodifiableList(new ArrayList<>(subsystems.values()));

        this.stepProviderSubsystems = setInvokerProviderSubsystems();
    }

    private List<StepInvokerProvider> setInvokerProviderSubsystems() {
        List<StepInvokerProvider> stepInvokerSubsystemList = new LinkedList<>();
        for ( Subsystem s : subsystemList) {
            if ( StepInvokerProvider.class.isAssignableFrom(s.getClass())) {
                log.debug("Adding Subsystem " + s.getClass().getName() + " as a Step provider");
                stepInvokerSubsystemList.add((StepInvokerProvider)s);
            }
        }
        return Collections.unmodifiableList(stepInvokerSubsystemList);
    }

    public Subsystem getSubsystemById(String id) {
        return subsystems.get(id);
    }

    @Override
    public List<StepInvokerProvider> getStepProviderSubsystems() {
        return stepProviderSubsystems;
    }

    public List<ExecutionListener> getExecutionListeners() {
        return subsystemList.stream().map(Subsystem::getExecutionListener).collect(Collectors.toList());
    }

    private void initializeProcessManager() {
        initializeSubsystem(
            "processManager",
            "chorusProcessManager",
            "org.chorusbdd.chorus.processes.manager.ProcessManagerImpl",
            false
        );

    }

    private void initializeRemotingManager() {
        initializeSubsystem(
            "remotingManager",
            "chorusRemotingManager",
            "org.chorusbdd.chorus.remoting.ProtocolAwareRemotingManager",
            false
        );
    }

    private void initializeConfigurationManager() {
        initializeSubsystem(
            "configurationManager",
            "chorusConfigurationManager",
            "org.chorusbdd.chorus.handlerconfig.ChorusProperties",
            false
        );
    }

    private void initializeStepServerManager() {
        initializeSubsystem(
            "stepServerManager",
            "chorusStepServerManager",
            "org.chorusbdd.chorus.stepserver.StepServer",
            true
        );
    }

    private void initializeSubsystem(String subsystemId, String sysProp, String defaultImplementingClass, boolean optional) {
        String processManagerClass = System.getProperty(sysProp, defaultImplementingClass);
        log.debug("Implementation for " + subsystemId + " is " + processManagerClass);
        Optional<Subsystem> instance = createSubsystem(subsystemId, optional, processManagerClass);
        instance.ifPresent(s -> subsystems.put(subsystemId, s));
    }

    private Optional<Subsystem> createSubsystem(String subsystemId, boolean optional, String processManagerClass) {
        Optional<Subsystem> instance = Optional.empty();
        try {
            Class clazz = Class.forName(processManagerClass);
            instance = Optional.of((Subsystem)clazz.newInstance());
        } catch (Exception e) {
            if ( ! optional ) {
                log.error("Failed to initialize  " + subsystemId +
                    " is class " + processManagerClass + " in the classpath, " +
                    "does it have a nullary constructor?", e);
                throw new ChorusException("Failed to initialize " + subsystemId, e);
            } else {
                log.trace("Did not create subsystem " + subsystemId + " because an instance of " + processManagerClass + " could not be instantiated", e);
            }
        }
        return instance;
    }
}
