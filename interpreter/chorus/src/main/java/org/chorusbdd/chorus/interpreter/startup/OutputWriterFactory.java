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
import org.chorusbdd.chorus.output.ConsoleOutputWriter;
import org.chorusbdd.chorus.output.ChorusOutputWriter;
import org.chorusbdd.chorus.output.PlainOutputWriter;

/**
 * Created by nick on 04/02/14.
 */
public class OutputWriterFactory {

    public ChorusOutputWriter createOutputWriter(ConfigProperties configProperties) {

        String formatterClass = configProperties.getValue(ChorusConfigProperty.OUTPUT_WRITER);
        
        if ( configProperties.isTrue(ChorusConfigProperty.CONSOLE_MODE)) {
            formatterClass = ConsoleOutputWriter.class.getName();
        }

        ChorusOutputWriter formatter = new PlainOutputWriter();
        if ( formatterClass != null) {
            try {
                Class formatterClazz = Class.forName(formatterClass);
                Object o = formatterClazz.newInstance();
                if ( o instanceof ChorusOutputWriter) {
                    formatter = (ChorusOutputWriter)o;
                } else {
                    System.out.println("The " + ChorusConfigProperty.OUTPUT_WRITER.getSystemProperty() +
                            " property must be a class which implements ChorusOutputWriter");
                }
            } catch (Exception e) {
                System.err.println("Failed to create results formatter " + formatterClass + " " + e);
            }
        }
        return formatter;
    }
}
