package org.chorusbdd.chorus.executionlistener;

import org.chorusbdd.chorus.results.ExecutionToken;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.results.ScenarioToken;
import org.chorusbdd.chorus.results.StepToken;

import java.util.List;

/**
 * User: nick
 * Date: 24/12/12
 * Time: 14:44
 *
 * Base implementation for the ExecutionListener interface
 */
public class ExecutionListenerAdapter implements ExecutionListener {

    public void testsStarted(ExecutionToken testExecutionToken) {
    }

    public void testsCompleted(ExecutionToken testExecutionToken, List<FeatureToken> features) {
    }

    public void featureStarted(ExecutionToken testExecutionToken, FeatureToken feature) {
    }

    public void featureCompleted(ExecutionToken testExecutionToken, FeatureToken feature) {
    }

    public void scenarioStarted(ExecutionToken testExecutionToken, ScenarioToken scenario) {
    }

    public void scenarioCompleted(ExecutionToken testExecutionToken, ScenarioToken scenario) {
    }

    public void stepStarted(ExecutionToken testExecutionToken, StepToken step) {
    }

    public void stepCompleted(ExecutionToken testExecutionToken, StepToken step) {
    }
}
