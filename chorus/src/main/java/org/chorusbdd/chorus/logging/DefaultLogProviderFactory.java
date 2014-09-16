package org.chorusbdd.chorus.logging;


/**
 * Created by nick on 15/09/2014.
 */
public class DefaultLogProviderFactory {

    public ChorusLogProvider createLogProvider() {
        ChorusLogProvider result = NullLogProvider.NULL_LOG_PROVIDER;

        result = getSystemPropertyProvider(result);

        //at present I think it's probably better to force the user to turn on the ChorusCommonsLogProvider by
        //setting the appropriate system property -DchorusLogProvider

        //the alternative is that we end up with log4j missing configuration errors in the output
        //where log4j is in the classpath but the user hasn't actually configured it (common)
        //        if ( result == NullLogProvider.NULL_LOG_PROVIDER) {
        //            result = getCommonsProviderIfCommonsIsPresent();
        //        }

        if ( result == NullLogProvider.NULL_LOG_PROVIDER) {
            //fall back to a basic std out logger
            result = new StdOutLogProvider();
        }
        return result;
    }

//
//    private ChorusLogProvider getCommonsProviderIfCommonsIsPresent() {
//        ChorusLogProvider result = null;
//            try {
//
//                //do we have commons logging on the classpath?
//                Class c = Class.forName("org.apache.commons.logging.Log");
//
//                //if so load up the ChorusCommonsLogProvider
//                //doing this by reflection to avoid any nasty class loading issues if we import
//                //ChorusCommonsLogProvider and commons isn't actually present
//                Class commonsLogProvider = Class.forName("org.chorusbdd.chorus.logging.ChorusCommonsLogProvider");
//                result = (ChorusLogProvider)commonsLogProvider.newInstance();
//
//            } catch (Exception e) {
//            }
//        return result;
//    }

    private ChorusLogProvider getSystemPropertyProvider(ChorusLogProvider result) {
        String provider = System.getProperty("chorusLogProvider");
        if ( provider != null) {
            try {
                if ( provider != null ) {
                    Class c = Class.forName(provider);
                    result = (ChorusLogProvider)c.newInstance();
                }
            } catch (Throwable t) {
                System.err.println("Failed to instantiate ChorusLogProvider class " + provider + " will use the default LogProvider");
            }
        }
        return result;
    }
}
