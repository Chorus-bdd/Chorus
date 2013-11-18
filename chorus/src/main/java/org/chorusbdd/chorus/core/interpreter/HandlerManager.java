package org.chorusbdd.chorus.core.interpreter;

import org.chorusbdd.chorus.annotations.ChorusResource;
import org.chorusbdd.chorus.annotations.Destroy;
import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.HandlerScope;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 18/11/13
 * Time: 22:20
 * To change this template use File | Settings | File Templates.
 */
public class HandlerManager {

    private static ChorusLog log = ChorusLogFactory.getLog(ChorusInterpreter.class);
    
    private final HashMap<Class, Object> unmanagedHandlerInstances = new HashMap<Class, Object>();
    private final FeatureToken feature;
    private final List<Class> orderedHandlerClasses;
    private final SpringContextSupport springContextSupport;

    public HandlerManager(FeatureToken feature, List<Class> orderedHandlerClasses, SpringContextSupport springContextSupport) {
        this.feature = feature;
        this.orderedHandlerClasses = orderedHandlerClasses;
        this.springContextSupport = springContextSupport;
    }

    public List<Object> getAndCreateHandlers() throws Exception {
        List<Object> handlerInstances = new ArrayList<Object>();
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
                else if (handlerAnnotation.scope() == HandlerScope.UNMANAGED ) {
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
        return handlerInstances;
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

    public void cleanupHandlers(List<Object> handlerInstances, boolean lastScenario) throws Exception {
        //CLEAN UP SCENARIO SCOPED HANDLERS
        for (int i = 0; i < handlerInstances.size(); i++) {
            Object handler = handlerInstances.get(i);
            Handler handlerAnnotation = handler.getClass().getAnnotation(Handler.class);
            if (lastScenario || handlerAnnotation.scope() == HandlerScope.SCENARIO) {
                cleanupHandler(handler);
                log.debug("Cleaned up scenario handler: " + handlerAnnotation.value());
            }
        }
    }

    private void cleanupHandler(Object handler) throws Exception {
        log.debug("Cleaning Up Handler " + handler);
        springContextSupport.dispose(handler);

        Class<?> handlerClass = handler.getClass();
        Handler handlerAnnotation = handlerClass.getAnnotation(Handler.class);
        if ( handlerAnnotation.scope() != HandlerScope.UNMANAGED ) {
            for (Method method : handlerClass.getMethods()) {
                if (method.getParameterTypes().length == 0) {
                    if (method.getAnnotation(Destroy.class) != null) {
                        log.trace("Found Destroy annotation on handler method " + method + " will invoke");
                        try {
                            method.invoke(handler);
                        } catch ( Throwable t) {
                            log.warn("Exception when calling @Destroy method [" + method + "] on handler " + handlerClass, t);
                        }
                    }
                }
            }
        }
    }

}
