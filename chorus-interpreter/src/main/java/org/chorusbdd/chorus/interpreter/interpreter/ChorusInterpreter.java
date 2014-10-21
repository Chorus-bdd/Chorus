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
package org.chorusbdd.chorus.interpreter.interpreter;

import org.chorusbdd.chorus.annotations.Scope;
import org.chorusbdd.chorus.context.ChorusContext;
import org.chorusbdd.chorus.pathscanner.HandlerClassDiscovery;
import org.chorusbdd.chorus.stepinvoker.DefaultStepInvokerProvider;
import org.chorusbdd.chorus.stepinvoker.HandlerClassInvokerFactory;
import org.chorusbdd.chorus.stepinvoker.StepInvoker;
import org.chorusbdd.chorus.stepinvoker.StepInvokerProvider;
import org.chorusbdd.chorus.executionlistener.ExecutionListener;
import org.chorusbdd.chorus.executionlistener.ExecutionListenerSupport;
import org.chorusbdd.chorus.interpreter.subsystem.SubsystemManager;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.parser.KeyWord;
import org.chorusbdd.chorus.results.EndState;
import org.chorusbdd.chorus.results.ExecutionToken;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.results.ScenarioToken;
import org.chorusbdd.chorus.util.NamedExecutors;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by: Steve Neal
 * Date: 29/09/11
 */
@SuppressWarnings("unchecked")
public class ChorusInterpreter {

    private static ChorusLog log = ChorusLogFactory.getLog(ChorusInterpreter.class);

    private static final ScheduledExecutorService timeoutExcecutor = NamedExecutors.newSingleThreadScheduledExecutor("TimeoutExecutor");

    private long scenarioTimeoutMillis = 360000;
    private List<String> basePackages = Collections.emptyList();

    private ExecutionListenerSupport executionListenerSupport = new ExecutionListenerSupport();

    private HandlerClassDiscovery handlerClassDiscovery = new HandlerClassDiscovery();
    private SpringContextSupport springContextSupport = new SpringContextSupport();

    private ScheduledFuture scenarioTimeoutInterrupt;
    private ScheduledFuture scenarioTimeoutStopThread;
    private ScheduledFuture scenarioTimeoutKill;

    private StepProcessor stepProcessor = new StepProcessor(executionListenerSupport);

    private SubsystemManager subsystemManager;

    public ChorusInterpreter() {}

    public void processFeatures(ExecutionToken executionToken, List<FeatureToken> features) throws Exception {

        //load all available handler classes
        HashMap<String, Class> allHandlerClasses = handlerClassDiscovery.discoverHandlerClasses(basePackages);
        
        //RUN EACH FEATURE
        for (FeatureToken feature : features) {
            try {
                processFeature(
                        executionToken,
                        allHandlerClasses,
                        feature
                );
            } catch (Throwable t) {
                log.error("Exception while running feature " + feature, t);
                executionToken.incrementFeaturesFailed();
            }
        }    
    }

    private void processFeature(ExecutionToken executionToken, HashMap<String, Class> allHandlerClasses, FeatureToken feature) throws Exception {

        //notify we started, even if there are missing handlers
        //(but nothing will be done)
        //this is still important so execution listeners at least see the feature (but will show as 'unimplemented')
        String config = feature.isConfiguration() ? " in config " + feature.getConfigurationName() : "";
        log.info("Running feature from file: " + feature.getFeatureFile() + config);
        executionListenerSupport.notifyFeatureStarted(executionToken, feature);

        //check that the required handler classes are all available and list them in order of precidence
        List<Class> orderedHandlerClasses = new ArrayList<Class>();
        StringBuilder unavailableHandlersMessage = handlerClassDiscovery.findHandlerClasses(allHandlerClasses, feature, orderedHandlerClasses);
        boolean foundAllHandlerClasses = unavailableHandlersMessage.length() == 0;

        //run the scenarios in the feature
        if (foundAllHandlerClasses) {
            log.debug("The following handlers will be used " + orderedHandlerClasses);
            runScenarios(executionToken, feature, orderedHandlerClasses);

            String description = feature.getEndState() == EndState.PASSED ? " passed! " : feature.getEndState() == EndState.PENDING ? " pending! " : " failed! ";
            log.trace("The feature " + description);

            if ( feature.getEndState() == EndState.PASSED) {
                executionToken.incrementFeaturesPassed();
            } else if ( feature.getEndState() == EndState.PENDING ) {
                executionToken.incrementFeaturesPending();
            } else {
                executionToken.incrementFeaturesFailed();
            }
        } else {
            log.warn("The following handlers were not available, failing feature " + feature.getName());
            feature.setUnavailableHandlersMessage(unavailableHandlersMessage.toString());
            executionToken.incrementUnavailableHandlers();
            executionToken.incrementFeaturesFailed();
        }

        executionListenerSupport.notifyFeatureCompleted(executionToken, feature);
    }

    private void runScenarios(ExecutionToken executionToken, FeatureToken feature, List<Class> orderedHandlerClasses) throws Exception {
        List<ScenarioToken> scenarios = feature.getScenarios();
        
        HandlerManager handlerManager = new HandlerManager(feature, orderedHandlerClasses, springContextSupport, subsystemManager);
        handlerManager.createFeatureScopedHandlers();
        handlerManager.processStartOfFeature();

        log.debug("Now running scenarios " + scenarios + " for feature " + feature);
        for (Iterator<ScenarioToken> iterator = scenarios.iterator(); iterator.hasNext(); ) {
            ScenarioToken scenario = iterator.next();

            //if the feature start scenario exists and failed we skip all but feature end scenario
            boolean skip = 
                    ! scenario.isFeatureStartScenario() && 
                    ! scenario.isFeatureEndScenario() &&
                    feature.isFeatureStartScenarioFailed();
            
            if ( skip ) {
                log.warn("Skipping scenario " + scenario + " since " + KeyWord.START_FEATURE_SCENARIO_NAME + " failed");
            }
            
            processScenario(
                executionToken,
                handlerManager,
                scenario,
                skip
            );
        }

        handlerManager.processEndOfFeature();
    }

