package org.chorusbdd.chorus.executionlistener;

import org.chorusbdd.chorus.core.interpreter.ChorusExecutionListener;
import org.chorusbdd.chorus.core.interpreter.results.*;

import java.io.PrintWriter;

/**
 * Created by: Steve Neal
 * Date: 11/01/12
 */
public class SystemOutExecutionListener implements ChorusExecutionListener {

    private ResultsFormatter formatter;

    private boolean showSummary = true;
    private boolean verbose = false;
    private boolean trace = true;

    public SystemOutExecutionListener() {
        this(true, false, true);
    }

    public SystemOutExecutionListener(boolean showSummary, boolean verbose, boolean trace) {
        this.trace = trace;
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
        if ( trace ) {
            formatter.printFeature(feature);
        }
    }

    public void featureCompleted(TestExecutionToken testExecutionToken, FeatureToken feature) {
    }

    public void scenarioStarted(TestExecutionToken testExecutionToken, ScenarioToken scenario) {
        if ( trace ) {
            formatter.printScenario(scenario);
        }
    }

    public void scenarioCompleted(TestExecutionToken testExecutionToken, ScenarioToken scenario) {
    }

    public void stepStarted(TestExecutionToken testExecutionToken, StepToken step) {
    }

    public void stepCompleted(TestExecutionToken testExecutionToken, StepToken step) {
        if ( trace ) {
            formatter.printStep(step);
            if (step.getThrowable() != null) {
                step.getThrowable().printStackTrace(System.out);
            }
        }
    }

    public void testsCompleted(TestExecutionToken testExecutionToken, ResultsSummary results) {
        if (showSummary) {
            formatter.printResults(results.getFeatures(), verbose, results);
        } else {
            formatter.printResults(results.getFeatures(), verbose);
        }
        formatter.close();
    }
}
