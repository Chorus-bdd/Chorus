package org.chorusbdd.chorus.tools.swing.viewer;

import org.chorusbdd.chorus.core.interpreter.ChorusExecutionListener;
import org.chorusbdd.chorus.core.interpreter.results.*;
import org.chorusbdd.chorus.tools.util.ImageUtils;

import javax.swing.*;
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
public class FeatureTreeViewer extends JPanel implements ChorusExecutionListener {

    private ExecutionOutputViewer executionOutputViewer;
    private DefaultMutableTreeNode root = new DefaultMutableTreeNode();
    private DefaultTreeModel model = new DefaultTreeModel(root);
    private JTree featureTree = new JTree(model);

    private FeatureNode currentFeature;
    private ScenarioNode currentScenario;
    private StepNode currentStep;

    public FeatureTreeViewer(ExecutionOutputViewer executionOutputViewer) {
        this.executionOutputViewer = executionOutputViewer;
        configureDisplay();
        JScrollPane p = new JScrollPane(featureTree);
        setLayout(new BorderLayout());
        add(p, BorderLayout.CENTER);
    }

    private void configureDisplay() {
        featureTree.setRootVisible(false);
        //featureTree.setBackground(Color.BLACK);
        featureTree.setCellRenderer(new ResultNodeCellRenderer());
    }

    public void testsStarted(TestExecutionToken testExecutionToken) {
    }

    public void featureStarted(TestExecutionToken testExecutionToken, FeatureToken feature) {
        currentFeature = addNode(new FeatureNode(feature), root, true);
    }

    public void featureCompleted(TestExecutionToken testExecutionToken, FeatureToken feature) {
        //if we have received the updated token over the wire, the completed instance may be != the started instance
        //completed instance has the finished state, so update our token reference to that
        currentFeature.setToken(feature);
        currentFeature = null;
    }

    public void scenarioStarted(TestExecutionToken testExecutionToken, ScenarioToken scenario) {
        currentScenario = addNode(new ScenarioNode(scenario), currentFeature, true);
    }

    public void scenarioCompleted(TestExecutionToken testExecutionToken, ScenarioToken scenario) {
        //if we have received the updated token over the wire, the completed instance may be != the started instance
        //completed instance has the finished state, so update our token reference to that
        currentScenario.setToken(scenario);
        featureTree.collapsePath(new TreePath(currentScenario.getPath()));  //collapse down the detail nodes once feature finished
        currentScenario = null;
    }

    public void stepStarted(TestExecutionToken testExecutionToken, StepToken step) {
        currentStep = addNode(new StepNode(step), currentScenario, true);
    }

    public void stepCompleted(TestExecutionToken testExecutionToken, StepToken step) {
        //if we have received the updated token over the wire, the completed instance may be != the started instance
        //completed instance has the finished state, so update our token reference to that
        currentStep.setToken(step);
        currentStep = null;
    }

    public void testsCompleted(TestExecutionToken testExecutionToken, List<FeatureToken> features) {
        //make sure we repaint to show the latest state
    }

    private <T extends AbstractTokenTreeNode> T addNode(T newNode, MutableTreeNode parent, boolean showNode) {
        model.insertNodeInto(newNode, parent, parent.getChildCount());
        if (showNode) {
            featureTree.scrollPathToVisible(new TreePath(newNode.getPath()));
        }
        return newNode;
    }

    private abstract class AbstractTokenTreeNode<N extends ResultToken> extends DefaultMutableTreeNode {

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
            return currentFeature;
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
            return currentScenario;
        }
    }

    private class StepNode extends AbstractTokenTreeNode<StepToken>  {

        public StepNode(StepToken token) {
            super(token);
        }

        protected ImageIcon getFailedIcon() {
            return ImageUtils.STEP_FAILED;
        }

        protected ImageIcon getNotImplementedIcon() {
            return ImageUtils.STEP_NOT_IMPLEMENTED;
        }

        protected ImageIcon getOkIcon() {
            return ImageUtils.STEP_OK;
        }

        protected ImageIcon getInProgressIcon() {
            return ImageUtils.STEP_IN_PROGRESS;
        }

        protected AbstractTokenTreeNode<StepToken> getCurrentlyProcessingNode() {
            return currentStep;
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
            //setBackgroundNonSelectionColor(Color.BLACK);
            //setBackgroundSelectionColor(Color.GRAY);
            //setForeground(Color.WHITE);
            return this;
        }
    }
}
