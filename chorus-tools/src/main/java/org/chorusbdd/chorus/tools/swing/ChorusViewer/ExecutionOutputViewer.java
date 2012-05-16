package org.chorusbdd.chorus.tools.swing.chorusviewer;

import org.chorusbdd.chorus.core.interpreter.ChorusExecutionListener;
import org.chorusbdd.chorus.core.interpreter.results.*;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 15/05/12
 * Time: 16:03
 * To change this template use File | Settings | File Templates.
 */
public class ExecutionOutputViewer extends JPanel implements ChorusExecutionListener {

    private final JTextPane executionTextPane = new JTextPane();
    private final StyledDocument document = executionTextPane.getStyledDocument();
    private final Style base = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
    private final Style red = document.addStyle("red", base);
    private final Color PALE_YELLOW = new Color(255, 253, 221);

    public ExecutionOutputViewer() {
        setLayout(new BorderLayout());
        JScrollPane sp = new JScrollPane();
        sp.getViewport().add(executionTextPane);
        add(sp, BorderLayout.CENTER);

        executionTextPane.setBackground(PALE_YELLOW);
        StyleConstants.setForeground(base, Color.BLUE);
        StyleConstants.setForeground(red, Color.RED);
    }

    public void testsStarted(TestExecutionToken testExecutionToken) {
        //addText("Starting tests " + testExecutionToken.toString(), base);
    }

    public void featureStarted(TestExecutionToken testExecutionToken, FeatureToken feature) {
        addText("\n\n\nFeature:  " + feature.getName(), base);
    }

    public void featureCompleted(TestExecutionToken testExecutionToken, FeatureToken feature) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void scenarioStarted(TestExecutionToken testExecutionToken, ScenarioToken scenario) {
        addText("\n\n    Scenario:  " + scenario.getName(), base);
    }

    public void scenarioCompleted(TestExecutionToken testExecutionToken, ScenarioToken scenario) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void stepStarted(TestExecutionToken testExecutionToken, TestExecutionToken step) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void stepCompleted(TestExecutionToken testExecutionToken, StepToken step) {
        addText("\n        " + step.toString(), red);
    }

    public void testsCompleted(TestExecutionToken testExecutionToken, ResultsSummary results) {
        addText("\n\n\n", base);
    }

    private Segment addText(String textToAdd, Style style) {
        Segment result = new Segment();
        try {
            int insertPosition = document.getLength();
            document.insertString(insertPosition, textToAdd, style);
            document.getText(insertPosition, textToAdd.length(), result);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        return result;
    }
}
