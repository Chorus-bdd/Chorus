package org.chorusbdd.chorus.core.interpreter;

import org.chorusbdd.chorus.core.interpreter.token.FeatureToken;
import org.chorusbdd.chorus.core.interpreter.token.ScenarioToken;
import org.chorusbdd.chorus.core.interpreter.token.StepToken;
import org.chorusbdd.chorus.format.PlainResultsFormatter;
import org.chorusbdd.chorus.format.ResultsFormatter;

import java.io.PrintWriter;

/**
 * Created by: Steve Neal
 * Date: 11/01/12
 */
public class TraceListener implements ChorusInterpreterExecutionListener {

    private ResultsFormatter formatter;

    public TraceListener() {
        this.formatter = new PlainResultsFormatter(new PrintWriter(System.out, true));
    }

    public void startFeature(FeatureToken feature) {
        formatter.printFeature(feature);
    }

    public void startScenario(ScenarioToken scenario) {
        formatter.printScenario(scenario);
    }

    public void stepExecuted(StepToken step) {
        formatter.printStep(step);
        if (step.getThrowable() != null) {
            step.getThrowable().printStackTrace(System.out);
        }
    }
}
