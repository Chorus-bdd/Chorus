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

import org.chorusbdd.chorus.annotations.*;
import org.chorusbdd.chorus.core.interpreter.results.*;
import org.chorusbdd.chorus.core.interpreter.tagexpressions.TagExpressionEvaluator;
import org.chorusbdd.chorus.util.ChorusRemotingException;
import org.chorusbdd.chorus.util.ExceptionHandling;
import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by: Steve Neal
 * Date: 29/09/11
 */
@SuppressWarnings("unchecked")
public class ChorusInterpreter {

    private static ChorusLog log = ChorusLogFactory.getLog(ChorusInterpreter.class);

    private boolean dryRun;
    private String[] basePackages = new String[0];
    private String filterExpression;

    private SpringInjector springInjector = SpringInjector.NULL_INJECTOR;
    private ExecutionListenerSupport executionListenerSupport = new ExecutionListenerSupport();

    private HandlerClassDiscovery handlerClassDiscovery = new HandlerClassDiscovery();

    /**
     * Defines the class which will be instantiated to perform injection of Spring context/resources
     */
    private final String SPRING_INJECTOR_CLASSNAME = "org.chorusbdd.chorus.spring.SpringContextInjector";


    /**
     * Used to determine whether a scenario should be run
     */
    private final TagExpressionEvaluator tagExpressionEvaluator = new TagExpressionEvaluator();

    public ChorusInterpreter() {
        try {
            Class c = null;
            try {
                c = Class.forName(SPRING_INJECTOR_CLASSNAME);
            } catch ( ClassNotFoundException cnf ) {
                //chorus-spring is not in classpath
            }
            if ( c != null) {
                springInjector = (SpringInjector)c.newInstance();
            }
        } catch (Exception e) {
            log.error("Failed to instantiate " + SPRING_INJECTOR_CLASSNAME, e);
        }
    }


    public List<FeatureToken> processFeatures(ExecutionToken executionToken, List<File> featureFiles) throws Exception {
        List<FeatureToken> allFeatures = new ArrayList<FeatureToken>();

        //load all available feature classes
        HashMap<String, Class> allHandlerClasses = handlerClassDiscovery.discoverHandlerClasses(basePackages);

        HashMap<Class, Object> unmanagedHandlerInstances = new HashMap<Class, Object>();

        //FOR EACH FEATURE FILE
        for (File featureFile : featureFiles) {

            List<FeatureToken> features = parseFeatures(featureFile, executionToken);
            if ( features != null ) {
                filterFeaturesByScenarioTags(features);

                //RUN EACH FEATURE
                for (FeatureToken feature : features) {
                    try {
                        processFeature(
                            executionToken,
                            allFeatures,
                            allHandlerClasses,
                            unmanagedHandlerInstances,
                            featureFile,
                            feature
                        );
                    } catch (Throwable t) {
                        log.error("Exception while running feature " + feature, t);
                        executionToken.incrementFeaturesFailed();
                    }
                }
            }
        }
        return allFeatures;
    }

    private List<FeatureToken> parseFeatures(File featureFile, ExecutionToken executionToken) {
        List<FeatureToken> features = null;
        ChorusParser parser = new ChorusParser();
        try {
            log.info(String.format("Loading feature from file: %s", featureFile));
            features = parser.parse(new FileReader(featureFile));
            //we can end up with more than one feature per file if using Chorus 'configurations'
        } catch (Throwable t) {
            log.warn("Failed to parse feature file " + featureFile + " will skip this feature file");
            if ( t.getMessage() != null ) {
                log.warn(t.getMessage());
            }
            //in fact the feature file might contain more than one feature although this is probably a bad practice-
            // we can't know if parsing failed, best we can do is increment failed by one
            executionToken.incrementFeaturesFailed();
        }
        return features;
    }

