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
import org.chorusbdd.chorus.tools.util.CompositeListener;

import javax.swing.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 15/05/12
 * Time: 15:47
 * To change this template use File | Settings | File Templates.
 */
public class ResultsPane extends JSplitPane implements ExecutionListener {

    private ExecutionToken testExecutionToken;
    private ExecutionOutputViewer executionOutputViewer = new ExecutionOutputViewer();
    private FeatureTreeViewer featureTreeViewer = new FeatureTreeViewer(executionOutputViewer);

    //proxy our events to both the executionOutputViewer and the featureTreeViewer
    private ExecutionListener proxyingListener = CompositeListener.getCompositeListener(
        ExecutionListener.class,
        executionOutputViewer,
        featureTreeViewer
    );


    public ResultsPane(ExecutionToken testExecutionToken) {
        this.testExecutionToken = testExecutionToken;
        setLeftComponent(featureTreeViewer);
        setRightComponent(executionOutputViewer);
        setResizeWeight(0.4);
    }

    public void testsStarted(ExecutionToken testExecutionToken) {
        proxyingListener.testsStarted(testExecutionToken);
    }

    public void testsCompleted(ExecutionToken testExecutionToken, List<FeatureToken> features) {
        proxyingListener.testsCompleted(testExecutionToken, features);
    }

    public void featureStarted(ExecutionToken testExecutionToken, FeatureToken feature) {
        proxyingListener.featureStarted(testExecutionToken, feature);
    }

    public void featureCompleted(ExecutionToken testExecutionToken, FeatureToken feature) {
        proxyingListener.featureCompleted(testExecutionToken, feature);
    }

    public void scenarioStarted(ExecutionToken testExecutionToken, ScenarioToken scenario) {
        proxyingListener.scenarioStarted(testExecutionToken, scenario);
    }

    public void scenarioCompleted(ExecutionToken testExecutionToken, ScenarioToken scenario) {
        proxyingListener.scenarioCompleted(testExecutionToken, scenario);
    }

    public void stepStarted(ExecutionToken testExecutionToken, StepToken step) {
        proxyingListener.stepStarted(testExecutionToken, step);
    }

    public void stepCompleted(ExecutionToken testExecutionToken, StepToken step) {
        proxyingListener.stepCompleted(testExecutionToken, step);
    }
}
