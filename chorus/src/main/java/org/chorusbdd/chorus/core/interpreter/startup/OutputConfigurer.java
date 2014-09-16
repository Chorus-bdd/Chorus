package org.chorusbdd.chorus.core.interpreter.startup;

import org.chorusbdd.chorus.config.ConfigReader;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.logging.ChorusLogProvider;
import org.chorusbdd.chorus.logging.LogLevel;
import org.chorusbdd.chorus.output.OutputFactory;
import org.chorusbdd.chorus.output.OutputFormatter;
import org.chorusbdd.chorus.output.OutputFormatterLogProvider;

/**
 * Created by nick on 15/09/2014.
 *
 * Initialize the Chorus output and logging subsystems according to the configured properties of the interpreter
 */
public class OutputConfigurer {

    public void configureOutput(ConfigReader config) {
        OutputFormatter outputFormatter = createOutputFormatter(config);
        createLogProvider(config, outputFormatter);
    }

    private void createLogProvider(ConfigReader config, OutputFormatter outputFormatter) {
        ChorusLogProvider chorusLogProvider = new ChorusLogProviderFactory().createLogProvider(config, outputFormatter);
        setLogLevel(config, chorusLogProvider);
        ChorusLogFactory.setLogProvider(chorusLogProvider);
    }

    private OutputFormatter createOutputFormatter(ConfigReader config) {
        OutputFormatter outputFormatter = new OutputFormatterFactory().createOutputFormatter(config);
        OutputFactory.setOutputFormatter(outputFormatter);
        return outputFormatter;
    }

    private void setLogLevel(ConfigReader config, ChorusLogProvider chorusLogProvider) {
        String logLevel = config.getValue(ChorusConfigProperty.LOG_LEVEL);
        LogLevel l = LogLevel.getLogLevel(logLevel);

        if ( chorusLogProvider instanceof OutputFormatterLogProvider) {

            //only the built in OutputFormatterLogProvider used by the interpreter supports
            //setting the log level from the interpreter configuration

            //Alternative log providers if set may use a variety of means for log level configuration
            //e.g. a log4j.xml file, at present we rely on the user to configure them as they see fit
            ((OutputFormatterLogProvider) chorusLogProvider).setLogLevel(l);
        }
    }
}
