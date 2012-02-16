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

package uk.co.smartkey.chorus.core.interpreter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.co.smartkey.chorus.annotations.*;
import uk.co.smartkey.chorus.core.interpreter.tagexpressions.TagExpressionEvaluator;
import uk.co.smartkey.chorus.util.RegexpUtils;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

/**
 * Created by: Steve Neal
 * Date: 29/09/11
 */
@SuppressWarnings("unchecked")
public class ChorusInterpreter {

    private Log log = LogFactory.getLog(ChorusInterpreter.class);

    private boolean dryRun;
    private String[] basePackages = new String[0];
    private String filterExpression;

    private List<ChorusInterpreterExecutionListener> listeners = new ArrayList<ChorusInterpreterExecutionListener>();

    /**
     * Map: Handler instance -> Spring context
     */
    private Map<Object, FileSystemXmlApplicationContext> springContexts = new HashMap<Object, FileSystemXmlApplicationContext>();

    /**
     * Always included in the Handlers base package scan
     */
    private static final String CHORUS_HANDLERS_PACKAGE = "uk.co.smartkey.chorus.handlers";

    /**
     * Used to determine whether a scenario should be run
     */
    private final TagExpressionEvaluator tagExpressionEvaluator = new TagExpressionEvaluator();

    public List<FeatureToken> processFeatures(List<File> featureFiles) throws Exception {

        List<FeatureToken> results = new ArrayList<FeatureToken>();

        //load all available feature classes
        HashMap<String, Class> allHandlerClasses = loadHandlerClasses(basePackages);

        HashMap<Class, Object> unmanagedHandlerInstances = new HashMap<Class, Object>();

        //FOR EACH FEATURE FILE
        for (File featureFile : featureFiles) {
            ChorusParser parser = new ChorusParser();
            List<FeatureToken> features = parser.parse(new FileReader(featureFile));

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

            //RUN EACH FEATURE
            for (FeatureToken feature : features) {
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
                    notifyStartFeature(feature);

                    //this will contain the handlers for the feature file (scenario scopes ones will be replaced for each scenario)
                    List<Object> handlerInstances = new ArrayList<Object>();
                    //FOR EACH SCENARIO
                    List<ScenarioToken> scenarios = feature.getScenarios();
                    for (Iterator<ScenarioToken> iterator = scenarios.iterator(); iterator.hasNext(); ) {
                        ScenarioToken scenario = iterator.next();
                        notifyStartScenario(scenario);

                        boolean isLastScenario = !iterator.hasNext();
                        log.info(String.format("Processing scenario: %s", scenario.getName()));

                        //reset the ChorusContext for the scenario
                        ChorusContext.destroy();

                        //CREATE THE HANDLER INSTANCES
                        if (handlerInstances.size() == 0) {
                            //first scenario in file, so initialise the handler instances in order of precidence
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


                        //RUN THE STEPS IN THE SCENARIO
                        boolean scenarioPassed = true;//track the scenario state
                        List<StepToken> steps = scenario.getSteps();
                        StepEndState endState;
                        for (StepToken step : steps) {

                            //process the step
                            boolean forceSkip = !scenarioPassed;
                            endState = processStep(handlerInstances, step, forceSkip);

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

                        //CLEAN UP SCENARIO SCOPED HANDLERS
                        for (int i = 0; i < handlerInstances.size(); i++) {
                            Object handler = handlerInstances.get(i);
                            Handler handlerAnnotation = handler.getClass().getAnnotation(Handler.class);
                            if (isLastScenario || handlerAnnotation.scope() == HandlerScope.SCENARIO) {
                                cleanupHandler(handler);
                                log.debug("Cleaned up scenario handler: " + handlerAnnotation.value());
                            }
                        }
                    }
                } else {
                    feature.setUnavailableHandlersMessage(unavailableHandlersMessage.toString());
                }
            }
        }

        return results;
    }

    //
    // Execution event methods
    //

    public void addExecutionListener(ChorusInterpreterExecutionListener listener) {
        listeners.add(listener);
    }

    public boolean removeExecutionListener(ChorusInterpreterExecutionListener listener) {
        return listeners.remove(listener);
    }

    private void notifyStepExecuted(StepToken step) {
        for (ChorusInterpreterExecutionListener listener : listeners) {
            listener.stepExecuted(step);
        }
    }

    private void notifyStartFeature(FeatureToken feature) {
        for (ChorusInterpreterExecutionListener listener : listeners) {
            listener.startFeature(feature);
        }
    }

    private void notifyStartScenario(ScenarioToken scenario) {
        for (ChorusInterpreterExecutionListener listener : listeners) {
            listener.startScenario(scenario);
        }
    }

    private Object createAndInitHandlerInstance(Class handlerClass, File featureFile, FeatureToken featureToken) throws Exception {
        Object featureInstance = handlerClass.newInstance();
        injectSpringResources(featureInstance, featureToken);
        injectInterpreterResources(featureInstance, featureFile, featureToken);
        return featureInstance;
    }

    /**
     * Scans the classpath for features
     *
     * @param basePackages name of the base package under which a recursive scan for @Handler classes will be performed
     * @return a Map of [feature-name -> feature class]
     */
    private HashMap<String, Class> loadHandlerClasses(String[] basePackages) {
        //always include the Chorus handlers package
        String[] allBasePackages = new String[basePackages.length + 1];
        allBasePackages[0] = CHORUS_HANDLERS_PACKAGE;
        System.arraycopy(basePackages, 0, allBasePackages, 1, basePackages.length);

        HashMap<String, Class> featureClasses = new HashMap<String, Class>();
        HandlerClassScanner scanner = new HandlerClassScanner();
        Set<BeanDefinitionHolder> holders = scanner.myscan(allBasePackages);
        for (BeanDefinitionHolder holder : holders) {
            try {
                Class featureClass = Class.forName(holder.getBeanDefinition().getBeanClassName());
                Handler f = (Handler) featureClass.getAnnotation(Handler.class);
                String featureName = f.value();
                featureClasses.put(featureName, featureClass);
            } catch (ClassNotFoundException e) {
                log.error(e);//should never get here
            }
        }
        return featureClasses;
    }

    /**
     * @param instances the objects on which to execute the step (ordered by greatest precidence first)
     * @param step      details of the step to be executed
     * @param skip      is true the step will be skipped if found
     * @return the exit state of the executed step
     */
    private StepEndState processStep(List<Object> instances, StepToken step, boolean skip) {

        //return this at the end
        StepEndState endState = null;

        if (skip) {
            //output skipped and don't call the method
            endState = StepEndState.SKIPPED;
        } else {
            //identify what method should be called and its parameters
            Method methodToCall = null;
            Object instanceToCallOn = null;
            Object[] methodCallArgs = null;
            String methodToCallPendingMessage = "";

            //find the method to call
            for (Object instance : instances) {
                for (Method method : instance.getClass().getMethods()) {

                    //only check methods with Step annotation
                    Step stepAnnotationInstance = method.getAnnotation(Step.class);
                    if (stepAnnotationInstance != null) {
                        String regex = stepAnnotationInstance.value();
                        String action = step.getAction();

                        Object[] values = RegexpUtils.extractGroups(regex, action, method.getParameterTypes());
                        if (values != null) { //the regexp matched the action and the method's parameters
                            if (methodToCall == null) {
                                methodToCall = method;
                                methodCallArgs = values;
                                methodToCallPendingMessage = stepAnnotationInstance.pending();
                                instanceToCallOn = instance;
                            } else {
                                log.warn(String.format("Ambiguous method (%s.%s) found for step (%s) will use first method found (%s.%s)",
                                        instance.getClass().getSimpleName(),
                                        method.getName(),
                                        step,
                                        instanceToCallOn.getClass().getSimpleName(),
                                        methodToCall.getName()));
                            }
                        }
                    }
                }
            }

            //call the method if found
            if (methodToCall == null) {
                //no method found yet for this step
                endState = StepEndState.UNDEFINED;
            } else {
                if (!methodToCallPendingMessage.equals(Step.NO_PENDING_MESSAGE)) {
                    step.setMessage(methodToCallPendingMessage);
                    endState = StepEndState.PENDING;
                } else {
                    if (dryRun) {
                        step.setMessage("This step is OK");
                        endState = StepEndState.DRYRUN;
                    } else {
                        try {
                            //call the step method using reflection
                            Object result = methodToCall.invoke(instanceToCallOn, methodCallArgs);
                            if (result != null) {
                                step.setMessage(result.toString());
                            }
                            endState = StepEndState.PASSED;
                        } catch (InvocationTargetException e) {
                            //here if the method called threw an exception
                            if (e.getTargetException() instanceof StepPendingException) {
                                StepPendingException spe = (StepPendingException) e.getTargetException();
                                step.setThrowable(spe);
                                step.setMessage(spe.getMessage());
                                endState = StepEndState.PENDING;
                            } else {
                                Throwable cause = e.getCause();
                                step.setThrowable(cause);
                                step.setMessage(cause.getMessage());
                                endState = StepEndState.FAILED;
                            }
                        } catch (Exception e) {
                            log.error(e);
                        }
                    }
                }
            }
        }

        step.setEndState(endState);
        notifyStepExecuted(step);
        return endState;
    }

    /**
     * Will load a Spring context from the named @ContextConfiguration resource. Will then inject the beans
     * into fields annotated with @Resource where the name of the bean matches the name of the field.
     *
     * @param handler an instance of the handler class that will be used for testing
     */
    private void injectSpringResources(Object handler, FeatureToken featureToken) {
        Class<?> featureClass = handler.getClass();
        ContextConfiguration contextConfiguration = featureClass.getAnnotation(ContextConfiguration.class);
        if (contextConfiguration != null) {
            String contextFileName = contextConfiguration.value()[0];

            //check for a 'Configuration:' specific Spring context
            if (featureToken.getConfigurationName() != null) {
                if (contextFileName.endsWith(".xml")) {
                    String tmp = String.format("%s-%s.xml", contextFileName.substring(0, contextFileName.length() - 4), featureToken.getConfigurationName());
                    URL url = featureClass.getResource(tmp);
                    if (url != null) {
                        contextFileName = tmp;
                    }
                } else {
                    log.warn("Unexpected suffix for Spring config file (should end with .xml) : " + contextFileName);
                }
            }

            URL url = featureClass.getResource(contextFileName);
            FileSystemXmlApplicationContext springContext = new FileSystemXmlApplicationContext(url.toExternalForm());
            springContexts.put(handler, springContext);

            //inject handler fields with the Spring beans
            Field[] fields = featureClass.getDeclaredFields();
            for (Field field : fields) {
                Resource resourceAnnotation = field.getAnnotation(Resource.class);
                if (resourceAnnotation != null) {
                    boolean beanNameInAnnotation = !"".equals(resourceAnnotation.name());
                    String name = beanNameInAnnotation ? resourceAnnotation.name() : field.getName();
                    Object bean = springContext.getBean(name);
                    if (bean == null) {
                        log.error("Failed to set @Resource (" + name + "). No such bean exists in application context.");
                    }
                    try {
                        field.setAccessible(true);
                        field.set(handler, bean);
                    } catch (IllegalAccessException e) {
                        log.error("Failed to set @Resource (" + name + ") with bean of type: " + bean.getClass(), e);
                    }
                }
            }
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
                } else if ("feature.token".equals(resourceName)) {
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
        //cleanup Spring fixture
        FileSystemXmlApplicationContext springContext = springContexts.remove(handler);
        if (springContext != null) {
            springContext.destroy();
        }

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
}
