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
import org.chorusbdd.chorus.logging.ChorusLogProvider;
import org.chorusbdd.chorus.logging.ChorusOut;
import org.chorusbdd.chorus.output.ChorusOutputWriter;
import org.chorusbdd.chorus.output.OutputWriterLogProvider;

/**
 * Created by nick on 10/02/14.
 * 
 * A LogProviderFactory which creates a default LogProvider (OutputWriterLogProvider) when logging within the main 
 * chorus interpreter process.
 * 
 * This factory is not suitable for logging using Chorus classes in libraries which are running in client-side
 * API code or in unit tests outside the main Chorus interpreter process. Instead consider DefaultLogProviderFactory
 */
public class ChorusLogProviderFactory {

    public ChorusLogProvider createLogProvider(ConfigProperties configProperties, ChorusOutputWriter chorusOutputWriter) {
        ChorusLogProvider result = createSystemPropertyProvider(configProperties);
        if ( result == null) {
            result = createDefaultLogProvider(chorusOutputWriter);
        }

        if ( result instanceof OutputWriterLogProvider) {
            ((OutputWriterLogProvider) result).setChorusOutputWriter(chorusOutputWriter);
        }
        return result;
    }

    private ChorusLogProvider createDefaultLogProvider(ChorusOutputWriter chorusOutputWriter) {
        OutputWriterLogProvider outputWriterLogProvider = new OutputWriterLogProvider();
        outputWriterLogProvider.setChorusOutputWriter(chorusOutputWriter);
        return outputWriterLogProvider;
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
