package org.chorusbdd.chorus.executionlistener;

import org.chorusbdd.chorus.core.interpreter.ChorusExecutionListener;
import org.chorusbdd.chorus.core.interpreter.results.*;

import java.io.PrintWriter;
import java.util.List;

/**
 * Created by: Steve Neal
 * Date: 11/01/12
 */
public class SystemOutExecutionListener implements ChorusExecutionListener {

    private ResultsFormatter formatter;

    private boolean showSummary = true;
    private boolean verbose = false;

    public SystemOutExecutionListener() {
        this(true, false);
    }

    public SystemOutExecutionListener(boolean showSummary, boolean verbose) {
        this.formatter = new PlainResultsFormatter(new PrintWriter(System.out, true));
        this.showSummary = showSummary;
        this.verbose = verbose;
    }

    public void setFormatter(ResultsFormatter formatter) {
        this.formatter = formatter;
    }

    public void testsStarted(TestExecutionToken testExecutionToken) {
    }

    public void featureStarted(TestExecutionToken testExecutionToken, FeatureToken feature) {
        formatter.printFeature(feature);
    }

    public void featureCompleted(TestExecutionToken testExecutionToken, FeatureToken feature) {
        if (! feature.foundAllHandlers()) {
            formatter.printMessage(feature.getUnavailableHandlersMessage());
        }
    }

    public void scenarioStarted(TestExecutionToken testExecutionToken, ScenarioToken scenario) {
        formatter.printScenario(scenario);
    }

    public void scenarioCompleted(TestExecutionToken testExecutionToken, ScenarioToken scenario) {
    }

    public void stepStarted(TestExecutionToken testExecutionToken, StepToken step) {
    }

    public void stepCompleted(TestExecutionToken testExecutionToken, StepToken step) {
        formatter.printStep(step);
        if (step.getThrowable() != null && verbose) {
            formatter.printStackTrace(step.getThrowable());
        }
    }

    public void testsCompleted(TestExecutionToken testExecutionToken, List<FeatureToken> features) {
        if (showSummary) {
            formatter.printResults(testExecutionToken.getResultsSummary());
        }
        formatter.close();
    }
}
