package handlerpriority;

import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.util.assertion.ChorusAssert;
import org.chorusbdd.chorus.util.assertion.ChorusAssertionError;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 14/06/12
 * Time: 09:21
 */
@Handler("Handler Priority Three")
public class HandlerPriorityHandlerThree {

    @Step("Chorus is working properly")
    public void isWorkingProperly() {
    }

    @Step("steps in all three handlers should be handled by (.*)")
    public void stepsInAllThreeHandlers(String expectedHandler) {
        String actualHandler = getClass().getAnnotation(Handler.class).value();
        ChorusAssert.assertEquals(actualHandler, expectedHandler);
    }

    @Step("steps in just handler two and three should be handled by (.*)")
    public void stepsInJustTwoAndThree(String expectedHandler) {
        String actualHandler = getClass().getAnnotation(Handler.class).value();
        ChorusAssert.assertEquals(actualHandler, expectedHandler);
    }

}
