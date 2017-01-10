package org.chorusbdd.chorus.tools.xml.writer;

import org.chorusbdd.chorus.executionlistener.ExecutionListener;
import org.chorusbdd.chorus.results.ExecutionToken;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.results.ScenarioToken;
import org.chorusbdd.chorus.results.StepToken;

import java.util.List;

/**
* Created by GA2EBBU on 19/02/2015.
*/
public class MockExecutionListener implements ExecutionListener {

    static ExecutionToken testExecutionToken;
    static List<FeatureToken> featureTokens;

    public void testsStarted(ExecutionToken testExecutionToken, List<FeatureToken> features) {
    }

    public void testsCompleted(ExecutionToken testExecutionToken, List<FeatureToken> features) {
        this.testExecutionToken = testExecutionToken;
        this.featureTokens = features;
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
