package org.chorusbdd.chorus.output;

import org.chorusbdd.chorus.logging.LogLevel;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.results.ResultsSummary;
import org.chorusbdd.chorus.results.ScenarioToken;
import org.chorusbdd.chorus.results.StepToken;

import java.io.PrintStream;

/**
 * A null implementation of OutputFormatter
 * This should never get used
 */
class NullOutputFormatter implements OutputFormatter {

    static final NullOutputFormatter NULL_FORMATTER = new NullOutputFormatter();

    private NullOutputFormatter() {
    }

    public void setPrintStream(PrintStream out) {
        logWarning();
    }

    public void printFeature(FeatureToken feature) {
    }

    public void printScenario(ScenarioToken scenario) {
    }

    public void printStepStart(StepToken step, int depth) {
    }

    public void printStepEnd(StepToken step, int depth) {
    }

    public void printStackTrace(String stackTrace) {
    }

    public void printMessage(String message) {
    }

    public void printResults(ResultsSummary summary) {
    }

    public void log(LogLevel type, Object message) {
    }

    public void logThrowable(LogLevel type, Throwable t) {
    }

    private void logWarning() {
        System.err.println("No OutputFormatter configured, ChorusLogFactory has not been initialized properly");
    }

}
