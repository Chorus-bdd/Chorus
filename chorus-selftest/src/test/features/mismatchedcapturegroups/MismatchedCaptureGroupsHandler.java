package mismatchedcapturegroups;

import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.util.assertion.ChorusAssertionError;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 14/06/12
 * Time: 09:21
 */
@Handler("Mismatched Capture Groups")
public class MismatchedCaptureGroupsHandler {

    @Step("Chorus is working properly")
    public void isWorkingProperly() {
    }

    @Step("I can run a step in scenario (.*)")
    public void canRunAStep(String parameter) {
    }

    @Step("if a step fails")
    public void ifAStepFails() {
        throw new ChorusAssertionError("This step threw an exception to fail it");
    }

    @Step("the subsequent steps are skipped")
    public void theSubsequentStepsAreSkipped() {
    }


}
