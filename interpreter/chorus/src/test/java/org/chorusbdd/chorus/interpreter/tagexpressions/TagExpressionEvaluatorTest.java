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
package org.chorusbdd.chorus.interpreter.tagexpressions;

import org.chorusbdd.chorus.pathscanner.TagExpressionEvaluator;
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

        abcScenarioTags = new ArrayList<>();
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
