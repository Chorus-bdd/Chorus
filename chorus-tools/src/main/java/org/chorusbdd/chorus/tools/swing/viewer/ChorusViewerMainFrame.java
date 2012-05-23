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
import org.chorusbdd.chorus.tools.util.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
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

    private JTabbedPane tabbedPane = new JTabbedPane();

    private Map<TestExecutionToken, ResultsPane> executionTokenToResultsPaneMap = new HashMap<TestExecutionToken, ResultsPane>();
    private Map<TestExecutionToken, ResultsTabComponent> executionTokenToTabComponent = new HashMap<TestExecutionToken, ResultsTabComponent>();


    public ChorusViewerMainFrame() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
        setSize(ChorusViewerConstants.DEFAULT_INITIAL_FRAME_SIZE);
        setTitle("Chorus Viewer");
        setLocationRelativeTo(null); //centre on screen
    }

    public void testsStarted(TestExecutionToken testExecutionToken) {
        ResultsPane resultPane = new ResultsPane(testExecutionToken);
        executionTokenToResultsPaneMap.put(testExecutionToken, resultPane);
        ResultsTabComponent t = new ResultsTabComponent(testExecutionToken);
        executionTokenToTabComponent.put(testExecutionToken, t);
        tabbedPane.addTab(testExecutionToken.toString(), resultPane);
        setTabComponentUsingReflection(t);
        tabbedPane.setSelectedComponent(resultPane);
        resultPane.testsStarted(testExecutionToken);
    }

    //do this using reflection, because the method does not exist in jdk < 1.5
    //we want to maintain compatibility
    private void setTabComponentUsingReflection(ResultsTabComponent t) {
        try {
            Method m = JTabbedPane.class.getMethod("setTabComponentAt", int.class, Component.class);
            m.invoke(tabbedPane, tabbedPane.getTabCount() - 1, t);
        } catch (Exception e) {
            e.printStackTrace();
            //probably no jdk 1.6, this method is 1.6+
        }
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

        //update the tab to show the latest results from the test execution token
        executionTokenToTabComponent.get(testExecutionToken).setTestExecutionToken(testExecutionToken);
    }

    public void testsCompleted(TestExecutionToken testExecutionToken, List<FeatureToken> features) {
        executionTokenToResultsPaneMap.get(testExecutionToken).testsCompleted(testExecutionToken, features);
    }

    public void removeTab(TestExecutionToken t) {
        ResultsPane p = executionTokenToResultsPaneMap.get(t);
        for ( int index=0; index < tabbedPane.getTabCount(); index ++) {
            if ( tabbedPane.getComponentAt(index) == p) {
                tabbedPane.removeTabAt(index);
                break;
            }
        }
    }

    private class ResultsTabComponent extends JPanel {

        private JLabel resultsLabel = new JLabel();
        private JButton closeButton = new JButton("X");

        public ResultsTabComponent(final TestExecutionToken t) {
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            add(resultsLabel);
            add(closeButton);
            closeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    removeTab(t);
                }
            });
            configureLabel(t);
            resultsLabel.setOpaque(false);
            setOpaque(false);
            closeButton.setOpaque(false);
            closeButton.setBorderPainted(false);
            closeButton.setContentAreaFilled(false);
            closeButton.setIconTextGap(0);
        }

        //we get a new instance on each step with updated metadata
        public void setTestExecutionToken(TestExecutionToken t) {
            configureLabel(t);
            repaint();
        }

        private void configureLabel(TestExecutionToken t) {
            ImageIcon i = t.isPassedAndFullyImplemented() ? ImageUtils.SCENARIO_OK :
                    t.isPassed() ? ImageUtils.SCENARIO_NOT_IMPLEMENTED : ImageUtils.SCENARIO_FAILED;
            resultsLabel.setIcon(i);
            resultsLabel.setText(getTabText(t));
        }

        private String getTabText(TestExecutionToken t) {
            StringBuilder sb = new StringBuilder("<html>");
            sb.append(t.toString());
            sb.append("<font color='green'>&nbsp;&nbsp;");
            sb.append(t.getStepsPassed());
            sb.append(" </font><font color='#FFC90E'>&nbsp;&nbsp;");
            sb.append(t.getUndefinedPendingOrSkipped());
            sb.append(" </font><font color='red'>&nbsp;&nbsp;");
            sb.append(t.getStepsFailed());
            sb.append(" </html>");
            return sb.toString();
        }
    }
}
