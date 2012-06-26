package undefinedstep;

import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.util.assertion.ChorusAssertionError;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 14/06/12
 * Time: 09:21
 */
@Handler("Undefined Step")
public class UndefinedStepHandler {

    @Step("Chorus is working properly")
    public void isWorkingProperly() {
    }

    @Step("I can run a feature with a single scenario")
    public void canRunAFeature() {
    }

    //@Step("if a step is not implemented")
    //public void ifAStepIsNotImplemented() {
    //}

    @Step("the subsequent step is skipped")
    public void thenNextStepIsSkipped() {
    }


}
