package org.chorusbdd.chorus.tools.swing.chorusviewer;

import org.chorusbdd.chorus.core.interpreter.ChorusExecutionListener;
import org.chorusbdd.chorus.core.interpreter.results.*;

import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: GA2EBBU
 * Date: 15/05/12
 * Time: 15:47
 * To change this template use File | Settings | File Templates.
 */
public class ResultsPane extends JSplitPane implements ChorusExecutionListener {

    private ExecutionOutputViewer executionOutputViewer = new ExecutionOutputViewer();
    private FeatureTreeViewer featureTreeViewer = new FeatureTreeViewer(executionOutputViewer);
    private TestExecutionToken testExecutionToken;

    public ResultsPane(TestExecutionToken testExecutionToken) {
        this.testExecutionToken = testExecutionToken;
        setLeftComponent(featureTreeViewer);
        setRightComponent(executionOutputViewer);
    }

    public void testsStarted(TestExecutionToken testExecutionToken) {
        featureTreeViewer.testsStarted(testExecutionToken);
        executionOutputViewer.testsStarted(testExecutionToken);
    }

    public void featureStarted(TestExecutionToken testExecutionToken, FeatureToken feature) {
        featureTreeViewer.featureStarted(testExecutionToken, feature);
        executionOutputViewer.featureStarted(testExecutionToken, feature);
    }

    public void scenarioStarted(TestExecutionToken testExecutionToken, ScenarioToken scenario) {
        featureTreeViewer.scenarioStarted(testExecutionToken, scenario);
        executionOutputViewer.scenarioStarted(testExecutionToken, scenario);
    }

    public void stepExecuted(TestExecutionToken testExecutionToken, StepToken step) {
        featureTreeViewer.stepExecuted(testExecutionToken, step);
        executionOutputViewer.stepExecuted(testExecutionToken, step);
    }

    public void testsCompleted(TestExecutionToken testExecutionToken, ResultsSummary results) {
        featureTreeViewer.testsCompleted(testExecutionToken, results);
        executionOutputViewer.testsCompleted(testExecutionToken, results);
    }
}
