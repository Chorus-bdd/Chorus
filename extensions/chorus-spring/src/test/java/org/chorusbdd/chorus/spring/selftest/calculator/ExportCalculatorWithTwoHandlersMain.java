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
package org.chorusbdd.chorus.spring.selftest.calculator; 

import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.remoting.jmx.ChorusHandlerJmxExporter;

import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

/**
 * Used to run a simple process that can be used to test Chorus features
 *
 * Created by: Steve Neal
 * Date: 14/11/11
 */
public class ExportCalculatorWithTwoHandlersMain {

    public static void main(String[] args) throws Exception {

        String calcName = args[0];

        //change the steps exported according to the name of the calc process we have started
        UnaryOperator<Pattern> patternFunction = pattern -> Pattern.compile(pattern.pattern() + " in " + calcName);

        //export calculator handlers
        new ChorusHandlerJmxExporter(
            patternFunction, 
            new EchoingHandler(),
            new CalculatorHandler()
        ).export();

        Thread.sleep(1000 * 60 * 5); //keeps process alive for 5 mins
    }

    @Handler("Echo Values")
    public static class EchoingHandler {
        @Step(".*accept a string with value '(.*)'")
        public void accept(String s) {
            System.out.println(s);
        }
    }
}
