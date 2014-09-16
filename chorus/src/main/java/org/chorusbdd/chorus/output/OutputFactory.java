package org.chorusbdd.chorus.output;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by nick on 15/09/2014.
 */
public class OutputFactory {

    private static volatile OutputFormatter outputFormatter = NullOutputFormatter.NULL_FORMATTER;

    private static final AtomicBoolean isInitialized = new AtomicBoolean();

    public static void setOutputFormatter(OutputFormatter outputFormatter) {
        if (! isInitialized.getAndSet(true) ) {
            OutputFactory.outputFormatter = outputFormatter;
        }
    }

    public static OutputFormatter getOutputFormatter() {
        return outputFormatter;
    }

}
