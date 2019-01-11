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
package org.chorusbdd.chorus.selftest.processhandler.featurescopedprocess;

import org.chorusbdd.chorus.remoting.jmx.ChorusHandlerJmxExporter;

import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 04/07/12
 * Time: 09:16
 */
public class FeatureScopedProcessMain {

    public static void main(String[] args) throws InterruptedException {
        
        String name = args[0];
        
        //add the process name to the step patterns so we can distinguish steps on each process
        UnaryOperator<Pattern> patternPreProcessor = pattern -> Pattern.compile(pattern.pattern() + " in " + name);
        
        FeatureScopedProcessHandler handler = new FeatureScopedProcessHandler();
        ChorusHandlerJmxExporter exporter = new ChorusHandlerJmxExporter(patternPreProcessor, handler);
        exporter.export();

        Thread.sleep(60000);
    }

}
