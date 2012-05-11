package org.chorusbdd.chorus.core.interpreter;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 11/05/12
 * Time: 17:30
 *
 * To avoid a dependency on Spring from core interpreter, we check for the existence of a SpringContextInjector
 * on the classpath, and if it exists, instantiate an instance of it which we address via the SpringInjector interface
 */
public interface SpringInjector {

    SpringInjector NULL_INJECTOR = new SpringInjector() {

        //if the null injector is in use, this means we failed to find and instantiate the SpringContextInjector from the chorus-spring module
        public void injectSpringContext(Object handler, FeatureToken featureToken, Class<?> featureClass, String contextFileName) {
            throw new UnsupportedOperationException("You need to add chorus-spring to your classpath to use the SpringContext annotation");
        }

        public void disposeContext(Object handler) {
        }
    };

    public void injectSpringContext(Object handler, FeatureToken featureToken, Class<?> featureClass, String contextFileName) throws Exception;

    void disposeContext(Object handler);
}
