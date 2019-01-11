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
package org.chorusbdd.chorus.stepinvoker;

import java.util.List;
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
     * This allows us to distinguish between a remote step returning null and a remote step defining a void return type
     */
    public static final String VOID_RESULT = "STEP_INVOKER_VOID_RESULT";

    /**
     * @return a Pattern which is matched against step text/action to determine whether this invoker matches a scenario step
     */
    Pattern getStepPattern();

    /**
     * @return true if this step is 'pending'
     */
    boolean isPending();

    /**
     * @return a pending message if the step is pending, or null if the step is not pending
     */
    String getPendingMessage();

    /**
     * Invoke the step passing the String values from the step action which match the capture groups in the step pattern
     *
     * @param stepTokenId, A unique ID of the step being executed, this ID is unique for each test run
     * @param args A list of arguments, one for each capturing group in the step pattern, extracted from the step text/action             
     * @return the result returned by the step method, or VOID_RESULT if the step method has a void return type
     */
    Object invoke(String stepTokenId, List<String> args) throws Exception;


    /**
     * Get the StepRetry, which defines whether a step should fail instantly or be retried for a period if a failure occurs
     *
     * @return StepRetry
     */
    StepRetry getRetry();

    /**
     * The id is used to uniquely identify a step invoker, since it's possible the Pattern is duplicated by several step providers
     * (this would cause an error if we match a step against the pattern)
     *
     * @return a String id for this step invoker, which should be unique and final
     */
    String getId();

    /**
     * The technical description may be used to
     *
     * @return a short description of the step implementation (e.g class and method name)
     */
    String getTechnicalDescription();

    
    /**
     * @return A Category into which to group steps for documentation and step cataloging
     */
    String getCategory();

    /**
     * 
     * @return true if this step is deprecated which implies that support for it may be discontinued in future releases
     */
    boolean isDeprecated();

}
