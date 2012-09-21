/**
 *  Copyright (C) 2000-2012 The Software Conservancy and Original Authors.
 *  All rights reserved.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to
 *  deal in the Software without restriction, including without limitation the
 *  rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 *  sell copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 *  IN THE SOFTWARE.
 *
 *  Nothing in this notice shall be deemed to grant any rights to trademarks,
 *  copyrights, patents, trade secrets or any other intellectual property of the
 *  licensor or any contributor except as expressly stated herein. No patent
 *  license is granted separate from the Software, for code that you delete from
 *  the Software, or for combinations of the Software with other software or
 *  hardware.
 */
package org.chorusbdd.chorus.tools.swing.viewer;

import org.chorusbdd.chorus.core.interpreter.results.ExecutionToken;
import org.chorusbdd.chorus.core.interpreter.results.FeatureToken;
import org.chorusbdd.chorus.core.interpreter.results.ScenarioToken;
import org.chorusbdd.chorus.core.interpreter.results.StepToken;
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


    public void testsStarted(ExecutionToken testExecutionToken) {
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

    public void testsCompleted(ExecutionToken testExecutionToken) {
        String resultsSummaryText = getResultsSummaryString(testExecutionToken);
        addText(resultsSummaryText, resultsSummary);
        addText("\n\n", base);
    }

    private String getResultsSummaryString(ExecutionToken testExecutionToken) {
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
