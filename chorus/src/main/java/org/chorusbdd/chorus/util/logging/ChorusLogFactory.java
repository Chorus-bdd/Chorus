package org.chorusbdd.chorus.util.logging;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 14/05/12
 * Time: 18:30
 */
public class ChorusLogFactory {

    private static ChorusLogProvider logProvider;

    public static final String COMMONS_LOG_FACTORY_CLASSNAME = "org.apache.commons.logging.LogFactory";

    static {
        ChorusLogProvider result = createSystemPropertyProvider();
        if ( result == null ) {
            result = createCommonsLogFactoryProvider(result);
        }
        logProvider = result;
    }

    private static ChorusLogProvider createCommonsLogFactoryProvider(ChorusLogProvider result) {
        try {
            Class c = Class.forName(COMMONS_LOG_FACTORY_CLASSNAME);
            //commons is on the classpath, load our commons wrapper provider
            //do this with reflection otherwise we'd load the class, and have a runtime mandatory dependency at this point
            Class chorusWrapperProvider = Class.forName("org.chorusbdd.chorus.util.logging.ChorusCommonsLogProvider");
            result = (ChorusLogProvider)chorusWrapperProvider.newInstance();
        } catch (ClassNotFoundException e) {
            System.out.println("Could not find class " + e.getMessage() + " on the classpath will use default logging");
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
            provider = System.getProperty(ChorusLogProvider.LOG_PROVIDER_SYSTEM_PROPERTY);
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
