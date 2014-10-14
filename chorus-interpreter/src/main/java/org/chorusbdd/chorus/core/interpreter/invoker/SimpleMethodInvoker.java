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
package org.chorusbdd.chorus.core.interpreter.invoker;

import org.chorusbdd.chorus.annotations.Step;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
* User: nick
* Date: 24/09/13
* Time: 18:46
*/
public class SimpleMethodInvoker extends AbstractStepMethodInvoker {

    private final Class[] parameterTypes;
    private final String pendingMessage;
    private final boolean isPending;
    private final Pattern stepPattern;

    public SimpleMethodInvoker(Object classInstance, Method method, Pattern stepPattern) {
        this(classInstance, method, stepPattern, null);
    }

    public SimpleMethodInvoker(Object classInstance, Method method, Pattern stepPattern, String pendingMessage) {
        super(classInstance, method);
        this.stepPattern = stepPattern;
        this.parameterTypes = method.getParameterTypes();
        this.pendingMessage = pendingMessage;
        this.isPending = ! Step.NO_PENDING_MESSAGE.equals(pendingMessage);
    }

    /**
     * @return a Pattern which is matched against step text/action to determine whether this invoker matches a scenario step
     */
    public Pattern getStepPattern() {
        return stepPattern;
    }

    /**
     * Chorus needs to extract values from the matched pattern and pass them as parameters when invoking the step
     *
     * @return an array of parameter types the length of which should equal the number of capture groups in the step pattern
     */
    public Class[] getParameterTypes() {
        return parameterTypes;
    }

    /**
     * @return true if this step is 'pending' (a placeholder for future implementation) and should not be invoked
     */
    public boolean isPending() {
        return isPending;
    }

    public String getPendingMessage() {
        return pendingMessage;
    }



    public Object invoke(Object... args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Object result =  getMethod().invoke(getClassInstance(), args);
        result = handleResultIfReturnTypeVoid(getMethod(), result);
        return result;
    }

}
