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

import java.lang.reflect.InvocationTargetException;
import java.util.regex.Pattern;

/**
 * User: nick
 * Date: 20/09/13
 * Time: 09:09
 *
 * A StepInvoker represents some step logic with a Pattern which can be matched against text in a Scenario step
 * The invoker may be used to execute the step, if the pattern matches
 *
 * Step handler methods annotated with @Step may be exposed as StepInvoker instances, in which case we use
 * reflection to invoke the method on the handler, but StepInvoker instances may also represent a proxy for
 * remote steps, or rely on some other mechanism to runs step logic.
 */
public interface StepInvoker {

    /**
     * A special String which represents the result of calling a method which had a void return type
     */
    public static final String VOID_RESULT = "STEP_INVOKER_VOID_RESULT";

    /**
     * @return a Pattern which is matched against step text/action to determine whether this invoker matches a scenario step
     */
    Pattern getStepPattern();

    /**
     * @return true if this step is 'pending' (a placeholder for future implementation) and should not be invoked
     */
    boolean isPending();

    /**
     * @return a pending message if the step is pending, or null if the step is not pending
     */
    String getPendingMessage();

    /**
     * Invoke the method
     *
     * @return the result returned by the step method, or VOID_RESULT if the step method has a void return type
     */
    Object invoke(Object... args) throws IllegalAccessException, InvocationTargetException;

    /**
     * @return a String id for this step invoker, which should be unique
     */
    String getId();
}
