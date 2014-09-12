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
package org.chorusbdd.chorus.core.interpreter.invoker;

import org.chorusbdd.chorus.annotations.PassesWithin;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
* User: nick
* Date: 24/09/13
* Time: 18:46
*/
public class StepMethodInvokerFactory {

    private static ChorusLog log = ChorusLogFactory.getLog(StepMethodInvokerFactory.class);

    public StepInvoker createInvoker(Object handlerInstance, Method method) {
        Annotation[] annotations = method.getDeclaredAnnotations();

        Step stepAnnotation = method.getAnnotation(Step.class);
        Pattern stepPattern = Pattern.compile(stepAnnotation.value());
        String pendingText = stepAnnotation.pending();
        StepInvoker simpleMethodInvoker = new SimpleMethodInvoker(handlerInstance, method, stepPattern, pendingText);

        //if the step is annotated with @PassesWithin then we wrap the simple invoker with the appropriate
        //PolledInvoker
        for ( Annotation a : annotations) {
            if ( a.annotationType() == PassesWithin.class) {
                PassesWithin passesWithin = (PassesWithin) a;
                switch(passesWithin.pollMode()) {
                    case UNTIL_FIRST_PASS:
                        simpleMethodInvoker = new UntilFirstPassInvoker(simpleMethodInvoker, passesWithin);
                        break;
                    case PASS_THROUGHOUT_PERIOD:
                        simpleMethodInvoker =  new PassesThroughoutInvoker(simpleMethodInvoker, passesWithin);
                        break;
                    default:
                        throw new UnsupportedOperationException("Unknown mode " + passesWithin.pollMode());
                }
            }
        }
        return simpleMethodInvoker;
    }
}
