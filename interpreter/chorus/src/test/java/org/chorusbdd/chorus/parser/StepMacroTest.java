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
package org.chorusbdd.chorus.parser;

import junit.framework.Assert;
import org.chorusbdd.chorus.results.StepToken;
import org.chorusbdd.chorus.util.ChorusException;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 23/02/13
 * Time: 21:34
 *
 * Test the matching of step macros to scenario steps, and the recursive addition of
 * step macro steps to a scenario step
 */
public class StepMacroTest extends Assert {

    @Test
    public void testStepMacroPatternMatch() {
        StepMacro stepMacro = new StepMacro("I reference a step macro with (\\d+) steps and (\\d+) capture groups");
        stepMacro.addStep(StepToken.createStep("Given", "my number of steps is <$1> steps"));
        stepMacro.addStep(StepToken.createStep("Then", "my number of groups is <$2> groups"));

        StepToken scenarioStepToken = StepToken.createStep("Given", "I reference a step macro with 2 steps and 2 capture groups");
        stepMacro.processStep(scenarioStepToken, Collections.singletonList(stepMacro), false);

        assertEquals(scenarioStepToken.getChildSteps().size(), 2);
        assertEquals("my number of steps is 2 steps", scenarioStepToken.getChildSteps().get(0).getAction());
        assertEquals("my number of groups is 2 groups", scenarioStepToken.getChildSteps().get(1).getAction());
    }

    @Test(expected=RecursiveStepMacroException.class)
    public void testMaxStepDepthForRecursiveMatching() {
        StepMacro stepMacro = new StepMacro("I have a recursive match");
        stepMacro.addStep(StepToken.createStep("Given", "I have a recursive match"));

        StepToken scenarioStepToken = StepToken.createStep("Given", "I have a recursive match");
        stepMacro.processStep(scenarioStepToken, Collections.singletonList(stepMacro), false);
    }

    @Test
    //test that a steps from a step macro themselves matched to the list of step macros recursively, creating
    //a tree structure of steps
    //any capture groups in the step macro steps should be expanded before this matching occurs
    public void testRecursiveMatching()  {
        StepMacro stepMacro = new StepMacro("I have a parent (.*) macro");
        stepMacro.addStep(StepToken.createStep("", "With a step which matches a child <$1> macro"));

        StepMacro childStepMacro = new StepMacro("With a step which matches a child step macro");
        childStepMacro.addStep(StepToken.createStep("", "Child Step"));

        StepToken scenarioStepToken = StepToken.createStep("Given", "I have a parent step macro");
        stepMacro.processStep(scenarioStepToken, Arrays.asList(stepMacro, childStepMacro), false);

        assertEquals(scenarioStepToken.getChildSteps().size(), 1);
        assertEquals("With a step which matches a child step macro", scenarioStepToken.getChildSteps().get(0).getAction());

        assertEquals(scenarioStepToken.getChildSteps().get(0).getChildSteps().size(), 1);
        assertEquals("Child Step", scenarioStepToken.getChildSteps().get(0).getChildSteps().get(0).getAction());
    }

    @Test
    public void testMismatchedCaptureGroups() {
        StepMacro stepMacro = new StepMacro("I reference a step macro with (\\d+) steps and (\\d+) capture groups");
        stepMacro.addStep(StepToken.createStep("Given", "my number of steps is <$3> steps"));

        StepToken scenarioStepToken = StepToken.createStep("Given", "I reference a step macro with 1 steps and 2 capture groups");
        try {
            stepMacro.processStep(scenarioStepToken, Collections.singletonList(stepMacro), false);
            fail("Expecting mismatched groups exception");
        } catch (ChorusException e) {
            assertEquals("Capture group with index 3 in StepMacro step 'my number of steps is <$3> steps' did not have a " +
                    "matching capture group in the pattern 'I reference a step macro with (\\d+) steps " +
                    "and (\\d+) capture groups'", e.getMessage());
        }
    }

    @Test
    public void testReplaceGroupVariables() {
        StepMacro s = new StepMacro("My stepmacro with variable <myvar> and variable <mysecondvar>");
        assertEquals("My stepmacro with variable (.+) and variable (.+)", s.getPattern().toString());
        assertEquals(1, s.getGroupVariable("<myvar>"));
        assertEquals(2, s.getGroupVariable("<mysecondvar>"));
    }


}
