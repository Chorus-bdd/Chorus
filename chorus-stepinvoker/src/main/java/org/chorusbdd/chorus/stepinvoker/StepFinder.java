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
package org.chorusbdd.chorus.stepinvoker;

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
* Created with IntelliJ IDEA.
* User: nick
* Date: 16/05/12
* Time: 22:07
*
* Find a matching step method to call from a List of handler classes and create a StepInvoker to call it
*
*/
public class StepFinder {

    private static ChorusLog log = ChorusLogFactory.getLog(StepFinder.class);

    private List<StepInvoker> stepInvokers;
    private String stepAction;
    private StepInvoker chosenStepInvoker;
    private List<String> invokerArgs;

    public StepFinder(List<StepInvoker> stepInvokers, String stepAction) {
        this.stepInvokers = stepInvokers;
        this.stepAction = stepAction;
    }

    public StepInvoker getChosenStepInvoker() {
        return chosenStepInvoker;
    }

    public List<String> getInvokerArgs() {
        return invokerArgs;
    }

    public StepFinder findStepMethod() {
        log.debug("Finding step method...");

        for ( StepInvoker i : stepInvokers) {
            checkForMatch(i);
        }
        return this;
    }

    private void checkForMatch(StepInvoker invoker) {
        log.debug("Regex to match is [" + invoker.getStepPattern() + "] and action is [" + stepAction + "]");
        Matcher matcher = invoker.getStepPattern().matcher(stepAction);
        if (matcher.matches()) {
            int groupCount = matcher.groupCount();

            //collect the regex group values
            List<String> regexGroupValues = new ArrayList<>();
            for (int i = 0; i < groupCount; i++) {
                regexGroupValues.add(matcher.group(i + 1));
            }
            foundStepInvoker(invoker, regexGroupValues);
        }
    }

    private void foundStepInvoker(StepInvoker stepInvoker, List<String> values) {
        if ( log.isTraceEnabled() ) log.trace("Matched! " + stepInvoker + "," + values);
        if (chosenStepInvoker == null) {
            this.invokerArgs = values;
            this.chosenStepInvoker = stepInvoker;
        } else {
            log.info(String.format("Ambiguous step [%s], more than one implementation, will use [%s] not [%s]",
            stepAction,
            this.chosenStepInvoker.getTechnicalDescription(),
            stepInvoker.getTechnicalDescription()));
        }
    }

    public boolean isMethodAvailable() {
        return chosenStepInvoker != null;
    }

    public boolean stepWasFound() {
        return chosenStepInvoker != null;
    }
}
