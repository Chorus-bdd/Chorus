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
package org.chorusbdd.chorus.interpreter.interpreter;

import org.chorusbdd.chorus.annotations.*;
import org.chorusbdd.chorus.interpreter.subsystem.SubsystemManager;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.results.ScenarioToken;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 18/11/13
 * Time: 22:20
 *
 * Manage the creation of Handler instances while a feature and its scenarios are run
 */
public class HandlerManager {

    private ChorusLog log = ChorusLogFactory.getLog(HandlerManager.class);
    
    //retain ordering of handlers 
    private final LinkedHashMap<Class, Object> featureScopedHandlers = new LinkedHashMap<>();
    
    private final FeatureToken feature;
    private final List<Class> orderedHandlerClasses;
    private final SpringContextSupport springContextSupport;
    private SubsystemManager subsystemManager;
    private String profile;
    private ScenarioToken currentScenario;

    public HandlerManager(FeatureToken feature, List<Class> orderedHandlerClasses, SpringContextSupport springContextSupport, SubsystemManager subsystemManager, String profile) {
        this.feature = feature;
        this.orderedHandlerClasses = orderedHandlerClasses;
        this.springContextSupport = springContextSupport;
        this.subsystemManager = subsystemManager;
        this.profile = profile;
    }
    
    public void createFeatureScopedHandlers() throws Exception {
        for (Class handlerClass : orderedHandlerClasses) {
            //create a new SCENARIO scoped handler
            Handler handlerAnnotation = (Handler) handlerClass.getAnnotation(Handler.class);
            if (handlerAnnotation.scope() != Scope.SCENARIO) { //feature or unmanaged
                Object handler = createAndInitHandlerInstance(handlerClass);
                featureScopedHandlers.put(handlerClass, handler);
                log.debug("Created new feature scoped handler: " + handlerAnnotation.value());
            }
        }    
    }

    public void setCurrentScenario(ScenarioToken currentScenario) {
        this.currentScenario = currentScenario;
    }
    
    public List<Object> getOrCreateHandlersForScenario() throws Exception {
        List<Object> handlerInstances = new ArrayList<>();
        
        for ( Class handlerClass : orderedHandlerClasses ) {
            Handler handlerAnnotation = (Handler) handlerClass.getAnnotation(Handler.class);
            if ( handlerAnnotation.scope() != Scope.SCENARIO ) {
                Object handler = featureScopedHandlers.get(handlerClass);
                assert(handler != null); //must have been created during createFeatureScopedHandlers
                log.debug("Adding feature scoped handler " + handler + " class " + handlerClass);
                handlerInstances.add(handler);
            } else {
                log.debug("Creating scenario scoped handler " + handlerClass);
                Object handler = createAndInitHandlerInstance(handlerClass);
                handlerInstances.add(handler);
            }
        }
        return handlerInstances;
    }

    private Object createAndInitHandlerInstance(Class handlerClass) throws Exception {
        Object handler = handlerClass.newInstance();
        log.debug("Created handler class " + handlerClass + " instance " + handler);
        injectSpringResources(handler);
        return handler;
    }


    public void processStartOfFeature() throws Exception {
        processStartOfScope(Scope.FEATURE, featureScopedHandlers.values());
    }

    /**
     * Scope is starting, perform the required processing on the supplied handlers.
     */
    public void processStartOfScope(Scope scopeStarting, Iterable<Object> handlerInstances) throws Exception {
        for (Object handler : handlerInstances) {
            Handler handlerAnnotation = handler.getClass().getAnnotation(Handler.class);
            Scope handlerScope = handlerAnnotation.scope();

            injectResourceFieldsForScope(scopeStarting, handler, handlerScope, handlerInstances);
            runLifecycleMethods(handler, handlerScope, scopeStarting, false);
        }    
    }

    private void injectResourceFieldsForScope(Scope scopeStarting, Object handler, Scope handlerScope, Iterable<Object> handlerInstances) {
        if ( scopeStarting == Scope.FEATURE && handlerScope == Scope.FEATURE) {
            //for feature scoped handlers, initialize the feature-level resources when feature starts
            injectResourceFields(handler, handlerInstances, Scope.FEATURE);
        } else if ( scopeStarting == Scope.SCENARIO) {
            if ( handlerScope == Scope.SCENARIO ) {
                //if a handler is created scenario scope it will not have had its feature-level
                //annotations set when the feature started
                injectResourceFields(handler, handlerInstances, Scope.FEATURE, Scope.SCENARIO);
            } else {
                injectResourceFields(handler, handlerInstances, Scope.SCENARIO);
            }
        }
    }

    public void processEndOfFeature() throws Exception {
        processEndOfScope( Scope.FEATURE, featureScopedHandlers.values());
    }
    
    /**
     * Scope is ending, perform the required processing on the supplied handlers.
     */
    public void processEndOfScope(Scope scopeEnding, Iterable<Object> handlerInstances) throws Exception {
        for (Object handler : handlerInstances) {
            Handler handlerAnnotation = handler.getClass().getAnnotation(Handler.class);
            Scope scope = handlerAnnotation.scope();

            runLifecycleMethods(handler, scope, scopeEnding, true);

            //dispose handler instances with a scope which matches the scopeEnding
            if (scope == scopeEnding) {
                disposeSpringResources(handler, scopeEnding);
            }
        }
    }

