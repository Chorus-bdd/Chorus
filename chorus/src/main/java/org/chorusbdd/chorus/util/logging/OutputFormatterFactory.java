package org.chorusbdd.chorus.util.logging;

import org.chorusbdd.chorus.util.config.ChorusConfigProperty;

/**
 * Created by nick on 04/02/14.
 */
public class OutputFormatterFactory {

    public OutputFormatter createOutputFormatter() {
        OutputFormatter formatter = new PlainOutputFormatter();

        String formatterClass = System.getProperty(ChorusConfigProperty.CHORUS_OUTPUT_FORMATTER_SYS_PROP);
        if ( formatterClass != null) {
            try {
                Class formatterClazz = Class.forName(formatterClass);
                Object o = formatterClazz.newInstance();
                if ( o instanceof OutputFormatter) {
                    formatter = (OutputFormatter)o;
                } else {
                    System.out.println("The " + ChorusConfigProperty.CHORUS_OUTPUT_FORMATTER_SYS_PROP + 
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
