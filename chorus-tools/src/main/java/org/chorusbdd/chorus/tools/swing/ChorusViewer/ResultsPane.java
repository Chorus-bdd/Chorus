package org.chorusbdd.chorus.tools.swing.ChorusViewer;

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
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void featureStarted(TestExecutionToken testExecutionToken, FeatureToken feature) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void scenarioStarted(TestExecutionToken testExecutionToken, ScenarioToken scenario) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void stepExecuted(TestExecutionToken testExecutionToken, StepToken step) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void testsCompleted(TestExecutionToken testExecutionToken, ResultsSummary results) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
