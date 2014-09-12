package org.chorusbdd.chorus.logging;

import org.chorusbdd.chorus.util.config.ChorusConfigProperty;
import org.chorusbdd.chorus.util.config.ConfigProperties;

/**
 * Created by nick on 10/02/14.
 */
public class LogProviderFactory {

    public ChorusLogProvider createLogProvider(ConfigProperties configProperties, OutputFormatter outputFormatter) {
        ChorusLogProvider result = createSystemPropertyProvider(configProperties, outputFormatter);
        if ( result == null) {
            result = createDefaultLogProvider(outputFormatter);
        }
        return result;
    }
    
    private ChorusLogProvider createDefaultLogProvider(OutputFormatter outputFormatter) {
        OutputFormatterLogProvider outputFormatterLogProvider = new OutputFormatterLogProvider();
        outputFormatterLogProvider.setOutputFormatter(outputFormatter);
        return outputFormatterLogProvider;
    }

    private ChorusLogProvider createSystemPropertyProvider(ConfigProperties configProperties, OutputFormatter outputFormatter) {
        ChorusLogProvider result = null;
        String provider = null;
        try {
            provider = configProperties.getValue(ChorusConfigProperty.LOG_PROVIDER);
            if ( provider != null ) {
                Class c = Class.forName(provider);
                result = (ChorusLogProvider)c.newInstance();
                result.setOutputFormatter(outputFormatter);
            }
        } catch (Throwable t) {
            ChorusOut.err.println("Failed to instantiate ChorusLogProvider class " + provider + " will use the default LogProvider");
        }
        return result;
    }
}
