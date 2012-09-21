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
import org.chorusbdd.chorus.core.interpreter.results.*;
import org.chorusbdd.chorus.tools.util.ImageUtils;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 15/05/12
 * Time: 16:03
 * To change this template use File | Settings | File Templates.
 */
public class FeatureTreeViewer extends JPanel implements ExecutionListener {

    private ExecutionOutputViewer executionOutputViewer;
    private DefaultTreeModel model = new DefaultTreeModel(new DefaultMutableTreeNode());
    private JTree featureTree = new JTree(model);

    private SuiteNode rootNode;
    private FeatureNode currentFeatureNode;
    private ScenarioNode currentScenarioNode;
    private SuiteNode currentSuiteNode;

    public FeatureTreeViewer(ExecutionOutputViewer executionOutputViewer) {
        this.executionOutputViewer = executionOutputViewer;
        configureDisplay();
        JScrollPane p = new JScrollPane(
            featureTree,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        setLayout(new BorderLayout());
        add(p, BorderLayout.CENTER);
        setPreferredSize(ChorusViewerConstants.DEFAULT_SPLIT_PANE_CONTENT_SIZE);
        addSelectionListener();
    }

    private void addSelectionListener() {
        featureTree.addTreeSelectionListener(new ShowSelectedNodeListener());
    }

    private void configureDisplay() {
        //featureTree.setRootVisible(false);
        featureTree.setCellRenderer(new ResultNodeCellRenderer());
    }

    public void testsStarted(ExecutionToken testExecutionToken) {
        rootNode = new SuiteNode(testExecutionToken);
        currentSuiteNode = rootNode;
        model.setRoot(currentSuiteNode);
    }

    public void featureStarted(ExecutionToken testExecutionToken, FeatureToken feature) {
        currentFeatureNode = addNode(new FeatureNode(feature), currentSuiteNode, true);
    }

    public void featureCompleted(ExecutionToken testExecutionToken, FeatureToken feature) {
        //if we have received the updated token over the wire, the completed instance may be != the started instance
        //completed instance has the finished state, so update our token reference to that
        currentFeatureNode.setToken(feature);
        currentFeatureNode = null;
    }

    public void scenarioStarted(ExecutionToken testExecutionToken, ScenarioToken scenario) {
        currentScenarioNode = addNode(new ScenarioNode(scenario), currentFeatureNode, true);
    }

    public void scenarioCompleted(ExecutionToken testExecutionToken, ScenarioToken scenario) {
        //if we have received the updated token over the wire, the completed instance may be != the started instance
        //completed instance has the finished state, so update our token reference to that
        currentScenarioNode.setToken(scenario);
        featureTree.collapsePath(new TreePath(currentScenarioNode.getPath()));  //collapse down the detail nodes once feature finished
        currentScenarioNode = null;
    }

    public void stepStarted(ExecutionToken testExecutionToken, StepToken step) {
    }

    public void stepCompleted(ExecutionToken testExecutionToken, StepToken step) {
    }

    public void testsCompleted(ExecutionToken testExecutionToken, List<FeatureToken> features) {
        currentSuiteNode.setToken(testExecutionToken);
        currentSuiteNode = null; //otherwise we never 'stop processing' root node and renders as in progress
        featureTree.repaint();
    }

    private <T extends AbstractTokenTreeNode> T addNode(T newNode, MutableTreeNode parent, boolean showNode) {
        model.insertNodeInto(newNode, parent, parent.getChildCount());
        if (showNode) {
            featureTree.scrollPathToVisible(new TreePath(newNode.getPath()));
        }
        return newNode;
    }

    private abstract class AbstractTokenTreeNode<N extends Token> extends DefaultMutableTreeNode {

        private N token;

        public AbstractTokenTreeNode(N token) {
            this.token = token;
        }

        public N getToken() {
            return token;
        }

        public void setToken(N token) {
            this.token = token;
            model.nodeChanged(this);  //repaint to show the latest state from token
        }

        public String toString() {
            return token.toString();
        }


        public ImageIcon getIcon() {
            return this == getCurrentlyProcessingNode() ? getInProgressIcon() :
                    token.isPassed() ?
                        token.isFullyImplemented() ? getOkIcon() : getNotImplementedIcon() :
                        getFailedIcon();

        }

        protected abstract ImageIcon getFailedIcon();

        protected abstract ImageIcon getNotImplementedIcon();

        protected abstract ImageIcon getOkIcon();

        protected abstract ImageIcon getInProgressIcon();

        protected abstract AbstractTokenTreeNode<N> getCurrentlyProcessingNode();

    }

    private class FeatureNode extends AbstractTokenTreeNode<FeatureToken>  {

        public FeatureNode(FeatureToken token) {
            super(token);
        }

        public String toString() {
            return getToken().getNameWithConfiguration();
        }

        protected ImageIcon getFailedIcon() {
            return ImageUtils.FEATURE_FAILED;
        }

        protected ImageIcon getNotImplementedIcon() {
            return ImageUtils.FEATURE_NOT_IMPLEMENTED;
        }

        protected ImageIcon getOkIcon() {
            return ImageUtils.FEATURE_OK;
        }

        protected ImageIcon getInProgressIcon() {
            return ImageUtils.FEATURE_IN_PROGRESS;
        }

        protected AbstractTokenTreeNode<FeatureToken> getCurrentlyProcessingNode() {
            return currentFeatureNode;
        }
    }

    private class ScenarioNode extends AbstractTokenTreeNode<ScenarioToken>  {

        public ScenarioNode(ScenarioToken token) {
            super(token);
        }

        public String toString() {
            return getToken().getName();
        }

        protected ImageIcon getFailedIcon() {
            return ImageUtils.SCENARIO_FAILED;
        }

        protected ImageIcon getNotImplementedIcon() {
            return ImageUtils.SCENARIO_NOT_IMPLEMENTED;
        }

        protected ImageIcon getOkIcon() {
            return ImageUtils.SCENARIO_OK;
        }

        protected ImageIcon getInProgressIcon() {
            return ImageUtils.SCENARIO_IN_PROGRESS;
        }

        protected AbstractTokenTreeNode<ScenarioToken> getCurrentlyProcessingNode() {
            return currentScenarioNode;
        }
    }
    
    private class SuiteNode extends AbstractTokenTreeNode<ExecutionToken>  {

        public SuiteNode(ExecutionToken token) {
            super(token);
        }

        public String toString() {
            return getToken().getTestSuiteName();
        }

        protected ImageIcon getFailedIcon() {
            return ImageUtils.SUITE_FAILED;
        }

        protected ImageIcon getNotImplementedIcon() {
            return ImageUtils.SUITE_NOT_IMPLEMENTED;
        }

        protected ImageIcon getOkIcon() {
            return ImageUtils.SUITE_OK;
        }

        protected ImageIcon getInProgressIcon() {
            return ImageUtils.SUITE_IN_PROGRESS;
        }

        protected AbstractTokenTreeNode<ExecutionToken> getCurrentlyProcessingNode() {
            return currentSuiteNode;
        }
    }

    private static class ResultNodeCellRenderer extends DefaultTreeCellRenderer {

        public Component getTreeCellRendererComponent(JTree tree, Object value,
                                               boolean selected, boolean expanded,
                                               boolean leaf, int row, boolean hasFocus) {


            if ( value instanceof AbstractTokenTreeNode) {  //not so for root node
                ImageIcon icon = ((AbstractTokenTreeNode) value).getIcon();
                setClosedIcon(icon);
                setOpenIcon(icon);
                setLeafIcon(icon);
            }

            super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            return this;
        }
    }

    /**
     * Show the execution output for just the selected tree node
     */
    private class ShowSelectedNodeListener implements TreeSelectionListener {

        public void valueChanged(TreeSelectionEvent e) {
            AbstractTokenTreeNode n = (AbstractTokenTreeNode)e.getNewLeadSelectionPath().getLastPathComponent();
            if ( n == rootNode) {
                executionOutputViewer.showAll();
            } else if ( n instanceof FeatureNode) {
                executionOutputViewer.showFeature(((FeatureNode)n).getToken());
            } else if ( n instanceof ScenarioNode) {
                executionOutputViewer.showScenario(((ScenarioNode)n).getToken());
            }
        }
    }
}
