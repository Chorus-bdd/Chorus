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
package org.chorusbdd.chorus.selftest;

import org.chorusbdd.chorus.interpreter.startup.ChorusConfigProperty;
import org.chorusbdd.chorus.output.AbstractChorusOutputWriter;

import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 26/06/12
 * Time: 08:43
 *
 * Standard set of properties for self-testing
 */
public class DefaultTestProperties extends Properties {

    public DefaultTestProperties() {
        //test output at log level info
        //we need to use log4j logging for our testing since when we test Spring features, Spring logs via commons
        put(ChorusConfigProperty.HANDLER_PACKAGES.getSystemProperty(), "org.chorusbdd.chorus.selftest");
        put(AbstractChorusOutputWriter.OUTPUT_FORMATTER_STEP_LENGTH_CHARS, "100");
        put(ChorusConfigProperty.LOG_LEVEL.getSystemProperty(), "info");
    }
}
