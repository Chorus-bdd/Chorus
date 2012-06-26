package failedstep;

import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.util.assertion.ChorusAssertionError;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 14/06/12
 * Time: 09:21
 */
@Handler("Failed Step")
public class FailedStepHandler {

    @Step("Chorus is working properly")
    public void isWorkingProperly() {
    }

    @Step("I can run a feature with a single scenario")
    public void canRunAFeature() {
    }

    @Step("if a step fails")
    public void ifAStepFails() {
        throw new ChorusAssertionError("This step threw an exception to fail it");
    }

    @Step("the subsequent step is skipped")
    public void thenNextStepIsSkipped() {
    }


}
