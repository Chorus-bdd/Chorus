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
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Nick Ebbutt
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
        setSize(ChorusViewerConstants.DEFAULT_INITIAL_FRAME_SIZE);
        setLocationRelativeTo(null); //centre on screen
    }

    public void testsStarted(TestExecutionToken testExecutionToken) {
        ResultsPane resultPane = new ResultsPane(testExecutionToken);
        executionTokenToResultsPaneMap.put(testExecutionToken, resultPane);
        resultsTabbedPane.addTab(testExecutionToken.toString(), resultPane);
        resultsTabbedPane.setSelectedComponent(resultPane);
        resultPane.testsStarted(testExecutionToken);
    }

    public void featureStarted(TestExecutionToken testExecutionToken, FeatureToken feature) {
        executionTokenToResultsPaneMap.get(testExecutionToken).featureStarted(testExecutionToken, feature);
    }

    public void featureCompleted(TestExecutionToken testExecutionToken, FeatureToken feature) {
        executionTokenToResultsPaneMap.get(testExecutionToken).featureCompleted(testExecutionToken, feature);
    }

    public void scenarioStarted(TestExecutionToken testExecutionToken, ScenarioToken scenario) {
        executionTokenToResultsPaneMap.get(testExecutionToken).scenarioStarted(testExecutionToken, scenario);
    }

    public void scenarioCompleted(TestExecutionToken testExecutionToken, ScenarioToken scenario) {
        executionTokenToResultsPaneMap.get(testExecutionToken).scenarioCompleted(testExecutionToken, scenario);
    }

    public void stepStarted(TestExecutionToken testExecutionToken, StepToken step) {
        executionTokenToResultsPaneMap.get(testExecutionToken).stepStarted(testExecutionToken, step);
    }

    public void stepCompleted(TestExecutionToken testExecutionToken, StepToken step) {
        executionTokenToResultsPaneMap.get(testExecutionToken).stepCompleted(testExecutionToken, step);
    }

    public void testsCompleted(TestExecutionToken testExecutionToken, List<FeatureToken> features) {
        executionTokenToResultsPaneMap.get(testExecutionToken).testsCompleted(testExecutionToken, features);
    }

}
