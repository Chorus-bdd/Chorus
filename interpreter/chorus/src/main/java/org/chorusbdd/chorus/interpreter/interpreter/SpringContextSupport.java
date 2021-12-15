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

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.results.FeatureToken;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 07/12/12
 * Time: 09:06
 * To change this template use File | Settings | File Templates.
 */
public class SpringContextSupport {

    private static final String SPRING_CONTEXT_CLASS = "org.springframework.context.ApplicationContext";

    private ChorusLog log = ChorusLogFactory.getLog(SpringContextSupport.class);

    private SpringInjector springInjector = SpringInjector.NULL_INJECTOR;

    public SpringContextSupport() {
        try {
            Class springClass = checkClass(SPRING_CONTEXT_CLASS);
            if ( springClass == null) {
                log.info("Spring does not appear to be on the classpath will not initialize chorus-spring");
            } else {
                Class injectorClass = checkClass(SPRING_INJECTOR_CLASSNAME);
                if ( injectorClass == null) {
                    log.info("Spring Injector class not found will not initialize chorus-spring");
                } else {
                    springInjector = (SpringInjector)injectorClass.newInstance();
                }

            }
        } catch (Exception e) {
            log.error("Failed to instantiate " + SPRING_INJECTOR_CLASSNAME, e);
        }
    }

    /**
     * @return true if a class with the given name can be loaded from the classpath
     */
    private Class checkClass(String className) {
        Class c = null;
        try {
            c = Class.forName(className);
        } catch ( NoClassDefFoundError e ) {
        } catch ( ClassNotFoundException cnf ) {}
        return c;
    }

    /**
     * Defines the class which will be instantiated to perform injection of Spring context/resources
     */
    private final String SPRING_INJECTOR_CLASSNAME = "org.chorusbdd.chorus.spring.SpringContextInjector";


    /**
     * Will load a Spring context from the named @ContextConfiguration resource. Will then inject the beans
     * into fields annotated with @Resource where the name of the bean matches the name of the field.
     *
     * @param handler an instance of the handler class that will be used for testing
     */
    void injectSpringResources(Object handler, FeatureToken featureToken) throws Exception {
        log.trace("Looking for SpringContext annotation on handler " + handler);
        Class<?> handlerClass = handler.getClass();
        Annotation[] annotations = handlerClass.getAnnotations();
        String contextFileName = findSpringContextFileName(handler, annotations);
        if (contextFileName != null) {
            log.debug("Found SpringContext annotation with value " + contextFileName  + " will inject spring context");
            springInjector.injectSpringContext(handler, featureToken, contextFileName);
        }
    }

    //support chorus built in SpringContext annotation or Spring's ContextConfiguration
    //we have to use reflection for this since Spring is not a mandatory dependency / not necessarily on the classpath for chorus core
    private String findSpringContextFileName(Object handler, Annotation[] annotations) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        String contextFileName = null;
        for (Annotation a : annotations) {
            String n = a.annotationType().getSimpleName();
            if ( n.equals("ContextConfiguration") || n.equals("SpringContext")) {
                log.trace("Found an spring context annotation " + a);
                contextFileName = getContextPathFromSpringAnnotation(handler, a, n);
                if ( contextFileName != null) {
                    break;
                }
            }
        }
        return contextFileName;
    }

    private String getContextPathFromSpringAnnotation(Object handler, Annotation a, String n) {
        String result = null;
        try {
            Class c = a.annotationType();
            Method m = c.getMethod("value");
            String[] s = (String[])m.invoke(a);
            if ( s.length == 1 ) {
                String contextPath = s[0];
                log.trace("Found annotation " + n + " on handler " + handler + ", will inject resources from " + contextPath);
                result = contextPath;
            } else {
                log.warn("The " + n + " annotation on handler " + handler + " does not have a value set to specify " +
                        "context path, will ignore this annotation - any Spring resources will not be injected" );
            }
        } catch (Throwable t) {
            log.error("Failed when trying to read spring context path from annotation " + n + " on handler "
                    + handler + ", will skip this annotation", t);
        }
        return result;
    }

    public void dispose(Object handler) {
        springInjector.disposeContext(handler);
    }

}
