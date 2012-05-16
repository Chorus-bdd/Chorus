package org.chorusbdd.chorus.core.interpreter;

import org.chorusbdd.chorus.core.interpreter.results.FeatureToken;
import org.chorusbdd.chorus.core.interpreter.results.ScenarioToken;
import org.chorusbdd.chorus.core.interpreter.results.StepToken;
import org.chorusbdd.chorus.core.interpreter.results.TestExecutionToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 16/05/12
 * Time: 22:03
 * To change this template use File | Settings | File Templates.
 */
public class ExecutionListenerSupport {

    private List<ChorusExecutionListener> listeners = new ArrayList<ChorusExecutionListener>();

    //
    // Execution event methods
    //
    public void addExecutionListener(ChorusExecutionListener... listeners) {
        this.listeners.addAll(Arrays.asList(listeners));
    }

    public boolean removeExecutionListener(ChorusExecutionListener... listeners) {
        return this.listeners.removeAll(Arrays.asList(listeners));
    }

    public void notifyStartTests(TestExecutionToken t) {
        for (ChorusExecutionListener listener : listeners) {
            listener.testsStarted(t);
        }
    }

    public void notifyStepStarted(TestExecutionToken t, StepToken step) {
        for (ChorusExecutionListener listener : listeners) {
            listener.stepStarted(t, step);
        }
    }

    public void notifyStepCompleted(TestExecutionToken t, StepToken step) {
        for (ChorusExecutionListener listener : listeners) {
            listener.stepCompleted(t, step);
        }
    }

    public void notifyFeatureStarted(TestExecutionToken t, FeatureToken feature) {
        for (ChorusExecutionListener listener : listeners) {
            listener.featureStarted(t, feature);
        }
    }

    public void notifyFeatureCompleted(TestExecutionToken t, FeatureToken feature) {
        for (ChorusExecutionListener listener : listeners) {
            listener.featureCompleted(t, feature);
        }
    }

    public void notifyScenarioStarted(TestExecutionToken t, ScenarioToken scenario) {
        for (ChorusExecutionListener listener : listeners) {
            listener.scenarioStarted(t, scenario);
        }
    }

    public void notifyScenarioCompleted(TestExecutionToken t, ScenarioToken scenario) {
        for (ChorusExecutionListener listener : listeners) {
            listener.scenarioCompleted(t, scenario);
        }
    }

    public void notifyTestsCompleted(TestExecutionToken t, List<FeatureToken> features) {
        for (ChorusExecutionListener listener : listeners) {
            listener.testsCompleted(t, features);
        }
    }

}
