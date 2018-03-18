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
public class FailedStepsWriterTest extends AbstractOutputWriterTest {
        FailedStepsWriter failedStepsWriter = new FailedStepsWriter();
    
    List<FeatureToken> listOfFeatures = new LinkedList<>();
    
    private String expected = "Failed Steps:\n" +
            "\n" +
            "  Feature One >\n" +
            "    Test Scenario >\n" +
            "       If I create a second step >\n" +
            "         If I create a step macro child step >\n" +
            "           If I create a step grandchild - FAILED (Failed due to a horrific and unforseen cataclysm)\n" +
            "\n" +
            "  Feature Two >\n" +
            "    Scenario Two >\n" +
            "       If I add a step to scenario 2 - UNDEFINED \n" +
            "\n" +
            "  Feature Three >\n" +
            "    Scenario Three >\n" +
            "       If I add a step to scenario 3 - TIMEOUT \n";

    @Before
    public void doBefore() {
        createFeatureOne();
        createFeatureTwo();
        createFeatureThree();
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
        failedStepsWriter.printFailedSteps(listOfFeatures, println);
    }
    

}