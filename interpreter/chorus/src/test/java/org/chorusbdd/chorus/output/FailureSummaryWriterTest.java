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
package org.chorusbdd.chorus.output;

import org.chorusbdd.chorus.results.*;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;

/**
 * Created by nickebbutt on 18/03/2018.
 */
public class FailureSummaryWriterTest extends AbstractOutputWriterTest {
    FailureSummaryWriter failureSummaryWriter = new FailureSummaryWriter();
    
    List<FeatureToken> listOfFeatures = new LinkedList<>();
    
    private String expected = "Failure Summary:\n" +
            "\n" +
            "  Feature One\n" +
            "    Test Scenario\n" +
            "       If I create a second step >\n" +
            "         If I create a step macro child step >\n" +
            "           If I create a step grandchild - FAILED (Failed due to a horrific and unforseen cataclysm)\n" +
            "\n" +
            "  Feature Two\n" +
            "    Scenario Two\n" +
            "       If I add a step to scenario 2 - UNDEFINED \n" +
            "\n" +
            "  Feature Three\n" +
            "    Scenario Three\n" +
            "       If I add a step to scenario 3 - TIMEOUT \n" +
            "\n" +
            "  Feature Four Failed No Handler\n";
    @Before
    public void doBefore() {
        createFeatureOne();
        createFeatureTwo();
        createFeatureThree();
        createFeatureFour();
    }

    private void createFeatureFour() {
        ScenarioToken scenarioFour = new ScenarioToken();
        scenarioFour.setName("Scenario Four");

        StepToken step = scenarioFour.addStep(StepToken.createStep("If", "I add a step to scenario 3"));
        step.setEndState(StepEndState.SKIPPED);
        
        FeatureToken featureFour = new FeatureToken();
        featureFour.setName("Feature Four Failed No Handler");
        featureFour.addScenario(scenarioFour);
        listOfFeatures.add(featureFour);
    }

    private void createFeatureThree() {
        ScenarioToken scenarioThree = new ScenarioToken();
        scenarioThree.setName("Scenario Three");
        StepToken stepSix = scenarioThree.addStep(StepToken.createStep("If", "I add a step to scenario 3"));
        stepSix.setEndState(StepEndState.TIMEOUT);

        StepToken stepSeven = scenarioThree.addStep(StepToken.createStep("If", "I add another skipped step to scenario 3"));
        stepSeven.setEndState(StepEndState.SKIPPED);

        FeatureToken featureThree = new FeatureToken();
        featureThree.setName("Feature Three");
        featureThree.addScenario(scenarioThree);
        listOfFeatures.add(featureThree);
    }

    private void createFeatureTwo() {
        ScenarioToken scenarioTwo = new ScenarioToken();
        scenarioTwo.setName("Scenario Two");
        StepToken stepFour = scenarioTwo.addStep(StepToken.createStep("If", "I add a step to scenario 2"));
        stepFour.setEndState(StepEndState.UNDEFINED);

        StepToken stepFive = scenarioTwo.addStep(StepToken.createStep("If", "I add another skipped step to scenario 2"));
        stepFive.setEndState(StepEndState.SKIPPED);

        FeatureToken featureTwo = new FeatureToken();
        featureTwo.setName("Feature Two");
        featureTwo.addScenario(scenarioTwo);
        listOfFeatures.add(featureTwo);
    }

    private void createFeatureOne() {
        ScenarioToken scenarioToken = new ScenarioToken();
        scenarioToken.setName("Test Scenario");
        StepToken stepOne = scenarioToken.addStep(StepToken.createStep("If", "I create a step"));
        stepOne.setEndState(StepEndState.PASSED);

        StepToken stepTwo = scenarioToken.addStep(StepToken.createStep("If", "I create a second step"));
        stepTwo.setEndState(StepEndState.FAILED);

        StepToken stepChild = StepToken.createStep("If", "I create a step macro child step");
        stepChild.setEndState(StepEndState.FAILED);
        stepTwo.addChildStep(stepChild);

        StepToken stepGrandchild = StepToken.createStep("If", "I create a step grandchild");
        stepGrandchild.setEndState(StepEndState.FAILED);
        stepGrandchild.setMessage("Failed due to a horrific and unforseen cataclysm");
        stepChild.addChildStep(stepGrandchild);

        StepToken peerStep = StepToken.createStep("If", "I create a peer step which was skipped");
        peerStep.setEndState(StepEndState.SKIPPED);
        stepChild.addChildStep(peerStep);

        FeatureToken featureToken = new FeatureToken();
        featureToken.setName("Feature One");
        featureToken.addScenario(scenarioToken);
        listOfFeatures.add(featureToken);
    }

    @Test
    public void testFailedStepOutput() {
        String output = captureOutput(this::writeTestOutput);
        assertEquals(expected, output);
    }
    
    protected void writeTestOutput(Consumer<String> println) {
        failureSummaryWriter.printFailureSummary(listOfFeatures, println);
    }
    

}