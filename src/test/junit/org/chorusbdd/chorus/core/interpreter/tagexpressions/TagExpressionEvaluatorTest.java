package org.chorusbdd.chorus.core.interpreter.tagexpressions;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by: Steve Neal
 * Date: 20/01/12
 */
public class TagExpressionEvaluatorTest {

    private TagExpressionEvaluator evaluatorUnderTest;

    private List<String> abcScenarioTags;

    @Before
    public void init() {
        evaluatorUnderTest = new TagExpressionEvaluator();

        abcScenarioTags = new ArrayList<String>();
        abcScenarioTags.add("@a");
        abcScenarioTags.add("@b");
        abcScenarioTags.add("@c");
    }

    @Test
    public void testAllTagsPresent() {
        //all these tags (regardlesss of the irregular spacing) are all present on the scenario so run it
        boolean run = evaluatorUnderTest.shouldRunScenarioWithTags("  @a   @b @c ", abcScenarioTags);
        assertTrue(run);
    }

    @Test
    public void testSomeTagsPresent() {
        //tags b and c are present on the scenario so run it
        boolean execute = evaluatorUnderTest.shouldRunScenarioWithTags("@b @c", abcScenarioTags);
        assertTrue(execute);
    }

    @Test
    public void testExtraneousTag() {
        //tag d is not present on the scenario so don't run it
        boolean execute = evaluatorUnderTest.shouldRunScenarioWithTags("@b @c @d", abcScenarioTags);
        assertFalse(execute);
    }

    @Test
    public void checkWithNegatedTag() {
        //tag c is present on the scenario so don't run it
        boolean execute = evaluatorUnderTest.shouldRunScenarioWithTags("@a @b !@c", abcScenarioTags);
        assertFalse(execute);
    }

    @Test
    public void checkSimpleExpressionWithOr() {
        //tag d is not available on the scenario but a is so run it
        boolean execute = evaluatorUnderTest.shouldRunScenarioWithTags("@d | @a", abcScenarioTags);
        assertTrue(execute);
    }

    @Test
    public void checkExtraneousTagWithOr() {
        //tag d does is not available on the scenario so don't run it
        boolean execute = evaluatorUnderTest.shouldRunScenarioWithTags("@a @d | @b @d", abcScenarioTags);
        assertFalse(execute);
    }

}
