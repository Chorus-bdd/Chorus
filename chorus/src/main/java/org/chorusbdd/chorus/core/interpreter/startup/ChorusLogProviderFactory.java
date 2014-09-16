package org.chorusbdd.chorus.core.interpreter.startup;

import org.chorusbdd.chorus.config.ConfigProperties;
import org.chorusbdd.chorus.logging.ChorusLogProvider;
import org.chorusbdd.chorus.logging.ChorusOut;
import org.chorusbdd.chorus.output.OutputFormatter;
import org.chorusbdd.chorus.output.OutputFormatterLogProvider;

/**
 * Created by nick on 10/02/14.
 */
public class ChorusLogProviderFactory {

    public ChorusLogProvider createLogProvider(ConfigProperties configProperties, OutputFormatter outputFormatter) {
        ChorusLogProvider result = createSystemPropertyProvider(configProperties);
        if ( result == null) {
            result = createDefaultLogProvider(outputFormatter);
        }

        if ( result instanceof OutputFormatterLogProvider) {
            ((OutputFormatterLogProvider) result).setOutputFormatter(outputFormatter);
        }
        return result;
    }

    private ChorusLogProvider createDefaultLogProvider(OutputFormatter outputFormatter) {
        OutputFormatterLogProvider outputFormatterLogProvider = new OutputFormatterLogProvider();
        outputFormatterLogProvider.setOutputFormatter(outputFormatter);
        return outputFormatterLogProvider;
    }

    private ChorusLogProvider createSystemPropertyProvider(ConfigProperties configProperties) {
        ChorusLogProvider result = null;
        String provider = null;
        try {
            provider = configProperties.getValue(ChorusConfigProperty.LOG_PROVIDER);
            if ( provider != null ) {
                Class c = Class.forName(provider);
                result = (ChorusLogProvider)c.newInstance();
            }
        } catch (Throwable t) {
            ChorusOut.err.println("Failed to instantiate ChorusLogProvider class " + provider + " will use the default LogProvider");
        }
        return result;
    }
}
