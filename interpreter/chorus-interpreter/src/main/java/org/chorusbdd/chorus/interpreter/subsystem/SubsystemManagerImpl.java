/**
 * MIT License
 *
 * Copyright (c) 2019 Chorus BDD Organisation.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.chorusbdd.chorus.interpreter.subsystem;

import org.chorusbdd.chorus.executionlistener.ExecutionListener;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.stepinvoker.StepInvokerProvider;
import org.chorusbdd.chorus.subsystem.Subsystem;
import org.chorusbdd.chorus.subsystem.SubsystemDiscovery;
import org.chorusbdd.chorus.util.ChorusException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 
 * Chorus uses subsystems to support various aspects of handler functionality
 * 
 * See {@link org.chorusbdd.chorus.annotations.SubsystemConfig} for a more complete description of the capabilities
 * 
 * Some of Chorus' subsystems are pluggable, we depend only on the interface and a user can override this 
 * with their own implementation by setting a system property
 * 
 * Subsystems can also be created by a user t
 *
 * Created by nick on 26/09/2014.
 *
 */
public class SubsystemManagerImpl implements SubsystemManager {

    private List<Subsystem> subsystemList = Collections.emptyList();
    private List<StepInvokerProvider> stepProviderSubsystems = Collections.emptyList();

    //Use Linked map to maintain ordering for consistent behaviour
    private Map<String, Subsystem> subsystems = new LinkedHashMap<>();

    private ChorusLog log = ChorusLogFactory.getLog(SubsystemManagerImpl.class);
    
    private SubsystemDiscovery subsystemDiscovery = new SubsystemDiscovery();

    @Override
    public void initializeSubsystems(List<String> handlerClassBasePackages) {
        Map<String, Class> subsystemsDiscovered = subsystemDiscovery.discoverSubsystems(handlerClassBasePackages);
        log.trace("Subsystems discovered:");
        log.trace(subsystemsDiscovered);
        
        subsystemsDiscovered.forEach(this::initializeSubsystem);
        
        subsystemList = Collections.unmodifiableList(new ArrayList<>(subsystems.values()));

        this.stepProviderSubsystems = getStepInvokerProviderSubsystems();
    }

    private List<StepInvokerProvider> getStepInvokerProviderSubsystems() {
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

    private void initializeSubsystem(String subsystemId, Class subsystemClass) {
        log.debug("Initializing subsystem " + subsystemId + " using class [" + subsystemClass.getName() + "]");
        Optional<Subsystem> instance = createSubsystem(subsystemId, subsystemClass);
        instance.ifPresent(s -> subsystems.put(subsystemId, s));
    }

    private Optional<Subsystem> createSubsystem(String subsystemId, Class subsystemClass) {
        Optional<Subsystem> instance = Optional.empty();
        try {
            instance = Optional.of((Subsystem)subsystemClass.newInstance());
        } catch (Exception e) {
            log.warn("Could not create subsystem " + subsystemId + " because an instance of " +
                    "[" + subsystemClass.getName() + "] could not be instantiated", e);
        }
        return instance;
    }
}
