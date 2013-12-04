package org.chorusbdd.chorus.selftest.executionlistener;

import org.chorusbdd.chorus.executionlistener.ExecutionListener;
import org.chorusbdd.chorus.results.ExecutionToken;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.results.ScenarioToken;
import org.chorusbdd.chorus.results.StepToken;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * User: nick
 * Date: 03/12/13
 * Time: 18:43
 */
public class ExecutionListenerOne implements ExecutionListener {
    
    public static AtomicBoolean isTestsStartedCalled = new AtomicBoolean();
    public static AtomicBoolean isFeatureStartedCalled = new AtomicBoolean();
    public static AtomicBoolean isScenarioStartedCalled = new AtomicBoolean();

    /**
     * @param testExecutionToken a token representing the current suite of tests starting execution
     */
    public void testsStarted(ExecutionToken testExecutionToken) {
        isTestsStartedCalled.getAndSet(true);
    }

    /**
     * @param testExecutionToken a token representing the current suite of tests
     * @param features           a List of features executed
     */
    public void testsCompleted(ExecutionToken testExecutionToken, List<FeatureToken> features) {
    }

    /**
     * @param testExecutionToken a token representing the current suite of tests running
     * @param feature            a token representing the feature which is starting
     */
    public void featureStarted(ExecutionToken testExecutionToken, FeatureToken feature) {
        isFeatureStartedCalled.getAndSet(true);
    }

    /**
     * @param testExecutionToken a token representing the current suite of tests running
     * @param feature            a token representing the feature which has just completed
     */
    public void featureCompleted(ExecutionToken testExecutionToken, FeatureToken feature) {
    }

    /**
     * @param testExecutionToken a token representing the current suite of tests running
     * @param scenario           a token representing the scenario which is starting
     */
    public void scenarioStarted(ExecutionToken testExecutionToken, ScenarioToken scenario) {
        isScenarioStartedCalled.getAndSet(true);
    }

    /**
     * @param testExecutionToken a token representing the current suite of tests running
     * @param scenario           a token representing the scenario which has just completed
     */
    public void scenarioCompleted(ExecutionToken testExecutionToken, ScenarioToken scenario) {
    }

    /**
     * @param testExecutionToken a token representing the current suite of tests running
     * @param step               a token representing the test stop which has just started execution
     */
    public void stepStarted(ExecutionToken testExecutionToken, StepToken step) {
    }

    /**
     * @param testExecutionToken a token representing the current suite of tests running
     * @param step               a token representing the test stop which has just completed execution
     */
    public void stepCompleted(ExecutionToken testExecutionToken, StepToken step) {
    }
}
