/**
 *  Copyright (C) 2000-2013 The Software Conservancy and Original Authors.
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
package org.chorusbdd.chorus.util.logging;

import org.chorusbdd.chorus.util.config.ChorusConfigProperty;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 14/05/12
 * Time: 18:30
 *
 *  A factory for ChorusLog instances.
 *  
 *  Also creates the OutputFormatter which used to write all Chorus' output
 *  The OutputFormatter implementation can be changed by setting the chorusOutputFormatter system property
 *
 */
public class ChorusLogFactory {

    private static final ChorusLogProvider logProvider;
    private static final OutputFormatter outputFormatter;

    static {
        outputFormatter = createOutputFormatter();
        logProvider = createLogProvider(outputFormatter);
    }
    
    private static ChorusLogProvider createLogProvider(OutputFormatter outputFormatter) {
        ChorusLogProvider result = createSystemPropertyProvider(outputFormatter);
        if ( result == null) {
            result = createDefaultLogProvider(outputFormatter);
        }
        return result;
    }

    private static OutputFormatter createOutputFormatter() {
        OutputFormatterFactory factory = new OutputFormatterFactory();
        return factory.createOutputFormatter();
    }

    public static ChorusLog getLog(Class clazz) {
        return logProvider.getLog(clazz);
    }

    public static ChorusLogProvider getLogProvider() {
        return logProvider;
    }
    
    public static OutputFormatter getOutputFormatter() {
        return outputFormatter;
    }

    private static ChorusLogProvider createDefaultLogProvider(OutputFormatter outputFormatter) {
        OutputFormatterLogProvider outputFormatterLogProvider = new OutputFormatterLogProvider();
        outputFormatterLogProvider.setOutputFormatter(outputFormatter);
        return outputFormatterLogProvider;
    }

    private static ChorusLogProvider createSystemPropertyProvider(OutputFormatter outputFormatter) {
        ChorusLogProvider result = null;
        String provider = null;
        try {
            provider = System.getProperty(ChorusConfigProperty.CHORUS_LOG_PROVIDER_SYS_PROP);
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
