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

    @Step("I pass a parameter (.*) matched by a regex with one capture group but no method params")
    public void canRunAStepMissingOneParam() {
    }

    @Step("I pass a parameter (.*) matched by a regex with (.*) capture groups but just one param")
    public void canRunAStepMissingOneParam(String justOneParam) {
    }

    @Step("the subsequent steps are skipped")
    public void theSubsequentStepsAreSkipped() {
    }

}
