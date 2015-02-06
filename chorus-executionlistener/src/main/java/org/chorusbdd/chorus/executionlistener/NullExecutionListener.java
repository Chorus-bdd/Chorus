package org.chorusbdd.chorus.executionlistener;

import org.chorusbdd.chorus.results.ExecutionToken;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.results.ScenarioToken;
import org.chorusbdd.chorus.results.StepToken;

import java.util.List;

/**
 * Created by nick on 15/10/2014.
 */
public class NullExecutionListener {

    public static final ExecutionListener NULL_LISTENER = new ExecutionListener() {

        public void testsStarted(ExecutionToken testExecutionToken, List<FeatureToken> features) {}

        public void testsCompleted(ExecutionToken testExecutionToken, List<FeatureToken> features) {}

        public void featureStarted(ExecutionToken testExecutionToken, FeatureToken feature) {}

        public void featureCompleted(ExecutionToken testExecutionToken, FeatureToken feature) {}

        public void scenarioStarted(ExecutionToken testExecutionToken, ScenarioToken scenario) {}

        public void scenarioCompleted(ExecutionToken testExecutionToken, ScenarioToken scenario) {}

        public void stepStarted(ExecutionToken testExecutionToken, StepToken step) {}

        public void stepCompleted(ExecutionToken testExecutionToken, StepToken step) {}
    };

}
