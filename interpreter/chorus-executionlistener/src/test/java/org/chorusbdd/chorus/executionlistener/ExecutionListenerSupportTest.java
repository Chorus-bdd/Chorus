package org.chorusbdd.chorus.executionlistener;

import org.chorusbdd.chorus.annotations.ExecutionPriority;
import org.chorusbdd.chorus.results.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

/**
 * Created by nickebbutt on 20/03/2018.
 */
@RunWith(MockitoJUnitRunner.class)
public class ExecutionListenerSupportTest {
    
    private ExecutionListenerSupport executionListenerSupport = new ExecutionListenerSupport();
    
    @Mock
    private ExecutionListener mockListener;
    
    @Mock
    private ExecutionToken executionToken;
    @Mock
    private ScenarioToken scenarioToken;
    @Mock
    private StepToken stepToken;
    @Mock
    private FeatureToken featureToken;
    
    private ExecutionListener priorityOneHundred = new PriorityOneHundred();
    private ExecutionListener priorityTwoHundred = new PriorityTwoHundred();
    private ExecutionListener priorityThreeHundred = new PriorityThreeHundred();
    private List<String> executions = new LinkedList<>();
    
    private List<FeatureToken> featureList;

    @Before
    public void setUp() throws Exception {
        featureList = asList(featureToken);
    }

    @Test
    public void whenIAddAnExecutionListenerItsLifecycleMethodsAreCalled() {
        executionListenerSupport.addExecutionListeners(mockListener);
       
        executionListenerSupport.notifyTestsStarted(executionToken, featureList);
        executionListenerSupport.notifyFeatureStarted(executionToken, featureToken);
        executionListenerSupport.notifyScenarioStarted(executionToken, scenarioToken);
        executionListenerSupport.notifyStepStarted(executionToken, stepToken);
        executionListenerSupport.notifyStepCompleted(executionToken, stepToken);
        executionListenerSupport.notifyScenarioCompleted(executionToken, scenarioToken);
        executionListenerSupport.notifyFeatureCompleted(executionToken, featureToken);
        executionListenerSupport.notifyTestsCompleted(executionToken, featureList, Collections.emptySet());

        //create inOrder object passing any mocks that need to be verified in order
        InOrder orderVerifier = inOrder(mockListener);

        orderVerifier.verify(mockListener).testsStarted(executionToken, featureList);
        orderVerifier.verify(mockListener).featureStarted(executionToken, featureToken);
        orderVerifier.verify(mockListener).scenarioStarted(executionToken, scenarioToken);
        orderVerifier.verify(mockListener).stepStarted(executionToken, stepToken);
        orderVerifier.verify(mockListener).stepCompleted(executionToken, stepToken);
        orderVerifier.verify(mockListener).scenarioCompleted(executionToken, scenarioToken);
        orderVerifier.verify(mockListener).featureCompleted(executionToken, featureToken);
        orderVerifier.verify(mockListener).testsCompleted(executionToken, featureList, Collections.emptySet());
    }


    @Test
    public void theOrderingImpliedByExecutionPriorityIsRespected() {
        
        executionListenerSupport.addExecutionListeners(priorityTwoHundred, priorityOneHundred, priorityThreeHundred);

        executionListenerSupport.notifyTestsStarted(executionToken, featureList);
        executionListenerSupport.notifyFeatureStarted(executionToken, featureToken);
        executionListenerSupport.notifyScenarioStarted(executionToken, scenarioToken);
        executionListenerSupport.notifyStepStarted(executionToken, stepToken);
        executionListenerSupport.notifyStepCompleted(executionToken, stepToken);
        executionListenerSupport.notifyScenarioCompleted(executionToken, scenarioToken);
        executionListenerSupport.notifyFeatureCompleted(executionToken, featureToken);
        executionListenerSupport.notifyTestsCompleted(executionToken, featureList, Collections.emptySet());
        
        List<String> expected = asList(new String[] {
             "testsStartedPriorityThreeHundred", 
             "testsStartedPriorityTwoHundred",
             "testsStartedPriorityOneHundred",
             "featureStartedPriorityThreeHundred",
             "featureStartedPriorityTwoHundred",
             "featureStartedPriorityOneHundred",
             "scenarioStartedPriorityThreeHundred",
             "scenarioStartedPriorityTwoHundred",
             "scenarioStartedPriorityOneHundred",
             "stepStartedPriorityThreeHundred",
             "stepStartedPriorityTwoHundred",
             "stepStartedPriorityOneHundred",
             "stepCompletedPriorityOneHundred",
             "stepCompletedPriorityTwoHundred",
             "stepCompletedPriorityThreeHundred",
             "scenarioCompletedPriorityOneHundred",
             "scenarioCompletedPriorityTwoHundred",
             "scenarioCompletedPriorityThreeHundred",
             "featureCompletedPriorityOneHundred",
             "featureCompletedPriorityTwoHundred",
             "featureCompletedPriorityThreeHundred",
             "testsCompletedPriorityOneHundred",
             "testsCompletedPriorityTwoHundred",
             "testsCompletedPriorityThreeHundred"
        });
        
        assertEquals(expected, executions);
    }


    @ExecutionPriority(200)
    class PriorityTwoHundred extends ExecutionTrackingExecutionListener {}

    @ExecutionPriority(100)
    class PriorityOneHundred extends ExecutionTrackingExecutionListener {}

    @ExecutionPriority(300)
    class PriorityThreeHundred extends ExecutionTrackingExecutionListener {}
    
    private abstract class ExecutionTrackingExecutionListener implements ExecutionListener {

        private final String name;

        public ExecutionTrackingExecutionListener() {
            this.name = getClass().getSimpleName();
        }
        
        @Override
        public void testsStarted(ExecutionToken testExecutionToken, List<FeatureToken> features) {
            executions.add("testsStarted" + name);
        }

        @Override
        public void testsCompleted(ExecutionToken testExecutionToken, List<FeatureToken> features, Set<CataloguedStep> cataloguedSteps) {
            executions.add("testsCompleted" + name);
        }

        @Override
        public void featureStarted(ExecutionToken testExecutionToken, FeatureToken feature) {
            executions.add("featureStarted" + name);
        }

        @Override
        public void featureCompleted(ExecutionToken testExecutionToken, FeatureToken feature) {
            executions.add("featureCompleted" + name);
        }

        @Override
        public void scenarioStarted(ExecutionToken testExecutionToken, ScenarioToken scenario) {
            executions.add("scenarioStarted" + name);
        }

        @Override
        public void scenarioCompleted(ExecutionToken testExecutionToken, ScenarioToken scenario) {
            executions.add("scenarioCompleted" + name);
        }

        @Override
        public void stepStarted(ExecutionToken testExecutionToken, StepToken step) {
            executions.add("stepStarted" + name);
        }

        @Override
        public void stepCompleted(ExecutionToken testExecutionToken, StepToken step) {
            executions.add("stepCompleted" + name);
        }
    }

}