    private void processScenario(ExecutionToken executionToken, HandlerManager handlerManager, ScenarioToken scenario, boolean skip) throws Exception {
        executionListenerSupport.notifyScenarioStarted(executionToken, scenario);
        log.info(String.format("Processing scenario: %s", scenario.getName()));

        //reset the ChorusContext for the scenario
        ChorusContext.destroy();

        handlerManager.setCurrentScenario(scenario);
        List<Object> handlerInstances = handlerManager.getOrCreateHandlersForScenario();
        handlerManager.processStartOfScope(Scope.SCENARIO, handlerInstances);

        createTimeoutTasks(Thread.currentThread()); //will interrupt or eventually kill thread / interpreter if blocked

        log.debug("Running scenario steps for Scenario " + scenario);
        StepInvokerProvider p = createStepInvokerProvider(handlerInstances);
        stepProcessor.runSteps(executionToken, p, scenario.getSteps(), skip);

        stopTimeoutTasks();

        //the special start or end scenarios don't count in the execution stats
        if ( ! scenario.isStartOrEndScenario() ) {
            updateExecutionStats(executionToken, scenario);
        }

        handlerManager.processEndOfScope(Scope.SCENARIO, handlerInstances);
        executionListenerSupport.notifyScenarioCompleted(executionToken, scenario);
    }

    private StepInvokerProvider createStepInvokerProvider(List<Object> handlerInstances) {
        DefaultStepInvokerProvider stepInvokerProvider = new DefaultStepInvokerProvider();
        for ( Object handler : handlerInstances) {
            HandlerClassInvokerFactory f = new HandlerClassInvokerFactory(handler);
            List<StepInvoker> l = f.createStepInvokers();
            stepInvokerProvider.addStepInvokers(l);
        }
        return stepInvokerProvider;
    }

    private void updateExecutionStats(ExecutionToken executionToken, ScenarioToken scenario) {
        if ( scenario.getEndState() == EndState.PASSED ) {
            executionToken.incrementScenariosPassed();
        } else if ( scenario.getEndState() == EndState.PENDING) {
            executionToken.incrementScenariosPending();
        } else {
            executionToken.incrementScenariosFailed();
        }
    }

    private void createTimeoutTasks(final Thread t) {
        scenarioTimeoutInterrupt = timeoutExcecutor.schedule(new Runnable() {
            public void run() {
                timeoutIfStillRunning(t);
            }
        }, scenarioTimeoutMillis, TimeUnit.MILLISECONDS);

        scenarioTimeoutStopThread = timeoutExcecutor.schedule(new Runnable() {
            public void run() {
                stopThreadIfStillRunning(t);
            }
        }, scenarioTimeoutMillis * 2, TimeUnit.MILLISECONDS);

        scenarioTimeoutKill = timeoutExcecutor.schedule(new Runnable() {
            public void run() {
                killInterpreterIfStillRunning(t);
            }
        }, scenarioTimeoutMillis * 3, TimeUnit.MILLISECONDS);
    }

    private void stopTimeoutTasks() {
        scenarioTimeoutInterrupt.cancel(true);
        scenarioTimeoutStopThread.cancel(true);
        scenarioTimeoutKill.cancel(true);
    }

    private void timeoutIfStillRunning(Thread t) {
        if ( t.isAlive()) {
            log.warn("Scenario timed out after " + scenarioTimeoutMillis + " millis, will interrupt");
            stepProcessor.setInterruptingOnTimeout(true);
            t.interrupt(); //first try to interrupt to see if this can unblock/fail the scenario
        }
    }

    private void stopThreadIfStillRunning(Thread t) {
        if ( t.isAlive()) {
            log.error("Scenario did not respond to interrupt after timeout, " +
                    "will stop the interpreter thread and fail the tests");
            t.stop(); //this will trigger a ThreadDeath exception which we should allow to propagate and will terminate the interpreter
        }
    }

    private void killInterpreterIfStillRunning(Thread t) {
        if ( t.isAlive()) {
            log.error("Scenario did not respond to thread.kill() after timeout, will now kill the interpreter");
            System.exit(1);
        }
    }

    public void setBasePackages(List<String> basePackages) {
        this.basePackages = basePackages;
    }

    public void setDryRun(boolean dryRun) {
        this.stepProcessor.setDryRun(dryRun);
    }

    public void addExecutionListener(ExecutionListener... listeners) {
        executionListenerSupport.addExecutionListener(listeners);
    }

    public void addExecutionListeners(List<ExecutionListener> executionListeners) {
        executionListenerSupport.addExecutionListener(executionListeners);
    }

    public boolean removeExecutionListener(ExecutionListener... listeners) {
        return executionListenerSupport.removeExecutionListener(listeners);
    }

    public void setScenarioTimeoutMillis(long scenarioTimeoutMillis) {
        this.scenarioTimeoutMillis = scenarioTimeoutMillis;
    }

    public void setSubsystemManager(SubsystemManager subsystemManager) {
        this.subsystemManager = subsystemManager;
    }
}
