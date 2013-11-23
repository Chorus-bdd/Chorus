package org.chorusbdd.chorus.core.interpreter;

import org.chorusbdd.chorus.annotations.*;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;

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

    private static ChorusLog log = ChorusLogFactory.getLog(ChorusInterpreter.class);
    
    private final HashMap<Class, Object> featureScopedHandlers = new HashMap<Class, Object>();
    private final FeatureToken feature;
    private final List<Class> orderedHandlerClasses;
    private final SpringContextSupport springContextSupport;

    public HandlerManager(FeatureToken feature, List<Class> orderedHandlerClasses, SpringContextSupport springContextSupport) {
        this.feature = feature;
        this.orderedHandlerClasses = orderedHandlerClasses;
        this.springContextSupport = springContextSupport;
    }
    
    public void createFeatureScopedHandlers() throws Exception {
        for (Class handlerClass : orderedHandlerClasses) {
            //create a new SCENARIO scoped handler
            Handler handlerAnnotation = (Handler) handlerClass.getAnnotation(Handler.class);
            if (handlerAnnotation.scope() != HandlerScope.SCENARIO) { //feature or unmanaged
                Object handler = createAndInitHandlerInstance(handlerClass, feature);
                featureScopedHandlers.put(handlerClass, handler);
                log.debug("Created new unmanaged handler: " + handlerAnnotation.value());
            }
        }    
    }

    public List<Object> getOrCreateHandlersForScenario() throws Exception {
        List<Object> handlerInstances = new ArrayList<Object>();
        
        for ( Class handlerClass : orderedHandlerClasses ) {
            Handler handlerAnnotation = (Handler) handlerClass.getAnnotation(Handler.class);
            if ( handlerAnnotation.scope() != HandlerScope.SCENARIO ) {
                Object handler = featureScopedHandlers.get(handlerClass);
                assert(handler != null); //must have been created during createFeatureScopedHandlers
                log.debug("Adding feature scoped handler " + handler + " class " + handlerClass);
                handlerInstances.add(handler);
            } else {
                log.debug("Creating scenario scoped handler " + handlerClass);
                Object handler = createAndInitHandlerInstance(handlerClass, feature);
                handlerInstances.add(handler);
            }
        }
        return handlerInstances;
    }

    private Object createAndInitHandlerInstance(Class handlerClass, FeatureToken featureToken) throws Exception {
        Object featureInstance = handlerClass.newInstance();
        log.debug("Created handler class " + handlerClass + " instance " + featureInstance);
        springContextSupport.injectSpringResources(featureInstance, featureToken);
        injectInterpreterResources(featureInstance, featureToken);
        return featureInstance;
    }

    private void injectInterpreterResources(Object handler, FeatureToken featureToken) {
        Class<?> featureClass = handler.getClass();
        
       
        List<Field> allFields = new ArrayList<Field>();
        addAllPublicFields(featureClass, allFields);
        log.trace("Now examining handler fields for ChorusResource annotation " + allFields);
        for (Field field : allFields) {
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

    private void addAllPublicFields(Class<?> featureClass, List<Field> allFields) {
        allFields.addAll(Arrays.asList(featureClass.getDeclaredFields()));
        
        Class s = featureClass.getSuperclass();
        if ( s != Object.class ) {
            addAllPublicFields(s, allFields);
        }
    }


    public void processStartOfFeature() throws Exception {
        processStartOfScope(HandlerScope.FEATURE, featureScopedHandlers.values());
    }

    /**
     * Scope is starting, perform the required processing on the supplied handlers.
     */
    public void processStartOfScope(HandlerScope scopeStarting, Iterable<Object> handlerInstances) throws Exception {
        for (Object handler : handlerInstances) {
            Handler handlerAnnotation = handler.getClass().getAnnotation(Handler.class);
            HandlerScope handlerScope = handlerAnnotation.scope();

            runLifecycleMethods(handler, handlerScope, scopeStarting, false);
        }    
    }

    public void processEndOfFeature() throws Exception {
        processEndOfScope( HandlerScope.FEATURE, featureScopedHandlers.values());
    }
    
    /**
     * Scope is ending, perform the required processing on the supplied handlers.
     */
    public void processEndOfScope(HandlerScope scopeEnding, Iterable<Object> handlerInstances) throws Exception {
        for (Object handler : handlerInstances) {
            Handler handlerAnnotation = handler.getClass().getAnnotation(Handler.class);
            HandlerScope handlerScope = handlerAnnotation.scope();

            runLifecycleMethods(handler, handlerScope, scopeEnding, true);

            //dispose handler instances with a scope which matches the scopeEnding
            if (handlerScope == scopeEnding) {
                disposeHandler(handler, scopeEnding);
            }
        }
    }

    /**
     * Run any lifecycle methods which match the targetMethodScope (.e.g at end of SCENARIO, run SCENARIO scoped methods)
     */
    private void runLifecycleMethods(Object handler, HandlerScope handlerScope, HandlerScope targetMethodScope, boolean isDestroy) throws Exception {
        if ( handlerScope != HandlerScope.UNMANAGED) { 
           //HandlerScope.UNMANAGED handlers do not run destroy or init methods
            String description = isDestroy ? "@Destroy" : "@Initialize";
            log.debug("Running " + description + " methods for Handler " + handler);
    
            Class<?> handlerClass = handler.getClass();
            for (Method method : handlerClass.getMethods()) {
                if (method.getParameterTypes().length == 0) {

                    HandlerScope methodScope = getMethodScope(isDestroy, method);
                    
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
    }

    //return the scope of a lifecycle method, or null if the method is not a lifecycle method
    private HandlerScope getMethodScope(boolean isDestroy, Method method) {
        HandlerScope methodScope = null;
        if ( isDestroy ) {
            Destroy annotation = method.getAnnotation(Destroy.class);
            methodScope = annotation != null ? annotation.scope() : null;
        } else {
            Initialize annotation = method.getAnnotation(Initialize.class);
            methodScope = annotation != null ? annotation.scope() : null;
        }
        return methodScope;
    }

    private void disposeHandler(Object handler, HandlerScope scope) {
        log.debug("Disposing handler " + handler + " class " + handler.getClass() + " with scope " + scope);
        springContextSupport.dispose(handler);
    }

}
