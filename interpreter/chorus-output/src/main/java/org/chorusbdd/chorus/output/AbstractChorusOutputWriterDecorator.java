/**
 * MIT License
 *
 * Copyright (c) 2019 Chorus BDD Organisation.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.chorusbdd.chorus.output;

import org.chorusbdd.chorus.logging.LogLevel;
import org.chorusbdd.chorus.results.*;

import java.util.List;
import java.util.Set;

/**
 * Created by Nick E on 14/01/2015.
 *
 * A base class for decorators which forwards all calls
 */
public abstract class AbstractChorusOutputWriterDecorator implements ChorusOutputWriter {

    private ChorusOutputWriter wrappedFormatter;

    public AbstractChorusOutputWriterDecorator(ChorusOutputWriter wrappedFormatter) {
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
    public void printResults(ResultsSummary summary, List<FeatureToken> featureList, Set<CataloguedStep> cataloguedSteps) {
        wrappedFormatter.printResults(summary, featureList, cataloguedSteps);
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
