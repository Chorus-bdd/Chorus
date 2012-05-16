package org.chorusbdd.chorus.remoting.jmx;

import org.chorusbdd.chorus.core.interpreter.ChorusExecutionListener;
import org.chorusbdd.chorus.core.interpreter.results.FeatureToken;
import org.chorusbdd.chorus.core.interpreter.results.ScenarioToken;
import org.chorusbdd.chorus.core.interpreter.results.StepToken;
import org.chorusbdd.chorus.core.interpreter.results.TestExecutionToken;
import org.chorusbdd.chorus.remoting.jmx.RemoteExecutionListenerMBean;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 16/05/12
 * Time: 17:08
 *
 * An MBean which simply delegates calls to the real execution listener implementation
 */
public class RemoteExecutionListener implements RemoteExecutionListenerMBean {

    private ChorusExecutionListener chorusExecutionListener;

    public RemoteExecutionListener(ChorusExecutionListener chorusExecutionListener) {
        this.chorusExecutionListener = chorusExecutionListener;
    }

    public void testsStarted(TestExecutionToken testExecutionToken) {
        chorusExecutionListener.testsStarted(testExecutionToken);
    }

    public void testsCompleted(TestExecutionToken testExecutionToken, List<FeatureToken> features) {
        chorusExecutionListener.testsCompleted(testExecutionToken, features);
    }

    public void featureStarted(TestExecutionToken testExecutionToken, FeatureToken feature) {
        chorusExecutionListener.featureStarted(testExecutionToken, feature);
    }

    public void featureCompleted(TestExecutionToken testExecutionToken, FeatureToken feature) {
        chorusExecutionListener.featureCompleted(testExecutionToken, feature);
    }

    public void scenarioStarted(TestExecutionToken testExecutionToken, ScenarioToken scenario) {
        chorusExecutionListener.scenarioStarted(testExecutionToken, scenario);
    }

    public void scenarioCompleted(TestExecutionToken testExecutionToken, ScenarioToken scenario) {
        chorusExecutionListener.scenarioCompleted(testExecutionToken, scenario);
    }

    public void stepStarted(TestExecutionToken testExecutionToken, StepToken step) {
        chorusExecutionListener.stepStarted(testExecutionToken, step);
    }

    public void stepCompleted(TestExecutionToken testExecutionToken, StepToken step) {
        chorusExecutionListener.stepCompleted(testExecutionToken, step);
    }
}
