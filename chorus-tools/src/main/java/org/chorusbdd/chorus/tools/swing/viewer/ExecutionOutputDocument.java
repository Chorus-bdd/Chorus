package org.chorusbdd.chorus.tools.swing.viewer;

import org.chorusbdd.chorus.core.interpreter.ChorusExecutionListener;
import org.chorusbdd.chorus.core.interpreter.results.FeatureToken;
import org.chorusbdd.chorus.core.interpreter.results.ScenarioToken;
import org.chorusbdd.chorus.core.interpreter.results.StepToken;
import org.chorusbdd.chorus.core.interpreter.results.TestExecutionToken;
import org.chorusbdd.chorus.executionlistener.PlainResultsFormatter;

import javax.swing.text.*;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 31/05/12
 * Time: 09:18
 */
public class ExecutionOutputDocument extends DefaultStyledDocument {

    private final Style base = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);

    private final Style testsHeader = addStyle("testsHeader", base);

    private final Style featureHeader = addStyle("featureHeader", base);
    private final Style featureDetail = addStyle("featureDetail", base);

    private final Style scenarioHeader = addStyle("scenarioHeader", base);
    private final Style scenarioDetail = addStyle("scenarioDetail", base);

    private final Style stepHeader = addStyle("stepHeader", base);
    private final Style stepDetail = addStyle("stepDetail", base);

    private final Style failedStepHeader = addStyle("failedStepHeader", base);
    private final Style failedStepDetail = addStyle("failedStepDetail", base);

    private final Style incompleteStepHeader = addStyle("incompleteStepHeader", base);
    private final Style incompleteStepDetail = addStyle("incompleteStepDetail", base);


    private final Style resultsSummary = addStyle("resultsSummary", base);

    public ExecutionOutputDocument() {
        StyleConstants.setFontFamily(base, "monospaced");
        StyleConstants.setFontFamily(featureHeader, "monospaced");

        //make base and all headers blue
        applyStyle(new StyleApplicator() {
          public void applyStyle(Style s) {
              StyleConstants.setForeground(base, Color.BLUE);
          }
        }, base, featureHeader, scenarioHeader, stepHeader);

        StyleConstants.setFontSize(testsHeader, 18);
        StyleConstants.setForeground(testsHeader, Color.MAGENTA.darker().darker());
        StyleConstants.setFontFamily(testsHeader, "proportional");


        StyleConstants.setForeground(featureDetail, Color.GREEN.darker().darker());
        StyleConstants.setForeground(scenarioDetail, Color.GREEN.darker().darker());
        StyleConstants.setForeground(stepDetail, Color.BLACK);
        StyleConstants.setForeground(incompleteStepDetail, Color.ORANGE);
        StyleConstants.setForeground(incompleteStepHeader, Color.ORANGE);
        StyleConstants.setForeground(failedStepHeader, Color.RED);
        StyleConstants.setForeground(failedStepDetail, Color.RED);
        StyleConstants.setForeground(resultsSummary, Color.BLUE.darker().darker());
    }


    public void testsStarted(TestExecutionToken testExecutionToken) {
        if ( ! "".equals(testExecutionToken.getTestSuiteName())) {
            addText("Suite: " + testExecutionToken.getTestSuiteName() + "\n", testsHeader);
        }
        addText("\n", featureHeader);
    }

    public void featureStarted(FeatureToken feature) {
        addText("Feature:  ", featureHeader);
        addText(feature.getName(), featureDetail);
    }

    public void featureCompleted(FeatureToken feature) {
        addText("\n\n", featureDetail);
    }

    public void scenarioStarted(ScenarioToken scenario) {
        addText("\n\n    Scenario:  ", scenarioHeader);
        addText(scenario.getName(), scenarioDetail);
    }

    public void scenarioCompleted(ScenarioToken scenario) {
    }

    public void stepStarted(StepToken step) {
    }

    public void stepCompleted(StepToken step) {
        Style s = step.isUndefinedPendingOrSkipped() ? incompleteStepDetail :
                step.isPassed() ? stepDetail : failedStepDetail;

        Style h = step.isUndefinedPendingOrSkipped() ? incompleteStepHeader :
                        step.isPassed() ? stepHeader : failedStepHeader;

        String stepText = step.toString();
        String stepHeaderText = stepText, stepDetailText = "";
        int firstSpace = stepText.indexOf(' ');
        if ( firstSpace != -1) {
            stepHeaderText = stepText.substring(0, firstSpace);
            stepDetailText = stepText.substring(firstSpace);
        }
        addText("\n        " + stepHeaderText, h);
        addText(stepDetailText, s);
    }

    public void testsCompleted(TestExecutionToken testExecutionToken) {
        String resultsSummaryText = getResultsSummaryString(testExecutionToken);
        addText(resultsSummaryText, resultsSummary);
        addText("\n\n", base);
    }

    private String getResultsSummaryString(TestExecutionToken testExecutionToken) {
        ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
        PrintWriter pw = new PrintWriter(os);
        PlainResultsFormatter f = new PlainResultsFormatter(pw);
        f.printResults(testExecutionToken.getResultsSummary());
        return new String(os.toByteArray());
    }

    private Segment addText(String textToAdd, Style style) {
        Segment result = new Segment();
        try {
            int insertPosition = getLength();
            insertString(insertPosition, textToAdd, style);
            getText(insertPosition, textToAdd.length(), result);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        return result;
    }


    private void applyStyle(StyleApplicator styleApplicator, Style... styles) {
        for ( Style s : styles) {
            styleApplicator.applyStyle(s);
        }
    }

    private static interface StyleApplicator {
        public void applyStyle(Style s);
    }
}
