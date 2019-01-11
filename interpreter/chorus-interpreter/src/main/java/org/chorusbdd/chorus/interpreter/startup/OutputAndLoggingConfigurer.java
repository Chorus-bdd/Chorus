/**
 * MIT License
 *
 * Copyright (c) 2019 Chorus BDD Organisation.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.chorusbdd.chorus.interpreter.startup;

import org.chorusbdd.chorus.config.ConfigProperties;
import org.chorusbdd.chorus.config.ConfigReader;
import org.chorusbdd.chorus.executionlistener.ExecutionListener;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.logging.ChorusLogProvider;
import org.chorusbdd.chorus.logging.LogLevel;
import org.chorusbdd.chorus.output.ChorusOutputWriter;
import org.chorusbdd.chorus.output.OutputWriterLogProvider;

/**
 * Created by nick on 15/09/2014.
 *
 * Initialize the Chorus output and logging subsystems according to the configured properties of the interpreter
 */
public class OutputAndLoggingConfigurer {

    private InterpreterOutputExecutionListener outputExecutionListener;
    private ChorusOutputWriter chorusOutputWriter;

    public void configureOutputAndLogging(ConfigReader config) {
        chorusOutputWriter = createOutputWriter(config);

        //the interpreter output execution listener implements OutputFormatter
        //and wraps and delegates to the configured formatter instance

        //We pass the interpreter output execution listener to the log provider
        //This allows us to capture any interpreter logging in the InterpreterOutputExecutionListener before sending it on to the configured
        //formatter for formatting/output

        outputExecutionListener = createInterpreterOutputListener(config, chorusOutputWriter);
        createLogProvider(config, outputExecutionListener);
    }

    private InterpreterOutputExecutionListener createInterpreterOutputListener(ConfigProperties config, ChorusOutputWriter chorusOutputWriter) {
        boolean verbose = config.isTrue(ChorusConfigProperty.SHOW_ERRORS);
        boolean showSummary = config.isTrue(ChorusConfigProperty.SHOW_SUMMARY);
        return new InterpreterOutputExecutionListener(showSummary, verbose, chorusOutputWriter);
    }

    private void createLogProvider(ConfigReader config, ChorusOutputWriter chorusOutputWriter) {
        ChorusLogProvider chorusLogProvider = new ChorusLogProviderFactory().createLogProvider(config, chorusOutputWriter);
        setLogLevel(config, chorusLogProvider);
        ChorusLogFactory.setLogProvider(chorusLogProvider);
    }

    private ChorusOutputWriter createOutputWriter(ConfigReader config) {
        ChorusOutputWriter chorusOutputWriter = new OutputWriterFactory().createOutputWriter(config);
        return chorusOutputWriter;
    }

    private void setLogLevel(ConfigReader config, ChorusLogProvider chorusLogProvider) {
        String logLevel = config.getValue(ChorusConfigProperty.LOG_LEVEL);
        LogLevel l = LogLevel.getLogLevel(logLevel);

        if ( chorusLogProvider instanceof OutputWriterLogProvider) {

            //only the built in OutputFormatterLogProvider used by the interpreter supports
            //setting the log level from the interpreter configuration

            //Alternative log providers if set may use a variety of means for log level configuration
            //e.g. a log4j.xml file, at present we rely on the user to configure them as they see fit
            ((OutputWriterLogProvider) chorusLogProvider).setLogLevel(l);
        }
    }

    public ExecutionListener getOutputExecutionListener() {
        return outputExecutionListener;
    }

    public void dispose() {
        chorusOutputWriter.dispose();
    }
}
