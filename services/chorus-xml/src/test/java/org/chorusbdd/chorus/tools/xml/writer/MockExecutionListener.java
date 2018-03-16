package org.chorusbdd.chorus.tools.xml.writer;

import org.chorusbdd.chorus.executionlistener.ExecutionListener;
import org.chorusbdd.chorus.results.*;

import java.util.List;
import java.util.Set;

/**
* Created by Nick E on 19/02/2015.
*/
public class MockExecutionListener implements ExecutionListener {

    static ExecutionToken testExecutionToken;
    static List<FeatureToken> featureTokens;

    public void testsStarted(ExecutionToken testExecutionToken, List<FeatureToken> features) {
    }

    public void testsCompleted(ExecutionToken testExecutionToken, List<FeatureToken> features, Set<CataloguedStep> cataloguedSteps) {
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
