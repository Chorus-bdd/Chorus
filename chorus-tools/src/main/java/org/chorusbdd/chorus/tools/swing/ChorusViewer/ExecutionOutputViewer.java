package org.chorusbdd.chorus.tools.swing.ChorusViewer;

import org.chorusbdd.chorus.core.interpreter.ChorusExecutionListener;
import org.chorusbdd.chorus.core.interpreter.results.*;

import javax.swing.*;
import javax.swing.text.BadLocationException;

/**
 * Created with IntelliJ IDEA.
 * User: GA2EBBU
 * Date: 15/05/12
 * Time: 16:03
 * To change this template use File | Settings | File Templates.
 */
public class ExecutionOutputViewer extends JPanel implements ChorusExecutionListener {

    private JTextPane executionTextPane = new JTextPane();

    public ExecutionOutputViewer() {
    }

    public void testsStarted(TestExecutionToken testExecutionToken) {
        try {
            executionTextPane.getStyledDocument().insertString(0, "Starting tests " + testExecutionToken.toString(), null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
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
