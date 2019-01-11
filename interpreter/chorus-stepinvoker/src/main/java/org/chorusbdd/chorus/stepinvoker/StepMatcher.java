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

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.util.ChorusException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
* Created with IntelliJ IDEA.
* User: nick
* Date: 16/05/12
* Time: 22:07
*
* Find a StepInvoker which matches a step action, and pick out any step arguments corresponding to capture groups
* in the step pattern
*/
public class StepMatcher {

    private ChorusLog log = ChorusLogFactory.getLog(StepMatcher.class);

    private List<StepInvoker> stepInvokers;
    private String stepAction;
    private StepInvoker chosenStepInvoker;
    private List<String> invokerArgs;
    private StepMatchResult stepMatchResult = StepMatchResult.STEP_NOT_FOUND;
    private ChorusException matchException;

    public StepMatcher(List<StepInvoker> stepInvokers, String stepAction) {
        this.stepInvokers = stepInvokers;
        this.stepAction = stepAction;
    }

    public StepInvoker getFoundStepInvoker() {
        return chosenStepInvoker;
    }

    public List<String> getInvokerArgs() {
        return invokerArgs;
    }

    public StepMatcher findStepMethod() {
        reset();
        log.debug("Finding step method...");

        try {
            for ( StepInvoker i : stepInvokers) {
                checkForMatch(i);
            }
        } catch (DuplicateStepMatchException e) {
            stepMatchResult = StepMatchResult.DUPLICATE_MATCH_ERROR;
            this.matchException = e;
        }
        return this;
    }

    private void checkForMatch(StepInvoker invoker) throws DuplicateStepMatchException {
        if ( log.isTraceEnabled()) {
            log.trace("Regex to match is [" + invoker.getStepPattern() + "] and action is [" + stepAction + "]");
        }
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

    private void foundStepInvoker(StepInvoker stepInvoker, List<String> stepArguments) throws DuplicateStepMatchException {
        if ( log.isTraceEnabled() ) {
            log.trace("Matched! " + stepInvoker + "," + stepArguments);
        }
        if (chosenStepInvoker == null) {
            this.invokerArgs = stepArguments;
            this.chosenStepInvoker = stepInvoker;
            this.stepMatchResult = StepMatchResult.STEP_FOUND;
        } else {
            throw new DuplicateStepMatchException(
                String.format("Ambiguous step [%s], more than one implementation (%s / %s)",
                    stepAction,
                    this.chosenStepInvoker.getTechnicalDescription(),
                    stepInvoker.getTechnicalDescription()
                )
            );
        }
    }

    private void reset() {
        stepMatchResult = StepMatchResult.STEP_NOT_FOUND;
        matchException = null;
        chosenStepInvoker = null;
    }

    public StepMatchResult getStepMatchResult() {
        return stepMatchResult;
    }

    public ChorusException getMatchException() {
        return matchException;
    }

}
