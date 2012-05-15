package org.chorusbdd.chorus.tools.swing.chorusviewer;

import org.chorusbdd.chorus.core.interpreter.ChorusExecutionListener;
import org.chorusbdd.chorus.core.interpreter.results.*;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: GA2EBBU
 * Date: 15/05/12
 * Time: 16:03
 * To change this template use File | Settings | File Templates.
 */
public class ExecutionOutputViewer extends JPanel implements ChorusExecutionListener {

    private final JTextPane executionTextPane = new JTextPane();
    private final StyledDocument document = executionTextPane.getStyledDocument();
    private final Style base = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
    private final Style red = document.addStyle("red", base);

    public ExecutionOutputViewer() {
        setLayout(new BorderLayout());
        JScrollPane sp = new JScrollPane();
        sp.getViewport().add(executionTextPane);
        add(sp, BorderLayout.CENTER);

        executionTextPane.setBackground(new Color(255, 253, 221));
        StyleConstants.setForeground(base, Color.BLUE);
        StyleConstants.setForeground(red, Color.RED);
    }

    public void testsStarted(TestExecutionToken testExecutionToken) {
        //addText("Starting tests " + testExecutionToken.toString(), base);
    }

    public void featureStarted(TestExecutionToken testExecutionToken, FeatureToken feature) {
        addText("\n\n\nFeature:" + feature.getName(), base);
    }

    public void scenarioStarted(TestExecutionToken testExecutionToken, ScenarioToken scenario) {
        addText("\n\n  Scenario:" + scenario.getName(), base);
    }

    public void stepExecuted(TestExecutionToken testExecutionToken, StepToken step) {
        addText("\n    " + step.toString(), red);
    }

    public void testsCompleted(TestExecutionToken testExecutionToken, ResultsSummary results) {
        addText("\n\n\n", base);
    }

    private void addText(String textToAdd, Style style) {
        try {
            document.insertString(document.getLength(), textToAdd, style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
}
