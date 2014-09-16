package org.chorusbdd.chorus.logging;


/**
 * Created by nick on 15/09/2014.
 */
public class DefaultLogProviderFactory {

    public ChorusLogProvider createLogProvider() {
        ChorusLogProvider result = new NullLogProvider();

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
