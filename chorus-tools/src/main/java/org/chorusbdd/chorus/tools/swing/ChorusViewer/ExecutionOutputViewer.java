package org.chorusbdd.chorus.tools.swing.ChorusViewer;

import org.chorusbdd.chorus.core.interpreter.ChorusExecutionListener;
import org.chorusbdd.chorus.core.interpreter.results.*;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;

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
        setLayout(new BorderLayout());
        JScrollPane sp = new JScrollPane();
        sp.getViewport().add(executionTextPane);
        add(sp, BorderLayout.CENTER);
    }

    public void testsStarted(TestExecutionToken testExecutionToken) {
        String textToAdd = "Starting tests " + testExecutionToken.toString();
        addText(textToAdd);
    }

    public void featureStarted(TestExecutionToken testExecutionToken, FeatureToken feature) {
        addText("Started feature " + feature);
    }

    public void scenarioStarted(TestExecutionToken testExecutionToken, ScenarioToken scenario) {
        addText("Started scenario " + scenario);
    }

    public void stepExecuted(TestExecutionToken testExecutionToken, StepToken step) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void testsCompleted(TestExecutionToken testExecutionToken, ResultsSummary results) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    private void addText(String textToAdd) {
        try {
            executionTextPane.getStyledDocument().insertString(0, textToAdd, null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
}
