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


