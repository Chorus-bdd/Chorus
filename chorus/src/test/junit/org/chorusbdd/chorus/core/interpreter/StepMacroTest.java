package org.chorusbdd.chorus.core.interpreter;

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
        stepMacro.addStep(new StepToken("Given", "my number of steps is <$1> steps"));
        stepMacro.addStep(new StepToken("Then", "my number of groups is <$2> groups"));

        StepToken scenarioStepToken = new StepToken("Given", "I reference a step macro with 2 steps and 2 capture groups");
        stepMacro.processStep(scenarioStepToken, Collections.singletonList(stepMacro));

        assertEquals(scenarioStepToken.getChildSteps().size(), 2);
        assertEquals("my number of steps is 2 steps", scenarioStepToken.getChildSteps().get(0).getAction());
        assertEquals("my number of groups is 2 groups", scenarioStepToken.getChildSteps().get(1).getAction());
    }

    @Test(expected=RecursiveStepMacroException.class)
    public void testMaxStepDepthForRecursiveMatching() {
        StepMacro stepMacro = new StepMacro("I have a recursive match");
        stepMacro.addStep(new StepToken("Given", "I have a recursive match"));

        StepToken scenarioStepToken = new StepToken("Given", "I have a recursive match");
        stepMacro.processStep(scenarioStepToken, Collections.singletonList(stepMacro));
    }

    @Test
    //test that a steps from a step macro themselves matched to the list of step macros recursively, creating
    //a tree structure of steps
    //any capture groups in the step macro steps should be expanded before this matching occurs
    public void testRecursiveMatching()  {
        StepMacro stepMacro = new StepMacro("I have a parent (.*) macro");
        stepMacro.addStep(new StepToken("", "With a step which matches a child <$1> macro"));

        StepMacro childStepMacro = new StepMacro("With a step which matches a child step macro");
        childStepMacro.addStep(new StepToken("", "Child Step"));

        StepToken scenarioStepToken = new StepToken("Given", "I have a parent step macro");
        stepMacro.processStep(scenarioStepToken, Arrays.asList(stepMacro, childStepMacro));

        assertEquals(scenarioStepToken.getChildSteps().size(), 1);
        assertEquals("With a step which matches a child step macro", scenarioStepToken.getChildSteps().get(0).getAction());

        assertEquals(scenarioStepToken.getChildSteps().get(0).getChildSteps().size(), 1);
        assertEquals("Child Step", scenarioStepToken.getChildSteps().get(0).getChildSteps().get(0).getAction());
    }

    @Test
    public void testMismatchedCaptureGroups() {
        StepMacro stepMacro = new StepMacro("I reference a step macro with (\\d+) steps and (\\d+) capture groups");
        stepMacro.addStep(new StepToken("Given", "my number of steps is <$3> steps"));

        StepToken scenarioStepToken = new StepToken("Given", "I reference a step macro with 1 steps and 2 capture groups");
        try {
            stepMacro.processStep(scenarioStepToken, Collections.singletonList(stepMacro));
            fail("Expecting mismatched groups exception");
        } catch (ChorusException e) {
            assertEquals("Capture group with index 3 in StepMacro step 'my number of steps is <$3> steps' did not have a " +
                    "matching capture group in the pattern 'I reference a step macro with (\\d+) steps " +
                    "and (\\d+) capture groups'", e.getMessage());
        }
    }


}
