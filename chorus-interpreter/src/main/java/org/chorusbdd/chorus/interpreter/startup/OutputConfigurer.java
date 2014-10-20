/**
 *  Copyright (C) 2000-2014 The Software Conservancy and Original Authors.
 *  All rights reserved.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to
 *  deal in the Software without restriction, including without limitation the
 *  rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 *  sell copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 *  IN THE SOFTWARE.
 *
 *  Nothing in this notice shall be deemed to grant any rights to trademarks,
 *  copyrights, patents, trade secrets or any other intellectual property of the
 *  licensor or any contributor except as expressly stated herein. No patent
 *  license is granted separate from the Software, for code that you delete from
 *  the Software, or for combinations of the Software with other software or
 *  hardware.
 */
package org.chorusbdd.chorus.interpreter.startup;

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