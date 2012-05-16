package org.chorusbdd.chorus.tools.swing.chorusviewer;

import org.chorusbdd.chorus.core.interpreter.ChorusExecutionListener;
import org.chorusbdd.chorus.core.interpreter.results.*;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 15/05/12
 * Time: 15:03
 *
 */
public class ChorusViewerMainFrame extends JFrame implements ChorusExecutionListener {

    private JTabbedPane resultsTabbedPane = new JTabbedPane();
    private Map<TestExecutionToken, ResultsPane> executionTokenToResultsPaneMap = new HashMap<TestExecutionToken, ResultsPane>();

    public ChorusViewerMainFrame() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(resultsTabbedPane, BorderLayout.CENTER);
        setSize(1024, 768);
        setLocationRelativeTo(null); //centre on screen
    }

    public void testsStarted(TestExecutionToken testExecutionToken) {
        ResultsPane resultPane = new ResultsPane(testExecutionToken);
        executionTokenToResultsPaneMap.put(testExecutionToken, resultPane);
        resultsTabbedPane.addTab(testExecutionToken.toString(), resultPane);
        resultPane.testsStarted(testExecutionToken);
    }

    public void featureStarted(TestExecutionToken testExecutionToken, FeatureToken feature) {
        executionTokenToResultsPaneMap.get(testExecutionToken).featureStarted(testExecutionToken, feature);
    }

    public void featureCompleted(TestExecutionToken testExecutionToken, FeatureToken feature) {
        executionTokenToResultsPaneMap.get(testExecutionToken).featureCompleted(testExecutionToken, feature);
    }

    public void scenarioStarted(TestExecutionToken testExecutionToken, ScenarioToken scenario) {
        executionTokenToResultsPaneMap.get(testExecutionToken).scenarioStarted(testExecutionToken, scenario);
    }

    public void scenarioCompleted(TestExecutionToken testExecutionToken, ScenarioToken scenario) {
        executionTokenToResultsPaneMap.get(testExecutionToken).scenarioCompleted(testExecutionToken, scenario);
    }

    public void stepStarted(TestExecutionToken testExecutionToken, StepToken step) {
        executionTokenToResultsPaneMap.get(testExecutionToken).stepStarted(testExecutionToken, step);
    }

    public void stepCompleted(TestExecutionToken testExecutionToken, StepToken step) {
        executionTokenToResultsPaneMap.get(testExecutionToken).stepCompleted(testExecutionToken, step);
    }

    public void testsCompleted(TestExecutionToken testExecutionToken, ResultsSummary results) {
        executionTokenToResultsPaneMap.get(testExecutionToken).testsCompleted(testExecutionToken, results);
    }

}
