/**
 *  Copyright (C) 2000-2012 The Software Conservancy as Trustee.
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

import org.chorusbdd.chorus.core.interpreter.ChorusExecutionListener;
import org.chorusbdd.chorus.core.interpreter.results.*;
import org.chorusbdd.chorus.executionlistener.PlainResultsFormatter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import java.awt.*;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.List;

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

    private final Style testsHeader = document.addStyle("testsHeader", base);

    private final Style featureHeader = document.addStyle("featureHeader", base);
    private final Style featureDetail = document.addStyle("featureDetail", base);

    private final Style scenarioHeader = document.addStyle("scenarioHeader", base);
    private final Style scenarioDetail = document.addStyle("scenarioDetail", base);

    private final Style stepHeader = document.addStyle("stepHeader", base);
    private final Style stepDetail = document.addStyle("stepDetail", base);

    private final Style resultsSummary = document.addStyle("resultsSummary", base);

    private final Color PALE_YELLOW = new Color(255, 253, 221);

    public ExecutionOutputViewer() {
        setLayout(new BorderLayout());
        JScrollPane sp = new JScrollPane(
            executionTextPane,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );
        add(sp, BorderLayout.CENTER);

        executionTextPane.setBackground(PALE_YELLOW);
        executionTextPane.setBorder(new EmptyBorder(8,5,5,5));

        StyleConstants.setFontFamily(base, "monospaced");
        StyleConstants.setFontFamily(featureHeader, "monospaced");

        //make base and all headers blue
        applyStyle(new StyleApplicator() {
            public void applyStyle(Style s) {
                StyleConstants.setForeground(base, Color.BLUE);
            }
        }, base, featureHeader, scenarioHeader, stepHeader);

        StyleConstants.setFontSize(testsHeader, executionTextPane.getFont().getSize() + 6);
        StyleConstants.setForeground(testsHeader, Color.MAGENTA.darker().darker());
        StyleConstants.setFontFamily(testsHeader, "proportional");


        StyleConstants.setForeground(featureDetail, Color.GREEN.darker().darker());
        StyleConstants.setForeground(scenarioDetail, Color.GREEN.darker().darker());
        StyleConstants.setForeground(stepDetail, Color.BLACK);
        StyleConstants.setForeground(resultsSummary, Color.BLUE.darker().darker());

        setPreferredSize(ChorusViewerConstants.DEFAULT_SPLIT_PANE_CONTENT_SIZE);
    }

    private void applyStyle(StyleApplicator styleApplicator, Style... styles) {
        for ( Style s : styles) {
            styleApplicator.applyStyle(s);
        }
    }

    public void testsStarted(TestExecutionToken testExecutionToken) {
        if ( ! "".equals(testExecutionToken.getTestSuiteName())) {
            addText("Suite: " + testExecutionToken.getTestSuiteName() + "\n", testsHeader);
        }
        addText("\n", featureHeader);
    }

    public void featureStarted(TestExecutionToken testExecutionToken, FeatureToken feature) {
        addText("Feature:  ", featureHeader);
        addText(feature.getName(), featureDetail);
    }

    public void featureCompleted(TestExecutionToken testExecutionToken, FeatureToken feature) {
        addText("\n\n", featureDetail);
    }

    public void scenarioStarted(TestExecutionToken testExecutionToken, ScenarioToken scenario) {
        addText("\n\n    Scenario:  ", scenarioHeader);
        addText(scenario.getName(), scenarioDetail);
    }

    public void scenarioCompleted(TestExecutionToken testExecutionToken, ScenarioToken scenario) {
    }

    public void stepStarted(TestExecutionToken testExecutionToken, StepToken step) {
    }

    public void stepCompleted(TestExecutionToken testExecutionToken, StepToken step) {
        String stepText = step.toString();
        String stepHeaderText = stepText, stepDetailText = "";
        int firstSpace = stepText.indexOf(' ');
        if ( firstSpace != -1) {
            stepHeaderText = stepText.substring(0, firstSpace);
            stepDetailText = stepText.substring(firstSpace);
        }
        addText("\n        " + stepHeaderText, stepHeader);
        addText(stepDetailText, stepDetail);
    }

    public void testsCompleted(TestExecutionToken testExecutionToken, List<FeatureToken> features) {
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
            int insertPosition = document.getLength();
            document.insertString(insertPosition, textToAdd, style);
            document.getText(insertPosition, textToAdd.length(), result);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        return result;
    }

    private interface StyleApplicator {
        public void applyStyle(Style s);
    }
}
