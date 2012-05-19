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

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
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
    private final Style red = document.addStyle("red", base);
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
        StyleConstants.setForeground(base, Color.BLUE);
        StyleConstants.setForeground(red, Color.RED);
        setPreferredSize(ChorusViewerConstants.DEFAULT_SPLIT_PANE_CONTENT_SIZE);
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

    public void stepStarted(TestExecutionToken testExecutionToken, StepToken step) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void stepCompleted(TestExecutionToken testExecutionToken, StepToken step) {
        addText("\n        " + step.toString(), red);
    }

    public void testsCompleted(TestExecutionToken testExecutionToken, List<FeatureToken> features) {
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
