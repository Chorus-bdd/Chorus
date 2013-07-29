/**
 *  Copyright (C) 2000-2012 The Software Conservancy and Original Authors.
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
package org.chorusbdd.chorus.core.interpreter;

import org.chorusbdd.chorus.annotations.ChorusResource;
import org.chorusbdd.chorus.annotations.Destroy;
import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.HandlerScope;
import org.chorusbdd.chorus.executionlistener.ExecutionListener;
import org.chorusbdd.chorus.executionlistener.ExecutionListenerSupport;
import org.chorusbdd.chorus.results.EndState;
import org.chorusbdd.chorus.results.ExecutionToken;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.results.ScenarioToken;
import org.chorusbdd.chorus.util.NamedExecutors;
import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
    private String[] basePackages = new String[0];

    private ExecutionListenerSupport executionListenerSupport = new ExecutionListenerSupport();

    private HandlerClassDiscovery handlerClassDiscovery = new HandlerClassDiscovery();
    private SpringContextSupport springContextSupport = new SpringContextSupport();

    private ScheduledFuture scenarioTimeoutInterrupt;
    private ScheduledFuture scenarioTimeoutStopThread;
    private ScheduledFuture scenarioTimeoutKill;

    private StepProcessor stepProcessor = new StepProcessor(executionListenerSupport);

    public ChorusInterpreter() {}

    public void processFeatures(ExecutionToken executionToken, List<FeatureToken> features) throws Exception {

        //load all available handler classes
        HashMap<String, Class> allHandlerClasses = handlerClassDiscovery.discoverHandlerClasses(basePackages);

        HashMap<Class, Object> unmanagedHandlerInstances = new HashMap<Class, Object>();
        
        //RUN EACH FEATURE
        for (FeatureToken feature : features) {
            try {
                processFeature(
                        executionToken,
                        allHandlerClasses,
                        unmanagedHandlerInstances,
                        feature
                );
            } catch (Throwable t) {
                log.error("Exception while running feature " + feature, t);
                executionToken.incrementFeaturesFailed();
            }
        }    
    }

    private void processFeature(ExecutionToken executionToken, HashMap<String, Class> allHandlerClasses, HashMap<Class, Object> unmanagedHandlerInstances, FeatureToken feature) throws Exception {

        //notify we started, even if there are missing handlers
        //(but nothing will be done)
        //this is still important so execution listeners at least see the feature (but will show as 'unimplemented')
        log.trace("Processing feature " + feature);
        executionListenerSupport.notifyFeatureStarted(executionToken, feature);

        //check that the required handler classes are all available and list them in order of precidence
        List<Class> orderedHandlerClasses = new ArrayList<Class>();
        StringBuilder unavailableHandlersMessage = handlerClassDiscovery.findHandlerClasses(allHandlerClasses, feature, orderedHandlerClasses);
        boolean foundAllHandlerClasses = unavailableHandlersMessage.length() == 0;

        //run the scenarios in the feature
        if (foundAllHandlerClasses) {
            log.debug("The following handlers will be used " + orderedHandlerClasses);
            runScenarios(executionToken, unmanagedHandlerInstances, feature, orderedHandlerClasses);

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

    private void runScenarios(ExecutionToken executionToken, HashMap<Class, Object> unmanagedHandlerInstances, FeatureToken feature, List<Class> orderedHandlerClasses) throws Exception {
        //this will contain the handlers for the feature file (scenario scopes ones will be replaced for each scenario)
        List<Object> handlerInstances = new ArrayList<Object>();
        //FOR EACH SCENARIO
        List<ScenarioToken> scenarios = feature.getScenarios();

        log.debug("Now running scenarios " + scenarios + " for feature " + feature);
        for (Iterator<ScenarioToken> iterator = scenarios.iterator(); iterator.hasNext(); ) {
            ScenarioToken scenario = iterator.next();
            boolean isLastScenario = !iterator.hasNext();

            processScenario(
                executionToken,
                unmanagedHandlerInstances,
                feature,
                orderedHandlerClasses,
                handlerInstances,
                isLastScenario,
                scenario
            );
        }
    }

    private void processScenario(final ExecutionToken executionToken, HashMap<Class, Object> unmanagedHandlerInstances, FeatureToken feature, List<Class> orderedHandlerClasses, final List<Object> handlerInstances, boolean isLastScenario, final ScenarioToken scenario) throws Exception {
        executionListenerSupport.notifyScenarioStarted(executionToken, scenario);

        log.info(String.format("Processing scenario: %s", scenario.getName()));

        //reset the ChorusContext for the scenario
        ChorusContext.destroy();

        addHandlerInstances(unmanagedHandlerInstances, feature, orderedHandlerClasses, handlerInstances);

        createTimeoutTasks(Thread.currentThread()); //will interrupt or eventually kill thread / interpreter if blocked

        log.debug("Running scenario steps for Scenario " + scenario);
        stepProcessor.runSteps(executionToken, handlerInstances, scenario.getSteps(), false);

        stopTimeoutTasks();

        if ( scenario.getEndState() == EndState.PASSED ) {
            executionToken.incrementScenariosPassed();
        } else if ( scenario.getEndState() == EndState.PENDING) {
            executionToken.incrementScenariosPending();
        } else {
            executionToken.incrementScenariosFailed();
        }

        //CLEAN UP SCENARIO SCOPED HANDLERS
        for (int i = 0; i < handlerInstances.size(); i++) {
            Object handler = handlerInstances.get(i);
            Handler handlerAnnotation = handler.getClass().getAnnotation(Handler.class);
            if (isLastScenario || handlerAnnotation.scope() == HandlerScope.SCENARIO) {
                cleanupHandler(handler);
                log.debug("Cleaned up scenario handler: " + handlerAnnotation.value());
            }
        }

        executionListenerSupport.notifyScenarioCompleted(executionToken, scenario);
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

    private void addHandlerInstances(HashMap<Class, Object> unmanagedHandlerInstances, FeatureToken feature, List<Class> orderedHandlerClasses, List<Object> handlerInstances) throws Exception {
        //CREATE THE HANDLER INSTANCES
        if (handlerInstances.size() == 0) {
            log.debug("Creating handler instances for feature " + feature);
            //first scenario in file, so initialise the handler instances in order of precedence
            for (Class handlerClass : orderedHandlerClasses) {
                //create a new SCENARIO scoped handler
                Handler handlerAnnotation = (Handler) handlerClass.getAnnotation(Handler.class);
                if (handlerAnnotation.scope() == HandlerScope.SCENARIO) {
                    handlerInstances.add(createAndInitHandlerInstance(handlerClass, feature));
                    log.debug("Created new scenario scoped handler: " + handlerAnnotation.value());
                }
                //or (re)use an UNMANAGED scoped handlers
                else if (handlerAnnotation.scope() == HandlerScope.UNMANAGED) {
                    Object handler = unmanagedHandlerInstances.get(handlerClass);
                    if (handler == null) {
                        handler = createAndInitHandlerInstance(handlerClass, feature);
                        unmanagedHandlerInstances.put(handlerClass, handler);
                        log.debug("Created new unmanaged handler: " + handlerAnnotation.value());
                    }
                    handlerInstances.add(createAndInitHandlerInstance(handlerClass, feature));
                }
            }
        } else {
            //replace scenario scoped handlers if not first scenario in feature file
            for (int i = 0; i < handlerInstances.size(); i++) {
                Object handler = handlerInstances.get(i);
                Handler handlerAnnotation = handler.getClass().getAnnotation(Handler.class);
                if (handlerAnnotation.scope() == HandlerScope.SCENARIO) {
                    handlerInstances.remove(i);
                    handlerInstances.add(i, createAndInitHandlerInstance(handler.getClass(), feature));
                    log.debug("Replaced scenario scoped handler: " + handlerAnnotation.value());
                }
            }
        }
    }


    private Object createAndInitHandlerInstance(Class handlerClass, FeatureToken featureToken) throws Exception {
        Object featureInstance = handlerClass.newInstance();
        log.trace("Created handler class " + handlerClass + " instance " + featureInstance);
        springContextSupport.injectSpringResources(featureInstance, featureToken);
        injectInterpreterResources(featureInstance, featureToken);
        return featureInstance;
    }



    private void injectInterpreterResources(Object handler, FeatureToken featureToken) {
        Class<?> featureClass = handler.getClass();
        Field[] fields = featureClass.getDeclaredFields();
        log.trace("Now examining handler fields for ChorusResource annotation " + Arrays.asList(fields));
        for (Field field : fields) {
            ChorusResource a = field.getAnnotation(ChorusResource.class);
            if (a != null) {
                String resourceName = a.value();
                log.debug("Found ChorusResource annotation " + resourceName + " on field " + field);
                field.setAccessible(true);
                Object o = null;
                if ("feature.file".equals(resourceName)) {
                    o = featureToken.getFeatureFile();
                } else if ("feature.dir".equals(resourceName)) {
                    o = featureToken.getFeatureFile().getParentFile();
                } else if ("feature.token".equals(resourceName)) {
                    o = featureToken;
                }
                if (o != null) {
                    try {
                        field.set(handler, o);
                    } catch (IllegalAccessException e) {
                        log.error("Failed to set @ChorusResource (" + resourceName + ") with object of type: " + o.getClass(), e);
                    }
                } else {
                    log.debug("Set field to value " + o);
                }
            }
        }
    }

    private void cleanupHandler(Object handler) throws Exception {
        log.debug("Cleaning Up Handler " + handler);
        springContextSupport.dispose(handler);

        //call any destroy methods on handler instance
        for (Method method : handler.getClass().getMethods()) {
            if (method.getParameterTypes().length == 0) {
                if (method.getAnnotation(Destroy.class) != null) {
                    log.trace("Found Destroy annotation on handler method " + method + " will invoke");
                    try {
                        method.invoke(handler);
                    } catch ( Throwable t) {
                        log.warn("Exception when calling @Destroy method [" + method + "] on handler " + handler.getClass(), t);
                    }
                }
            }
        }
    }

    public void setBasePackages(String[] basePackages) {
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
}
