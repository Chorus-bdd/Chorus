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
package org.chorusbdd.chorus.spring;

import org.chorusbdd.chorus.interpreter.interpreter.SpringInjector;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.results.FeatureToken;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 11/05/12
 * Time: 17:34
 *
 * Instantiate a SpringContext and associate it with a handler class
 * Clean up and closeAllConnections the context on scenario completion
 */
public class SpringContextInjector implements SpringInjector {

    private ChorusLog log = ChorusLogFactory.getLog(SpringContextInjector.class);

    /**
     * Map: Handler instance -> Spring context
     *
     * n.b. Nick, I am guessing the reason for storing in this map may be to retain a strong reference to the context until we
     * explicitly clear down after the scenario, since I couldn't find another obvious reason
     */
    private Map<Object, ContextWithURL> springContextByCreatingHandler = new HashMap<>();

    public void injectSpringContext(Object handler, FeatureToken featureToken, String contextFileName) {
        Class handlerClass = handler.getClass();
        //check for a 'Configuration:' specific Spring context
        if (featureToken.isConfiguration()) {
            if (contextFileName.endsWith(".xml")) {
                String tmp = String.format("%s-%s.xml", contextFileName.substring(0, contextFileName.length() - 4), featureToken.getConfigurationName());
                URL url = handlerClass.getResource(tmp);
                if (url != null) {
                    contextFileName = tmp;
                }
            } else {
                log.warn("Unexpected suffix for Spring config file (should end with .xml) : " + contextFileName);
            }
        }

        URL url = handlerClass.getResource(contextFileName);

        //this spring context may have been already loaded for another handler instance - if so, reuse it
        AbstractApplicationContext springContext = getExistingContextByUrl(url);

        //not already created, new one up
        if ( springContext == null) {
            springContext = createNewContext(handler, contextFileName, handlerClass, url);
        }

        //FileSystemXmlApplicationContext springContext = new FileSystemXmlApplicationContext(url.toExternalForm());
        injectResourceFields(springContext, handler);
    }

    private AbstractApplicationContext createNewContext(Object handler, String contextFileName, Class handlerClass, URL url) {
        AbstractApplicationContext springContext;
        log.debug("Creating Spring Context from URL " + url);
        springContext = new ClassPathXmlApplicationContext(contextFileName, handlerClass);
        springContextByCreatingHandler.put(handler, new ContextWithURL(springContext, url));
        log.info("Created new SpringContext for handler " + handlerClass + " at " + contextFileName);
        return springContext;
    }

    private AbstractApplicationContext getExistingContextByUrl(URL url) {
        AbstractApplicationContext result = null;
        for ( ContextWithURL c : springContextByCreatingHandler.values()) {
            if ( c.url.equals(url)) {
                result = c.springContext;
                break;
            }
        }
        return result;
    }

    /**
     * Inject the fields annotated with @Resource annotation with beans from a SpringContext, using the field
     * name to match the bean name, or where a different annotation value is supplied use the annotation value
     * as the bean name
     *
     * @param handlers          handler instances to configure
     * @param springContext     Spring context to supply beans
     */
    public static void injectResourceFields(ApplicationContext springContext, Object... handlers ) {
        for (Object handler : handlers) {
            Class handlerClass = handler.getClass();
            injectResourceFields(springContext, handler, handlerClass);

        }
    }

    private static void injectResourceFields(ApplicationContext springContext, Object handler, Class handlerClass) {
        ChorusLog log = ChorusLogFactory.getLog(SpringContextInjector.class);

        //inject handler fields with the Spring beans
        Field[] fields = handlerClass.getDeclaredFields();
        for (Field field : fields) {
            Resource resourceAnnotation = field.getAnnotation(Resource.class);
            if (resourceAnnotation != null) {
                boolean beanNameInAnnotation = !"".equals(resourceAnnotation.name());
                String name = beanNameInAnnotation ? resourceAnnotation.name() : field.getName();
                Object bean = springContext.getBean(name);
                log.trace("Found spring Resource annotation for field " + field + " will attempt to inject Spring bean " + bean);
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

        Class superclass = handlerClass.getSuperclass();
        if ( superclass != Object.class) {
            injectResourceFields(springContext, handler, superclass);
        }
    }

    public void disposeContext(Object handler) {
        //cleanup Spring fixture which was associated with this handler instance
        ContextWithURL springContext = springContextByCreatingHandler.remove(handler);
        if (springContext != null) {
            log.debug("Destroying spring context " + springContext + " for handler " + handler);
            springContext.springContext.destroy();
        }
    }

    private class ContextWithURL {
        URL url;
        AbstractApplicationContext springContext;

        public ContextWithURL(AbstractApplicationContext springContext, URL url) {
            this.springContext = springContext;
            this.url = url;
        }
    }


}
