package org.chorusbdd.chorus.executionlistener;

import org.chorusbdd.chorus.core.interpreter.ChorusInterpreterExecutionListener;
import org.chorusbdd.chorus.core.interpreter.results.FeatureToken;
import org.chorusbdd.chorus.core.interpreter.results.ResultsSummary;
import org.chorusbdd.chorus.core.interpreter.results.ScenarioToken;
import org.chorusbdd.chorus.core.interpreter.results.StepToken;

import java.io.PrintWriter;

/**
 * Created by: Steve Neal
 * Date: 11/01/12
 */
public class SystemOutExecutionListener implements ChorusInterpreterExecutionListener {

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

    public void featureStarted(FeatureToken feature) {
        if ( trace ) {
            formatter.printFeature(feature);
        }
    }

    public void scenarioStarted(ScenarioToken scenario) {
        if ( trace ) {
            formatter.printScenario(scenario);
        }
    }

    public void stepExecuted(StepToken step) {
        if ( trace ) {
            formatter.printStep(step);
            if (step.getThrowable() != null) {
                step.getThrowable().printStackTrace(System.out);
            }
        }
    }

    public void testsCompleted(ResultsSummary results) {
        if (showSummary) {
            formatter.printResults(results.getFeatures(), verbose, results);
        } else {
            formatter.printResults(results.getFeatures(), verbose);
        }
        formatter.close();
    }
}
