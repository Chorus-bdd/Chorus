/* Copyright (C) 2000-2011 The Software Conservancy as Trustee.
 * All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 *
 * Nothing in this notice shall be deemed to grant any rights to trademarks,
 * copyrights, patents, trade secrets or any other intellectual property of the
 * licensor or any contributor except as expressly stated herein. No patent
 * license is granted separate from the Software, for code that you delete from
 * the Software, or for combinations of the Software with other software or
 * hardware.
 */

package org.chorusbdd.chorus.core.interpreter;

import org.chorusbdd.chorus.annotations.*;
import org.chorusbdd.chorus.core.interpreter.results.*;
import org.chorusbdd.chorus.core.interpreter.scanner.ClasspathScanner;
import org.chorusbdd.chorus.core.interpreter.scanner.HandlerOnlyClassFilter;
import org.chorusbdd.chorus.core.interpreter.tagexpressions.TagExpressionEvaluator;
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

    /**
     * Always included in the Handlers base package scan
     */
    private static final String CHORUS_HANDLERS_PACKAGE = "org.chorusbdd.chorus.handlers";

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


    public TestExecutionToken processFeatures(TestExecutionToken executionToken, List<File> featureFiles) throws Exception {

        //identifies this execution, in case we have parallel or subsequent executions
        executionListenerSupport.notifyStartTests(executionToken);

        List<FeatureToken> allFeatures = new ArrayList<FeatureToken>();

        //load all available feature classes
        HashMap<String, Class> allHandlerClasses = loadHandlerClasses(basePackages);

        HashMap<Class, Object> unmanagedHandlerInstances = new HashMap<Class, Object>();

        //FOR EACH FEATURE FILE
        for (File featureFile : featureFiles) {
            ChorusParser parser = new ChorusParser();
            List<FeatureToken> features = parser.parse(new FileReader(featureFile));

            filterFeaturesByScenarioTags(features);

            //RUN EACH FEATURE
            for (FeatureToken feature : features) {
                processFeature(
                    executionToken,
                    allFeatures,
                    allHandlerClasses,
                    unmanagedHandlerInstances,
                    featureFile,
                    feature
                );
            }
        }

        executionListenerSupport.notifyTestsCompleted(executionToken, allFeatures);
        return executionToken;
    }

    private void processFeature(TestExecutionToken executionToken, List<FeatureToken> results, HashMap<String, Class> allHandlerClasses, HashMap<Class, Object> unmanagedHandlerInstances, File featureFile, FeatureToken feature) throws Exception {
        //notify we started, even if there are missing handlers
        //(but nothing will be done)
        //this is still important so execution listeners at least see the feature (but will show as 'unimplemented')
        executionListenerSupport.notifyFeatureStarted(executionToken, feature);

        results.add(feature);
        log.info(String.format("Loaded feature file: %s", featureFile));

        //check that the required handler classes are all available and list them in order of precidence
        StringBuilder unavailableHandlersMessage = new StringBuilder();
        List<Class> orderedHandlerClasses = new ArrayList<Class>();
        Class mainHandlerClass = allHandlerClasses.get(feature.getName());
        if (mainHandlerClass == null) {
            log.info(String.format("No explicit handler was found for Feature: (%s), will only use those specified in the Uses statements",
                    feature.getName()));
        } else {
            log.debug(String.format("Loaded handler class (%s) for Feature: (%s)",
                    mainHandlerClass.getName(),
                    feature.getName()));

            orderedHandlerClasses.add(mainHandlerClass);
        }
        for (String usesFeatureWithName : feature.getUsesFeatures()) {
            Class usesHandlerClass = allHandlerClasses.get(usesFeatureWithName);
            if (usesHandlerClass == null) {
                unavailableHandlersMessage.append(String.format("'%s' ", usesFeatureWithName));
            } else {
                log.debug(String.format("Loaded handler class (%s) for Uses: (%s)",
                        usesHandlerClass.getName(),
                        usesFeatureWithName));

                orderedHandlerClasses.add(usesHandlerClass);
            }
        }
        boolean foundAllHandlerClasses = unavailableHandlersMessage.length() == 0;

        //run the scenarios in the feature
        if (foundAllHandlerClasses) {

            //this will contain the handlers for the feature file (scenario scopes ones will be replaced for each scenario)
            List<Object> handlerInstances = new ArrayList<Object>();
            //FOR EACH SCENARIO
            List<ScenarioToken> scenarios = feature.getScenarios();
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
        } else {
            feature.setUnavailableHandlersMessage(unavailableHandlersMessage.toString());
            executionToken.incrementUnavailableHandlers();
        }

        executionListenerSupport.notifyFeatureCompleted(executionToken, feature);
    }

    private void processScenario(TestExecutionToken executionToken, HashMap<Class, Object> unmanagedHandlerInstances, File featureFile, FeatureToken feature, List<Class> orderedHandlerClasses, List<Object> handlerInstances, boolean isLastScenario, ScenarioToken scenario) throws Exception {
        executionListenerSupport.notifyScenarioStarted(executionToken, scenario);

        log.info(String.format("Processing scenario: %s", scenario.getName()));

        //reset the ChorusContext for the scenario
        ChorusContext.destroy();

        addHandlerInstances(unmanagedHandlerInstances, featureFile, feature, orderedHandlerClasses, handlerInstances);

        boolean scenarioPassed = runScenarioSteps(executionToken, handlerInstances, scenario);

        if ( scenarioPassed ) {
            executionToken.incrementScenariosPassed();
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

    private boolean runScenarioSteps(TestExecutionToken executionToken, List<Object> handlerInstances, ScenarioToken scenario) {
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
    private StepEndState processStep(TestExecutionToken executionToken, List<Object> handlerInstances, StepToken step, boolean skip) {

        executionListenerSupport.notifyStepStarted(executionToken, step);

        //return this at the end
        StepEndState endState = null;

        if (skip) {
            //output skipped and don't call the method
            endState = StepEndState.SKIPPED;
            executionToken.incrementStepsSkipped();
        } else {
            //identify what method should be called and its parameters
            StepDefinitionMethodFinder stepDefinitionMethodFinder = new StepDefinitionMethodFinder(handlerInstances, step);
            stepDefinitionMethodFinder.findStepMethod();

            //call the method if found
            if (stepDefinitionMethodFinder.isMethodAvailable()) {
                if (!stepDefinitionMethodFinder.getMethodToCallPendingMessage().equals(Step.NO_PENDING_MESSAGE)) {
                    step.setMessage(stepDefinitionMethodFinder.getMethodToCallPendingMessage());
                    endState = StepEndState.PENDING;
                    executionToken.incrementStepsPending();
                } else {
                    if (dryRun) {
                        step.setMessage("This step is OK");
                        endState = StepEndState.DRYRUN;
                        executionToken.incrementStepsPassed(); // treat dry run as passed? This state was unsupported in previous results
                    } else {
                        try {
                            //call the step method using reflection
                            Object result = stepDefinitionMethodFinder.getMethodToCall().invoke(stepDefinitionMethodFinder.getInstanceToCallOn(), stepDefinitionMethodFinder.getMethodCallArgs());
                            if (result != null) {
                                step.setMessage(result.toString());
                            }
                            endState = StepEndState.PASSED;
                            executionToken.incrementStepsPassed();
                        } catch (InvocationTargetException e) {
                            //here if the method called threw an exception
                            if (e.getTargetException() instanceof StepPendingException) {
                                StepPendingException spe = (StepPendingException) e.getTargetException();
                                step.setThrowable(spe);
                                step.setMessage(spe.getMessage());
                                endState = StepEndState.PENDING;
                                executionToken.incrementStepsPending();
                            } else {
                                Throwable cause = e.getCause();
                                step.setThrowable(cause);
                                step.setMessage(cause.getMessage());
                                endState = StepEndState.FAILED;
                                executionToken.incrementStepsFailed();
                            }
                        } catch (Exception e) {
                            log.error(e);
                        }
                    }
                }
            } else {
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
        //FILTER THE FEATURES AND SCENARIOS
        if (filterExpression != null) {
            for (Iterator<FeatureToken> fi = features.iterator(); fi.hasNext(); ) {
                //remove all filtered scenarios from this feature
                FeatureToken feature = fi.next();
                for (Iterator<ScenarioToken> si = feature.getScenarios().iterator(); si.hasNext(); ) {
                    ScenarioToken scenario = si.next();
                    if (!tagExpressionEvaluator.shouldRunScenarioWithTags(filterExpression, scenario.getTags())) {
                        si.remove();
                    }
                }
                //if there are no scenarios left, then remove this feature from the list to run
                if (feature.getScenarios().size() == 0) {
                    fi.remove();
                }
            }
        }
    }

    /**
     * Scans the classpath for features
     *
     * @param basePackages name of the base package under which a recursive scan for @Handler classes will be performed
     * @return a Map of [feature-name -> feature class]
     */
    private HashMap<String, Class> loadHandlerClasses(String[] basePackages) throws Exception {
        //always include the Chorus handlers package
        String[] allBasePackages = new String[basePackages.length + 1];
        allBasePackages[0] = CHORUS_HANDLERS_PACKAGE;
        System.arraycopy(basePackages, 0, allBasePackages, 1, basePackages.length);

        HashMap<String, Class> featureClasses = new HashMap<String, Class>();
        Set<Class> handlerClasses = ClasspathScanner.doScan(new HandlerOnlyClassFilter(), allBasePackages);
        for (Class handlerClass : handlerClasses) {
            Handler f = (Handler) handlerClass.getAnnotation(Handler.class);
            String featureName = f.value();
            featureClasses.put(featureName, handlerClass);
        }
        return featureClasses;
    }

    private Object createAndInitHandlerInstance(Class handlerClass, File featureFile, FeatureToken featureToken) throws Exception {
        Object featureInstance = handlerClass.newInstance();
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
        Class<?> handlerClass = handler.getClass();
        SpringContext springContext = handlerClass.getAnnotation(SpringContext.class);
        if (springContext != null) {
            String contextFileName = springContext.value()[0];
            springInjector.injectSpringContext(handler, featureToken, contextFileName);
        }
    }

    private void injectInterpreterResources(Object handler, File featureFile, FeatureToken featureToken) {
        Class<?> featureClass = handler.getClass();
        Field[] fields = featureClass.getDeclaredFields();
        for (Field field : fields) {
            ChorusResource a = field.getAnnotation(ChorusResource.class);
            if (a != null) {
                field.setAccessible(true);
                String resourceName = a.value();
                Object o = null;
                if ("feature.file".equals(resourceName)) {
                    o = featureFile;
                } else if ("feature.dir".equals(resourceName)) {
                    o = featureFile.getParentFile();
                } else if ("feature.results".equals(resourceName)) {
                    o = featureToken;
                }
                if (o != null) {
                    try {
                        field.set(handler, o);
                    } catch (IllegalAccessException e) {
                        log.error("Failed to set @ChorusResource (" + resourceName + ") with object of type: " + o.getClass(), e);
                    }
                }
            }
        }
    }

    private void cleanupHandler(Object handler) throws Exception {
        springInjector.disposeContext(handler);

        //call any destroy methods on handler instance
        for (Method method : handler.getClass().getMethods()) {
            if (method.getParameterTypes().length == 0) {
                if (method.getAnnotation(Destroy.class) != null) {
                    method.invoke(handler);
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

    public void addExecutionListener(ChorusExecutionListener... listeners) {
        executionListenerSupport.addExecutionListener(listeners);
    }

    public boolean removeExecutionListener(ChorusExecutionListener... listeners) {
        return executionListenerSupport.removeExecutionListener(listeners);
    }

}
