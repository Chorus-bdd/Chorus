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
package org.chorusbdd.chorus.tools.mocksuite.mocksuiteone;

import junit.framework.Assert;
import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.stepinvoker.StepPendingException;

/**
 * User: nick
 * Date: 08/01/13
 * Time: 08:53
 */
@Handler("Web Agent Self Test")
public class WebAgentSelfTestHandler {

    @Step("I run a scenario with several steps")
    public void runAScenarioWithSeveralSteps() {

    }

    @Step("a step fails an assertion")
    public void failAnAssertion() {
        Assert.assertTrue("Fail an assertion", false);
    }

    @Step("chorus scenario timeout is set to 2 seconds")
    public void timeoutSet() {
    }

    @Step("I wait for twelve seconds for timeout")
    public void waitForFour() throws InterruptedException {
        Thread.sleep(12000);
    }

    @Step(value = "one of the steps is marked pending", pending = "here be a pending message")
    public void stepMarkedPending() {
    }
    
    @Step(value = "one of the steps throws a pending exception")
    public void stepThrowsPending() {
        throw new StepPendingException("This one Pending");
    }

    @Step("I run a feature with a single test scenario which passes")
    public void runAFeatureWhichPasses() {
    }

    @Step("the scenario passes and the feature passes")
    public void scenarioPasses() {
    }

    @Step("I run step (.*)")
    public String runAStep(String stepName) {
        return stepName;
    }
}


