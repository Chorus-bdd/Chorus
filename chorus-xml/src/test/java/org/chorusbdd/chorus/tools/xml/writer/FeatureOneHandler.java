package org.chorusbdd.chorus.tools.xml.writer;

import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Step;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 18/12/12
 * Time: 18:08
 *
 * The step handlers in this class are stubs which do nothing since the only purpose of running the
 * feature is to generate execution tokens which we can then render as XML
 */
@Handler("Feature One")
public class FeatureOneHandler {

    @Step("I have a simple scenario")
    public void testIHaveSimpleScenario() {
    }

    @Step("this contains some steps")
    public void testContainsSteps() {
    }

    @Step("I can use this to generate tokens which I can access in my junit xml writers")
    public void testGenerateTokens() {
    }
}
