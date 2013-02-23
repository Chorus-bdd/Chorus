package org.chorusbdd.chorus.results;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * User: nick
 * Date: 17/01/13
 * Time: 18:22
 */
public class TestSuiteTest extends Assert {

    private TestSuite testSuite;
    private ScenarioToken scenarioToken;
    private ScenarioToken scenarioTwo;
    private StepToken stepOne;
    private StepToken stepTwo;
    private StepToken stepThree;
    private FeatureToken featureToken;
    private FeatureToken featureTwo;
    private ExecutionToken executionToken;

    @Before
    public void doBefore() {
        scenarioToken = new ScenarioToken();
        scenarioToken.setName("Test Scenario");
        stepOne = scenarioToken.addStep(new StepToken("If", "I create a step"));
        stepTwo = scenarioToken.addStep(new StepToken("If", "I create a second step"));

        scenarioTwo = new ScenarioToken();
        stepThree = scenarioTwo.addStep(new StepToken("If", "I add a step to scenario 2"));
        featureToken = new FeatureToken();
        featureToken.addScenario(scenarioToken);
        featureToken.addScenario(scenarioTwo);

        featureTwo = new FeatureToken();

        executionToken = new ExecutionToken("My test suite name");
        testSuite = new TestSuite(executionToken, Arrays.asList(featureToken, featureTwo));
    }

    @Test
    public void testVisitation() {
        MockVisitor m = new MockVisitor();
        testSuite.accept(m);
        assertEquals("Expected visit count", 9, m.visitations);
    }

    private class MockVisitor extends Assert implements TokenVisitor {

        private int visitations;

        private List<Token> expectedTokenOrder = new LinkedList<Token>(
        Arrays.asList(
            (Token)executionToken, executionToken.getResultsSummary(), featureToken, scenarioToken, stepOne, stepTwo,
            scenarioTwo, stepThree, featureTwo
        ));

        public void visit(ExecutionToken executionToken) {
            checkExpectedToken(executionToken);
        }

        private void checkExpectedToken(Token token) {
            Token t = expectedTokenOrder.remove(0);
            assertSame(token, t);
            visitations++;
        }

        public void visit(ResultsSummary resultsSummary) {
            checkExpectedToken(resultsSummary);
        }

        public void visit(FeatureToken featureToken) {
            checkExpectedToken(featureToken);
        }

        public void visit(ScenarioToken scenarioToken) {
            checkExpectedToken(scenarioToken);
        }

        public void visit(StepToken stepToken) {
            checkExpectedToken(stepToken);
        }
    }
}
