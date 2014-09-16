package org.chorusbdd.chorus.core.interpreter.startup;

import org.chorusbdd.chorus.config.ChorusConfigProperty;
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

    public void configureOutput(ConfigReader config) {

//        //the logging factory checks the system property version chorusLogProvider when it
//        //performs static initialization - the log provider must be set as a system property
//        //even if provided as a switch
//        ChorusConfigProperty p = ChorusConfigProperty.LOG_PROVIDER;
//        if ( System.getProperty(p.getSystemProperty()) == null && config.isSet(p)) {
//            System.setProperty(p.getSystemProperty(), config.getValue(p));
//        }
//
//        p = ChorusConfigProperty.OUTPUT_FORMATTER;
//        if ( System.getProperty(p.getSystemProperty()) == null && config.isSet(p)) {
//            System.setProperty(p.getSystemProperty(), config.getValue(p));
//        }

        OutputFormatter outputFormatter = new OutputFormatterFactory().createOutputFormatter(config);
        OutputFactory.setOutputFormatter(outputFormatter);

        ChorusLogProvider chorusLogProvider = new ChorusLogProviderFactory().createLogProvider(config, outputFormatter);
        ChorusLogFactory.setLogProvider(chorusLogProvider);
    }
}
