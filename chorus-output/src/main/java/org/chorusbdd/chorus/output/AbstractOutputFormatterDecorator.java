package org.chorusbdd.chorus.output;

import org.chorusbdd.chorus.logging.LogLevel;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.results.ResultsSummary;
import org.chorusbdd.chorus.results.ScenarioToken;
import org.chorusbdd.chorus.results.StepToken;

/**
 * Created by GA2EBBU on 14/01/2015.
 *
 * A base class for decorators which forwards all calls
 */
public abstract class AbstractOutputFormatterDecorator implements OutputFormatter {

    private OutputFormatter wrappedFormatter;

    public AbstractOutputFormatterDecorator(OutputFormatter wrappedFormatter) {
        this.wrappedFormatter = wrappedFormatter;
    }

    @Override
    public void printFeature(FeatureToken feature) {
        wrappedFormatter.printFeature(feature);
    }

    @Override
    public void printScenario(ScenarioToken scenario) {
        wrappedFormatter.printScenario(scenario);
    }

    @Override
    public void printStepStart(StepToken step, int depth) {
        wrappedFormatter.printStepStart(step, depth);
    }

    @Override
    public void printStepEnd(StepToken step, int depth) {
        wrappedFormatter.printStepEnd(step, depth);
    }

    @Override
    public void printStackTrace(String stackTrace) {
        wrappedFormatter.printStackTrace(stackTrace);
    }

    @Override
    public void printMessage(String message) {
        wrappedFormatter.printMessage(message);
    }

    @Override
    public void printResults(ResultsSummary summary) {
        wrappedFormatter.printResults(summary);
    }

    @Override
    public void log(LogLevel type, Object message) {
        wrappedFormatter.log(type, message);
    }

    @Override
    public void logError(LogLevel type, Throwable t) {
        wrappedFormatter.logError(type, t);
    }

    public void dispose() {
        wrappedFormatter.dispose();
    }
}
