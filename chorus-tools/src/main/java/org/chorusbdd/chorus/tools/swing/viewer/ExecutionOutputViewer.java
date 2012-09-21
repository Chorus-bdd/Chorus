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

import org.chorusbdd.chorus.core.interpreter.ExecutionListener;
import org.chorusbdd.chorus.core.interpreter.results.ExecutionToken;
import org.chorusbdd.chorus.core.interpreter.results.FeatureToken;
import org.chorusbdd.chorus.core.interpreter.results.ScenarioToken;
import org.chorusbdd.chorus.core.interpreter.results.StepToken;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 15/05/12
 * Time: 16:03
 * To change this template use File | Settings | File Templates.
 */
public class ExecutionOutputViewer extends JPanel implements ExecutionListener {

    private final JTextPane executionTextPane = new JTextPane();

    //document which contains the complete execution output
    private final ExecutionOutputDocument mainDocument = new ExecutionOutputDocument();

    public ExecutionOutputViewer() {
        setLayout(new BorderLayout());
        JScrollPane sp = new JScrollPane(
            executionTextPane,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );
        add(sp, BorderLayout.CENTER);

        executionTextPane.setDocument(mainDocument);
        executionTextPane.setBorder(new EmptyBorder(8,5,5,5));
        setPreferredSize(ChorusViewerConstants.DEFAULT_SPLIT_PANE_CONTENT_SIZE);
    }

    public void showAll() {
        executionTextPane.setDocument(mainDocument);
    }

    public void showFeature(FeatureToken f) {
        ExecutionOutputDocument d = new ExecutionOutputDocument();
        d.featureStarted(f);
        for ( ScenarioToken s : f.getScenarios()) {
            showScenario(s, d);
        }
        d.featureCompleted(f);
        executionTextPane.setDocument(d);
    }

    public void showScenario(ScenarioToken s) {
        ExecutionOutputDocument d = new ExecutionOutputDocument();
        showScenario(s, d);
        executionTextPane.setDocument(d);
    }

    private void showScenario(ScenarioToken s, ExecutionOutputDocument d) {
        d.scenarioStarted(s);
        for ( StepToken step : s.getSteps()) {
            d.stepStarted(step);
            d.stepCompleted(step);
        }
        d.scenarioCompleted(s);
    }

    public void testsStarted(ExecutionToken testExecutionToken) {
        mainDocument.testsStarted(testExecutionToken);
    }

    public void featureStarted(ExecutionToken testExecutionToken, FeatureToken feature) {
        mainDocument.featureStarted(feature);
    }

    public void featureCompleted(ExecutionToken testExecutionToken, FeatureToken feature) {
        mainDocument.featureCompleted(feature);
    }

    public void scenarioStarted(ExecutionToken testExecutionToken, ScenarioToken scenario) {
        mainDocument.scenarioStarted(scenario);
    }

    public void scenarioCompleted(ExecutionToken testExecutionToken, ScenarioToken scenario) {
        mainDocument.scenarioCompleted(scenario);
    }

    public void stepStarted(ExecutionToken testExecutionToken, StepToken step) {
        mainDocument.stepStarted(step);
    }

    public void stepCompleted(ExecutionToken testExecutionToken, StepToken step) {
        mainDocument.stepCompleted(step);
    }

    public void testsCompleted(ExecutionToken testExecutionToken, List<FeatureToken> features) {
        mainDocument.testsCompleted(testExecutionToken);
    }
}
