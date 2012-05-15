package org.chorusbdd.chorus.spring;

import org.chorusbdd.chorus.core.interpreter.token.FeatureToken;
import org.chorusbdd.chorus.core.interpreter.SpringInjector;
import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;
import org.springframework.context.support.FileSystemXmlApplicationContext;

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
 * Clean up and destroy the context on scenario completion
 */
public class SpringContextInjector implements SpringInjector {

    private ChorusLog log = ChorusLogFactory.getLog(SpringContextInjector.class);

    /**
     * Map: Handler instance -> Spring context
     *
     * n.b. Nick - I am guess the reason for storing in this map may be to retain a strong reference to the context until we
     * explicitly clear down after the scenario, since I couldn't find another obvious reason
     */
    private Map<Object, FileSystemXmlApplicationContext> springContexts = new HashMap<Object, FileSystemXmlApplicationContext>();

    public void injectSpringContext(Object handler, FeatureToken featureToken, Class<?> featureClass, String contextFileName) {
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

    public void disposeContext(Object handler) {
        //cleanup Spring fixture which was associated with this handler instance
        FileSystemXmlApplicationContext springContext = springContexts.remove(handler);
        if (springContext != null) {
            springContext.destroy();
        }
    }


}
