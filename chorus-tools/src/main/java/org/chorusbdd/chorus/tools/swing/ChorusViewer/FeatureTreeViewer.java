package org.chorusbdd.chorus.tools.swing.chorusviewer;

import org.chorusbdd.chorus.core.interpreter.ChorusExecutionListener;
import org.chorusbdd.chorus.core.interpreter.results.*;
import org.chorusbdd.chorus.tools.util.ImageUtils;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 15/05/12
 * Time: 16:03
 * To change this template use File | Settings | File Templates.
 */
public class FeatureTreeViewer extends JPanel implements ChorusExecutionListener {

    private ExecutionOutputViewer executionOutputViewer;
    private MutableTreeNode root = new DefaultMutableTreeNode();
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
        featureTree.setBackground(Color.BLACK);
        featureTree.setCellRenderer(new ResultNodeCellRenderer());
    }

    public void testsStarted(TestExecutionToken testExecutionToken) {
    }

    public void featureStarted(TestExecutionToken testExecutionToken, FeatureToken feature) {
        currentFeature = addNode(new FeatureNode(feature), root, true);
    }

    public void featureCompleted(TestExecutionToken testExecutionToken, FeatureToken feature) {
        currentFeature = null;
        featureTree.repaint(); //make sure we update icon to show no longer in progress
    }

    public void scenarioStarted(TestExecutionToken testExecutionToken, ScenarioToken scenario) {
        currentScenario = addNode(new ScenarioNode(scenario), currentFeature, true);
    }

    public void scenarioCompleted(TestExecutionToken testExecutionToken, ScenarioToken scenario) {
        currentScenario = null;
        featureTree.repaint(); //make sure we update icon to show no longer in progress
    }

    public void stepStarted(TestExecutionToken testExecutionToken, StepToken step) {
        currentStep = addNode(new StepNode(step), currentScenario, true);
    }

    public void stepCompleted(TestExecutionToken testExecutionToken, StepToken step) {
        currentStep = null;
        featureTree.repaint(); //make sure we update icon to show no longer in progress
    }

    public void testsCompleted(TestExecutionToken testExecutionToken, ResultsSummary results) {
    }

    private <T extends AbstractTokenTreeNode> T addNode(T newNode, MutableTreeNode parent, boolean showNode) {
        model.insertNodeInto(newNode, parent, parent.getChildCount());
        if (showNode) {
            featureTree.scrollPathToVisible(new TreePath(newNode.getPath()));
        }
        return newNode;
    }

    private abstract static class AbstractTokenTreeNode<N extends ResultToken> extends DefaultMutableTreeNode {

        private N token;

        public AbstractTokenTreeNode(N token) {
            this.token = token;
        }

        public N getToken() {
            return token;
        }

        public String toString() {
            return token.toString();
        }

        public abstract ImageIcon getIcon();
    }

    private class FeatureNode extends AbstractTokenTreeNode<FeatureToken>  {

        public FeatureNode(FeatureToken token) {
            super(token);
        }

        public String toString() {
            return getToken().getNameWithConfiguration();
        }

        public ImageIcon getIcon() {
            return this == currentFeature ? ImageUtils.FEATURE_IN_PROGRESS : ImageUtils.FEATURE_OK;
        }
    }

    private class ScenarioNode extends AbstractTokenTreeNode<ScenarioToken>  {

        public ScenarioNode(ScenarioToken token) {
            super(token);
        }

        public String toString() {
            return getToken().getName();
        }

        public ImageIcon getIcon() {
            return this == currentScenario ? ImageUtils.SCENARIO_IN_PROGRESS : ImageUtils.SCENARIO_OK;
        }
    }

    private class StepNode extends AbstractTokenTreeNode<StepToken>  {

        public StepNode(StepToken token) {
            super(token);
        }


        public ImageIcon getIcon() {
            return this == currentStep ? ImageUtils.STEP_IN_PROGRESS : ImageUtils.STEP_OK;
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
            setBackgroundNonSelectionColor(Color.BLACK);
            setBackgroundSelectionColor(Color.GRAY);
            setForeground(Color.WHITE);
            return this;
        }
    }
}
