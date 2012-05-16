package org.chorusbdd.chorus.tools.swing.chorusviewer;

import org.chorusbdd.chorus.core.interpreter.ChorusExecutionListener;
import org.chorusbdd.chorus.core.interpreter.results.*;
import org.chorusbdd.chorus.tools.util.CompositeListener;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Created with IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 15/05/12
 * Time: 15:47
 * To change this template use File | Settings | File Templates.
 */
public class ResultsPane extends JSplitPane implements ChorusExecutionListener {

    private TestExecutionToken testExecutionToken;
    private ExecutionOutputViewer executionOutputViewer = new ExecutionOutputViewer();
    private FeatureTreeViewer featureTreeViewer = new FeatureTreeViewer(executionOutputViewer);

    //proxy our events to both the executionOutputViewer and the featureTreeViewer
    private ChorusExecutionListener proxyingListener = CompositeListener.getCompositeListener(
        ChorusExecutionListener.class,
        executionOutputViewer,
        featureTreeViewer
    );


    public ResultsPane(TestExecutionToken testExecutionToken) {
        this.testExecutionToken = testExecutionToken;
        setLeftComponent(featureTreeViewer);
        setRightComponent(executionOutputViewer);
        addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent e) {
                setDividerLocation(0.5);
            }
        });
    }

    public void testsStarted(TestExecutionToken testExecutionToken) {
        proxyingListener.testsStarted(testExecutionToken);
    }

    public void testsCompleted(TestExecutionToken testExecutionToken, ResultsSummary results) {
        proxyingListener.testsCompleted(testExecutionToken, results);
    }

    public void featureStarted(TestExecutionToken testExecutionToken, FeatureToken feature) {
        proxyingListener.featureStarted(testExecutionToken, feature);
    }

    public void featureCompleted(TestExecutionToken testExecutionToken, FeatureToken feature) {
        proxyingListener.featureCompleted(testExecutionToken, feature);
    }

    public void scenarioStarted(TestExecutionToken testExecutionToken, ScenarioToken scenario) {
        proxyingListener.scenarioStarted(testExecutionToken, scenario);
    }

    public void scenarioCompleted(TestExecutionToken testExecutionToken, ScenarioToken scenario) {
        proxyingListener.scenarioCompleted(testExecutionToken, scenario);
    }

    public void stepStarted(TestExecutionToken testExecutionToken, StepToken step) {
        proxyingListener.stepStarted(testExecutionToken, step);
    }

    public void stepCompleted(TestExecutionToken testExecutionToken, StepToken step) {
        proxyingListener.stepCompleted(testExecutionToken, step);
    }
}
