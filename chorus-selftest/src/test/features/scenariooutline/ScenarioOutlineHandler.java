package scenariooutline;

import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Step;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 14/06/12
 * Time: 09:21
 */
@Handler("Scenario Outline")
public class ScenarioOutlineHandler {

    @Step("Chorus is working properly")
    public void isWorkingProperly() {

    }

    @Step("I can run a step with value (.*)")
    public String canRunAFeature(String value) {
        return value;
    }

    @Step("I can run a step with numeric value (.*)")
    public Double canRunAFeature(Double value) {
        return value;
    }

    @Step("value (?:.*) is not used")
    public void canRunAFeature() {
    }


}