    /**
     * Run any lifecycle methods which match the targetMethodScope (.e.g at end of SCENARIO, run SCENARIO scoped methods)
     */
    private void runLifecycleMethods(Object handler, Scope scope, Scope targetMethodScope, boolean isDestroy) throws Exception {
        String description = isDestroy ? "@Destroy" : "@Initialize";
        log.debug("Running " + description + " methods for Handler " + handler);

        Class<?> handlerClass = handler.getClass();
        for (Method method : handlerClass.getMethods()) {   //getMethods() includes inherited methods
            if (method.getParameterTypes().length == 0) {

                Scope methodScope = getMethodScope(isDestroy, method);

                //if this lifecycle method is for the correct cope, then run it
                if (methodScope != null && methodScope == targetMethodScope) {
                    log.trace("Found " + description + " annotation with scope " + targetMethodScope + " on handler method " + method + " and will now invoke it");
                    try {
                        method.invoke(handler);
                    } catch ( Throwable t) {
                        log.warn("Exception when calling " + description + " method [" + method + "] with scope " + targetMethodScope + " on handler " + handlerClass, t);
                    }
                }
            }
        }
    }

    //return the scope of a lifecycle method, or null if the method is not a lifecycle method
    private Scope getMethodScope(boolean isDestroy, Method method) {
        Scope methodScope;
        if ( isDestroy ) {
            Destroy annotation = method.getAnnotation(Destroy.class);
            methodScope = annotation != null ? annotation.scope() : null;
        } else {
            Initialize annotation = method.getAnnotation(Initialize.class);
            methodScope = annotation != null ? annotation.scope() : null;
        }
        return methodScope;
    }


    private void injectSpringResources(Object handler) throws Exception {
        log.debug("Injecting any spring resources for " + handler + " class " + handler.getClass());
        springContextSupport.injectSpringResources(handler, feature);
    }
    
    private void disposeSpringResources(Object handler, Scope scope) {
        log.debug("Disposing any spring resources for " + handler + " class " + handler.getClass() + " with scope " + scope);
        springContextSupport.dispose(handler);
    }


    /**
     * Here we set the values of any handler fields annotated with @ChorusResource
     */
    private void injectResourceFields(Object handler, Iterable<Object> handlerInstances, Scope... scopes) {
        Class<?> featureClass = handler.getClass();

        List<Field> allFields = new ArrayList<>();
        addAllPublicFields(featureClass, allFields);
        log.trace("Now examining handler fields for ChorusResource annotation " + allFields);

        HashSet<Scope> scopeSet = new HashSet<>(Arrays.asList(scopes));
        for (Field field : allFields) {
            setChorusResource(handler, handlerInstances, field, scopeSet);
        }
    }

    private void setChorusResource(Object handler, Iterable<Object> handlerInstances, Field field, Set<Scope> scopes) {
        ChorusResource a = field.getAnnotation(ChorusResource.class);
        if (a != null) {
            String resourceName = a.value();
            log.debug("Found ChorusResource annotation " + resourceName + " on field " + field);
            field.setAccessible(true);
            Object o = null;

            if ( scopes.contains(Scope.FEATURE)) {
                o = getFeatureResource(resourceName, handlerInstances);
            }

            if ( o == null && scopes.contains(Scope.SCENARIO)) {
                o = getScenarioResource(resourceName);
            }

            if (o != null) {
                try {
                    field.set(handler, o);
                } catch (IllegalAccessException e) {
                    log.error("Failed to set @ChorusResource (" + resourceName + ") with object of type: " + o.getClass(), e);
                }
            } else {
                log.trace("Set field to value " + o);
            }
        }
    }

    private Object getScenarioResource(String resourceName) {
        Object o = null;
        if ( ChorusResource.scenarioToken.equals(resourceName)) {
            o = currentScenario;
        }
        return o;
    }

    private Object getFeatureResource(String resourceName, Iterable<Object> handlerInstances) {
        Object o = null;
        if (ChorusResource.featureFile.equals(resourceName)) {
            o = feature.getFeatureFile();
        } else if (ChorusResource.featureDir.equals(resourceName)) {
            o = feature.getFeatureFile().getParentFile();
        } else if (ChorusResource.featureToken.equals(resourceName)) {
            o = feature;
        } else if (ChorusResource.profile.equals(resourceName)) {
            o = profile;
        } else if (resourceName.matches("subsystem.+")) {
            o = getSubsystemResource(resourceName);
        } else if ( resourceName.startsWith(ChorusResource.handlerPrefix)) {
            o = getHandlerResource(resourceName, handlerInstances);
        }
        return o;
    }

    private Object getSubsystemResource(String resourceName) {
        Object o;
        String subsystemId = resourceName.substring(10);
        log.trace("Looking for subsystem named " + subsystemId + " for @ChorusResource field");
        o = subsystemManager.getSubsystemById(subsystemId);

        if ( o == null) {
            log.warn("Cannot set @ChorusResource field since cannot find a subsystem which is named " + resourceName);
        }
        return o;
    }

    private Object getHandlerResource(String resourceName, Iterable<Object> handlerInstances) {
        Object o = null;
        String handlerName = resourceName.substring(ChorusResource.handlerPrefix.length());
        for ( Object handlerInstance : handlerInstances) {
            Handler h = handlerInstance.getClass().getAnnotation(Handler.class);
            if ( handlerName.trim().toLowerCase().equals(h.value().trim().toLowerCase())) {
                o = handlerInstance;
                break;
            }
        }

        if ( o == null) {
            log.warn("Could not find a handler named " + handlerName +
                    " to inject field annotated @ChorusResource(\"handler." + handlerName + "\", missing Uses: statement?");
        }
        return o;
    }

    private void addAllPublicFields(Class<?> featureClass, List<Field> allFields) {
        allFields.addAll(Arrays.asList(featureClass.getDeclaredFields()));

        Class s = featureClass.getSuperclass();
        if ( s != Object.class ) {
            addAllPublicFields(s, allFields);
        }
    }
}
