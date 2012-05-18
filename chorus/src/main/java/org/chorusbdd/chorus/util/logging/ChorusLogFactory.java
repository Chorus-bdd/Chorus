package org.chorusbdd.chorus.util.logging;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 14/05/12
 * Time: 18:30
 *
 *  A logging abstraction which prevents a mandatory runtime dependency on commons logging LogFactory
 *  (so this class is a logging abstraction over a logging abstraction -- all in the name of no mandatory runtime dependencies!)
 *
 *  The actual implementation of ChorusLogProvider is derived at runtime in the following manner:
 *
 *  1) if the -DchorusLogProvider system property is set, take the value of this property as the name of the factory
 *  class to instantiate. This class must implement ChorusLogProvider
 *
 *  2) In the absence of a specified ChorusLogProvider system property, we examine the classpath to see if the commons
 *  LogFactory is available. If it is, we obtain a commons LogFactory instance. Commons Log instances returned are by the
 *  Commons LogFactory are wrapped in a ChorusLog wrapper class to avoid the rest of chorus having a depedency on commons
 *
 *  3) If neither of the above approaches works, create a ChorusStandardOutLogFactory which will return ChorusLog instances
 *  which write their logging output direct to standard out/err
 */
public class ChorusLogFactory {

    private static final ChorusLogProvider logProvider;

    public static final String LOG_PROVIDER_SYSTEM_PROPERTY = "logProvider";

    private static final String COMMONS_LOG_FACTORY_CLASSNAME = "org.apache.commons.logging.LogFactory";
    private static final String CHORUS_COMMONS_LOG_PROVIDER = "org.chorusbdd.chorus.util.logging.ChorusCommonsLogProvider";

    static {
        ChorusLogProvider result = createSystemPropertyProvider();
        if ( result == null ) {
            result = createCommonsLogFactoryProvider(result);
        }
        if ( result == null ) {
            result = createStandardOutLogFactory();
            result.getLog(ChorusLogFactory.class).info(
                "Could not find commons logging on the classpath will use default stdout logging"
            );
        }
        logProvider = result;
    }

    public static ChorusLog getLog(Class clazz) {
        return logProvider.getLog(clazz);
    }

    private static ChorusLogProvider createStandardOutLogFactory() {
        return new StandardOutLogProvider();
    }

    private static ChorusLogProvider createCommonsLogFactoryProvider(ChorusLogProvider result) {
        try {
            Class c = Class.forName(COMMONS_LOG_FACTORY_CLASSNAME);
            //commons is on the classpath, load our commons wrapper provider
            //do this with reflection otherwise we'd load the class, and have a runtime mandatory dependency at this point
            Class chorusWrapperProvider = Class.forName(CHORUS_COMMONS_LOG_PROVIDER);
            result = (ChorusLogProvider)chorusWrapperProvider.newInstance();
        } catch (ClassNotFoundException e) {
            //Could not find java commons logging on the classpath will use default logging
        } catch (InstantiationException e) {
            System.err.println("Failed to instantiate ChorusLogProvider will revert to default logging");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            System.err.println("IllegalAccessException when initializing ChorusLogProvider, will revert to default logging");
            e.printStackTrace();
        }
        return result;
    }

    private static ChorusLogProvider createSystemPropertyProvider() {
        ChorusLogProvider result = null;
        String provider = null;
        try {
            provider = System.getProperty(LOG_PROVIDER_SYSTEM_PROPERTY);
            if ( provider != null ) {
                Class c = Class.forName(provider);
                result = (ChorusLogProvider)c.newInstance();
            }
        } catch (Throwable t) {
            System.err.println("Failed to instantiate ChorusLogProvider class " + provider + ", will look for alternative commons logger or use Std out");
        }
        return result;
    }

}