    private void processFeature(ExecutionToken executionToken, List<FeatureToken> results, HashMap<String, Class> allHandlerClasses, HashMap<Class, Object> unmanagedHandlerInstances, File featureFile, FeatureToken feature) throws Exception {

        //notify we started, even if there are missing handlers
        //(but nothing will be done)
        //this is still important so execution listeners at least see the feature (but will show as 'unimplemented')
        log.trace("Processing feature " + feature);
        executionListenerSupport.notifyFeatureStarted(executionToken, feature);

        results.add(feature);

        //check that the required handler classes are all available and list them in order of precidence
        List<Class> orderedHandlerClasses = new ArrayList<Class>();
        StringBuilder unavailableHandlersMessage = handlerClassDiscovery.findHandlerClasses(allHandlerClasses, feature, orderedHandlerClasses);
        boolean foundAllHandlerClasses = unavailableHandlersMessage.length() == 0;

        //run the scenarios in the feature
        if (foundAllHandlerClasses) {
            log.debug("The following handlers will be used " + orderedHandlerClasses);
            runScenarios(executionToken, unmanagedHandlerInstances, featureFile, feature, orderedHandlerClasses);

            String description = feature.isPassed() ? " passed! " : feature.isPending() ? " pending! " : " failed! ";
            log.trace("The feature " + description);

            if ( feature.isPassed()) {
                executionToken.incrementFeaturesPassed();
            } else if ( feature.isPending() ) {
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

    private void runScenarios(ExecutionToken executionToken, HashMap<Class, Object> unmanagedHandlerInstances, File featureFile, FeatureToken feature, List<Class> orderedHandlerClasses) throws Exception {
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
                featureFile,
                feature,
                orderedHandlerClasses,
                handlerInstances,
                isLastScenario,
                scenario
            );
        }
    }

    private void processScenario(ExecutionToken executionToken, HashMap<Class, Object> unmanagedHandlerInstances, File featureFile, FeatureToken feature, List<Class> orderedHandlerClasses, List<Object> handlerInstances, boolean isLastScenario, ScenarioToken scenario) throws Exception {
        executionListenerSupport.notifyScenarioStarted(executionToken, scenario);

        log.info(String.format("Processing scenario: %s", scenario.getName()));

        //reset the ChorusContext for the scenario
        ChorusContext.destroy();

        addHandlerInstances(unmanagedHandlerInstances, featureFile, feature, orderedHandlerClasses, handlerInstances);

        runScenarioSteps(executionToken, handlerInstances, scenario);

        if ( scenario.isPassed() ) {
            executionToken.incrementScenariosPassed();
        } else if ( scenario.isPending()) {
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

    private boolean runScenarioSteps(ExecutionToken executionToken, List<Object> handlerInstances, ScenarioToken scenario) {
        log.debug("Running scenario steps for Scenario " + scenario);
        //RUN THE STEPS IN THE SCENARIO
        boolean scenarioPassed = true;//track the scenario state
        List<StepToken> steps = scenario.getSteps();
        StepEndState endState;
        for (StepToken step : steps) {

            //process the step
            boolean forceSkip = !scenarioPassed;
            endState = processStep(executionToken, handlerInstances, step, forceSkip);

            switch (endState) {
                case PASSED:
                    break;
                case FAILED:
                    scenarioPassed = false;//skip (don't execute) the rest of the steps
                    break;
                case UNDEFINED:
                    scenarioPassed = false;//skip (don't execute) the rest of the steps
                    break;
                case PENDING:
                    scenarioPassed = false;//skip (don't execute) the rest of the steps
                    break;
                case SKIPPED:
                    break;
            }
        }
        return scenarioPassed;
    }

    private void addHandlerInstances(HashMap<Class, Object> unmanagedHandlerInstances, File featureFile, FeatureToken feature, List<Class> orderedHandlerClasses, List<Object> handlerInstances) throws Exception {
        //CREATE THE HANDLER INSTANCES
        if (handlerInstances.size() == 0) {
            log.debug("Creating handler instances for feature " + feature);
            //first scenario in file, so initialise the handler instances in order of precedence
            for (Class handlerClass : orderedHandlerClasses) {
                //create a new SCENARIO scoped handler
                Handler handlerAnnotation = (Handler) handlerClass.getAnnotation(Handler.class);
                if (handlerAnnotation.scope() == HandlerScope.SCENARIO) {
                    handlerInstances.add(createAndInitHandlerInstance(handlerClass, featureFile, feature));
                    log.debug("Created new scenario scoped handler: " + handlerAnnotation.value());
                }
                //or (re)use an UNMANAGED scoped handlers
                else if (handlerAnnotation.scope() == HandlerScope.UNMANAGED) {
                    Object handler = unmanagedHandlerInstances.get(handlerClass);
                    if (handler == null) {
                        handler = createAndInitHandlerInstance(handlerClass, featureFile, feature);
                        unmanagedHandlerInstances.put(handlerClass, handler);
                        log.debug("Created new unmanaged handler: " + handlerAnnotation.value());
                    }
                    handlerInstances.add(createAndInitHandlerInstance(handlerClass, featureFile, feature));
                }
            }
        } else {
            //replace scenario scoped handlers if not first scenario in feature file
            for (int i = 0; i < handlerInstances.size(); i++) {
                Object handler = handlerInstances.get(i);
                Handler handlerAnnotation = handler.getClass().getAnnotation(Handler.class);
                if (handlerAnnotation.scope() == HandlerScope.SCENARIO) {
                    handlerInstances.remove(i);
                    handlerInstances.add(i, createAndInitHandlerInstance(handler.getClass(), featureFile, feature));
                    log.debug("Replaced scenario scoped handler: " + handlerAnnotation.value());
                }
            }
        }
    }

    /**
     * @param handlerInstances the objects on which to execute the step (ordered by greatest precidence first)
     * @param step      details of the step to be executed
     * @param skip      is true the step will be skipped if found
     * @return the exit state of the executed step
     */
    private StepEndState processStep(ExecutionToken executionToken, List<Object> handlerInstances, StepToken step, boolean skip) {

        log.trace("Starting to process step " + step);
        executionListenerSupport.notifyStepStarted(executionToken, step);

        //return this at the end
        StepEndState endState = null;

        if (skip) {
            log.debug("Skipping step  " + step);
            //output skipped and don't call the method
            endState = StepEndState.SKIPPED;
            executionToken.incrementStepsSkipped();
        } else {
            log.debug("Processing step " + step);
            //identify what method should be called and its parameters
            StepDefinitionMethodFinder stepDefinitionMethodFinder = new StepDefinitionMethodFinder(handlerInstances, step);
            stepDefinitionMethodFinder.findStepMethod();

            //call the method if found
            if (stepDefinitionMethodFinder.isMethodAvailable()) {
                //setting a pending message in the step annotation implies the step is pending - we don't execute it
                String pendingMessage = stepDefinitionMethodFinder.getMethodToCallPendingMessage();
                if (!pendingMessage.equals(Step.NO_PENDING_MESSAGE)) {
                    log.debug("Step has a pending message " + pendingMessage + " skipping step");
                    step.setMessage(pendingMessage);
                    endState = StepEndState.PENDING;
                    executionToken.incrementStepsPending();
                } else {
                    if (dryRun) {
                        log.debug("Dry Run, so not executing this step");
                        step.setMessage("This step is OK");
                        endState = StepEndState.DRYRUN;
                        executionToken.incrementStepsPassed(); // treat dry run as passed? This state was unsupported in previous results
                    } else {
                        log.debug("Now executing the step using method " + stepDefinitionMethodFinder.getMethodToCall());
                        try {
                            //call the step method using reflection
                            Object result = stepDefinitionMethodFinder.getMethodToCall().invoke(
                                stepDefinitionMethodFinder.getInstanceToCallOn(),
                                stepDefinitionMethodFinder.getMethodCallArgs()
                            );
                            log.debug("Finished executing the step, step passed, result was " + result);
                            if (result != null) {
                                step.setMessage(result.toString());
                            }
                            endState = StepEndState.PASSED;
                            executionToken.incrementStepsPassed();
                        } catch (InvocationTargetException e) {
                            log.debug("Step execution failed, we hit an exception invoking the step method");
                            //here if the method called threw an exception
                            if (e.getTargetException() instanceof StepPendingException) {
                                StepPendingException spe = (StepPendingException) e.getTargetException();
                                step.setThrowable(spe);
                                step.setMessage(spe.getMessage());
                                endState = StepEndState.PENDING;
                                executionToken.incrementStepsPending();
                                log.debug("Step Pending Exception prevented execution");
                            } else {
                                Throwable cause = e.getCause();
                                step.setThrowable(cause);
                                String location = "";
                                if ( ! (cause instanceof ChorusRemotingException) ) {
                                    //the remoting exception contains its own location in the message
                                    location = ExceptionHandling.getExceptionLocation(cause);
                                }
                                String message = cause.getMessage() != null ? cause.getMessage() : cause.getClass().getSimpleName();
                                step.setMessage(location + message);
                                endState = StepEndState.FAILED;
                                executionToken.incrementStepsFailed();
                                log.debug("Exception failed due to exception " + e.getMessage());
                            }
                        } catch (Exception e) {
                            log.error(e);
                        }
                    }
                }
            } else {
                log.debug("Could not find a step method definition for step " + step);
                //no method found yet for this step
                endState = StepEndState.UNDEFINED;
                executionToken.incrementStepsUndefined();
            }
        }

        step.setEndState(endState);
        executionListenerSupport.notifyStepCompleted(executionToken, step);
        return endState;
    }

    private void filterFeaturesByScenarioTags(List<FeatureToken> features) {
        log.debug("Filtering by scenario tags");
        //FILTER THE FEATURES AND SCENARIOS
        if (filterExpression != null) {
            for (Iterator<FeatureToken> fi = features.iterator(); fi.hasNext(); ) {
                //remove all filtered scenarios from this feature
                FeatureToken feature = fi.next();
                for (Iterator<ScenarioToken> si = feature.getScenarios().iterator(); si.hasNext(); ) {
                    ScenarioToken scenario = si.next();
                    if (!tagExpressionEvaluator.shouldRunScenarioWithTags(filterExpression, scenario.getTags())) {
                        log.debug("Removing scenario " + scenario + " which does not match tag " + filterExpression);
                        si.remove();
                    }
                }
                //if there are no scenarios left, then remove this feature from the list to run
                if (feature.getScenarios().size() == 0) {
                    log.debug("Will not run feature " + fi + " which does not have any scenarios which " +
                            "passed the tag filter " + filterExpression);
                    fi.remove();
                }
            }
        }
    }

    private Object createAndInitHandlerInstance(Class handlerClass, File featureFile, FeatureToken featureToken) throws Exception {
        Object featureInstance = handlerClass.newInstance();
        log.trace("Created handler class " + handlerClass + " instance " + featureInstance);
        injectSpringResources(featureInstance, featureToken);
        injectInterpreterResources(featureInstance, featureFile, featureToken);
        return featureInstance;
    }

    /**
     * Will load a Spring context from the named @ContextConfiguration resource. Will then inject the beans
     * into fields annotated with @Resource where the name of the bean matches the name of the field.
     *
     * @param handler an instance of the handler class that will be used for testing
     */
    private void injectSpringResources(Object handler, FeatureToken featureToken) throws Exception {
        log.trace("Looking for SpringContext annotation on handler " + handler);
        Class<?> handlerClass = handler.getClass();
        SpringContext springContext = handlerClass.getAnnotation(SpringContext.class);
        if (springContext != null) {
            String contextFileName = springContext.value()[0];
            log.debug("Found SpringContext annotation with value " + contextFileName  + " will inject spring context");
            springInjector.injectSpringContext(handler, featureToken, contextFileName);
        }
    }

    private void injectInterpreterResources(Object handler, File featureFile, FeatureToken featureToken) {
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
                    o = featureFile;
                } else if ("feature.dir".equals(resourceName)) {
                    o = featureFile.getParentFile();
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
        springInjector.disposeContext(handler);

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
        this.dryRun = dryRun;
    }

    public void setFilterExpression(String filterExpression) {
        this.filterExpression = filterExpression;
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

}
