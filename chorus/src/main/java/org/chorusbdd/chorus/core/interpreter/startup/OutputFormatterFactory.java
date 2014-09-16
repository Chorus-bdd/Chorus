package org.chorusbdd.chorus.core.interpreter.startup;

import org.chorusbdd.chorus.config.ChorusConfigProperty;
import org.chorusbdd.chorus.config.ConfigProperties;
import org.chorusbdd.chorus.logging.ChorusOut;
import org.chorusbdd.chorus.output.ConsoleOutputFormatter;
import org.chorusbdd.chorus.output.OutputFormatter;
import org.chorusbdd.chorus.output.PlainOutputFormatter;

/**
 * Created by nick on 04/02/14.
 */
public class OutputFormatterFactory {

    public OutputFormatter createOutputFormatter(ConfigProperties configProperties) {

        String formatterClass = configProperties.getValue(ChorusConfigProperty.OUTPUT_FORMATTER);
        
        if ( configProperties.isTrue(ChorusConfigProperty.CONSOLE_MODE)) {
            formatterClass = ConsoleOutputFormatter.class.getName();
        }

        OutputFormatter formatter = new PlainOutputFormatter();
        if ( formatterClass != null) {
            try {
                Class formatterClazz = Class.forName(formatterClass);
                Object o = formatterClazz.newInstance();
                if ( o instanceof OutputFormatter) {
                    formatter = (OutputFormatter)o;
                } else {
                    System.out.println("The " + ChorusConfigProperty.OUTPUT_FORMATTER.getSystemProperty() + 
                            " property must be a class which implements OutputFormatter");
                }
            } catch (Exception e) {
                System.err.println("Failed to create results formatter " + formatterClass + " " + e);
            }
        }

        formatter.setPrintStream(ChorusOut.out);
        return formatter;
    }
}
