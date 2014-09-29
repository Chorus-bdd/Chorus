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
package org.chorusbdd.chorus.core.interpreter.interpreter;

import org.chorusbdd.chorus.core.interpreter.invoker.StepInvoker;
import org.chorusbdd.chorus.core.interpreter.invoker.StepInvokerProvider;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.results.StepToken;

/**
* Created with IntelliJ IDEA.
* User: nick
* Date: 16/05/12
* Time: 22:07
*
* Find a matching step method to call from a List of handler classes and create a StepInvoker to call it
*
*/
class StepDefinitionMethodFinder {

    private static ChorusLog log = ChorusLogFactory.getLog(StepDefinitionMethodFinder.class);

    private StepInvokerProvider stepInvokerProvider;
    private StepToken step;
    private StepInvoker chosenStepInvoker;
    private Object[] invokerArgs;

    public StepDefinitionMethodFinder(StepInvokerProvider stepInvokerProvider, StepToken step) {
        this.stepInvokerProvider = stepInvokerProvider;
        this.step = step;
    }

    public StepInvoker getChosenStepInvoker() {
        return chosenStepInvoker;
    }

    public Object[] getInvokerArgs() {
        return invokerArgs;
    }

    public StepDefinitionMethodFinder findStepMethod() {
        log.debug("Finding step method...");

        for ( StepInvoker i : stepInvokerProvider.getStepInvokerList()) {
            checkForMatch(i);
        }
        return this;
    }

    private void checkForMatch(StepInvoker invoker) {
        String action = step.getAction();

        log.debug("Regex to match is [" + invoker.getStepPattern() + "] and action is [" + action + "]");
        Object[] values = StepMatcher.extractGroupsAndCheckMethodParams(invoker, action);
        if (values != null) { //the regexp matched the action and the method's parameters
            foundStepMethod(invoker, values);
        }
    }

    private void foundStepMethod(StepInvoker stepInvoker, Object[] values) {
        log.trace("Matched!");
        if (chosenStepInvoker == null) {
            this.invokerArgs = values;
            this.chosenStepInvoker = stepInvoker;
        } else {
            log.info(String.format("Ambiguous method (%s) found for step (%s) will use first method found (%s)",
            stepInvoker,
            step,
            this.chosenStepInvoker));
        }
    }

    public boolean isMethodAvailable() {
        return chosenStepInvoker != null;
    }

}
