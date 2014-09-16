package org.chorusbdd.chorus.core.interpreter.startup;

import org.chorusbdd.chorus.config.ConfigReader;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.logging.ChorusLogProvider;
import org.chorusbdd.chorus.output.OutputFactory;
import org.chorusbdd.chorus.output.OutputFormatter;

/**
 * Created by nick on 15/09/2014.
 *
 * Initialize the Chorus output and logging subsystems according to the configured properties of the interpreter
 */
public class OutputConfigurer {

    public void configureOutput(ConfigReader configProperties) {
        OutputFormatter outputFormatter = new OutputFormatterFactory().createOutputFormatter(configProperties);
        OutputFactory.setOutputFormatter(outputFormatter);

        ChorusLogProvider chorusLogProvider = new ChorusLogProviderFactory().createLogProvider(configProperties, outputFormatter);
        ChorusLogFactory.setLogProvider(chorusLogProvider);
    }
}
