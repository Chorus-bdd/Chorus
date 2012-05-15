package org.chorusbdd.chorus.core.interpreter;

import org.chorusbdd.chorus.core.interpreter.results.*;

/**
 * Implementors can register with a ChorusInterpreter to recieve callbacks during test execution.
 *
 * Created by: Steve Neal
 * Date: 11/01/12
 */
public interface ChorusInterpreterExecutionListener {

    /**
     * @param testExecutionToken, a token representing the current suite of tests starting execution
     */
    public void testsStarted(TestExecutionToken testExecutionToken);

    /**
     * @param testExecutionToken, a token representing the current suite of tests running
     * @param feature, a token representing the feature which is starting
     */
    public void featureStarted(TestExecutionToken testExecutionToken, FeatureToken feature);

    /**
     * @param testExecutionToken, a token representing the current suite of tests running
     * @param scenario, a token representing the scenario which is starting
     */
    public void scenarioStarted(TestExecutionToken testExecutionToken, ScenarioToken scenario);

    /**
     * @param testExecutionToken, a token representing the current suite of tests running
     * @param step, a token representing the test stop which has just completed excecution
     */
    public void stepExecuted(TestExecutionToken testExecutionToken, StepToken step);

    /**
     * @param testExecutionToken, a token representing the current suite of tests running
     * @param results, a token representing the results of the test suite
     */
    public void testsCompleted(TestExecutionToken testExecutionToken, ResultsSummary results);
}
