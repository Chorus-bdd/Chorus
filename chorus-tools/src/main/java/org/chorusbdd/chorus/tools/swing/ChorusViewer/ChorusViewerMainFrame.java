package org.chorusbdd.chorus.tools.swing.chorusviewer;

import org.chorusbdd.chorus.core.interpreter.ChorusExecutionListener;
import org.chorusbdd.chorus.core.interpreter.results.*;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: GA2EBBU
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
        setSize(800, 600);
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

    public void scenarioStarted(TestExecutionToken testExecutionToken, ScenarioToken scenario) {
        executionTokenToResultsPaneMap.get(testExecutionToken).scenarioStarted(testExecutionToken, scenario);
    }

    public void stepExecuted(TestExecutionToken testExecutionToken, StepToken step) {
        executionTokenToResultsPaneMap.get(testExecutionToken).stepExecuted(testExecutionToken, step);
    }

    public void testsCompleted(TestExecutionToken testExecutionToken, ResultsSummary results) {
        executionTokenToResultsPaneMap.get(testExecutionToken).testsCompleted(testExecutionToken, results);
    }
}
