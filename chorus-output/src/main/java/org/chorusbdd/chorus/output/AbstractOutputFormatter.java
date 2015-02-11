/**
 *  Copyright (C) 2000-2014 The Software Conservancy and Original Authors.
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
package org.chorusbdd.chorus.output;

import org.chorusbdd.chorus.logging.ChorusOut;
import org.chorusbdd.chorus.logging.LogLevel;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.results.ResultsSummary;
import org.chorusbdd.chorus.results.ScenarioToken;
import org.chorusbdd.chorus.results.StepToken;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * User: nick
 * Date: 12/02/14
 * Time: 19:06
 */
abstract class AbstractOutputFormatter implements OutputFormatter {

    private static ScheduledExecutorService stepProgressExecutorService = Executors.newSingleThreadScheduledExecutor();

    private PrintStream printStream;
    private PrintWriter printWriter;

    private int STEP_LENGTH_CHARS;

    public AbstractOutputFormatter() {
        //why -11? we are aiming for max of 120 chars, allow for a 7 char state and a 4 char leading indent
        STEP_LENGTH_CHARS = Integer.parseInt(System.getProperty(OUTPUT_FORMATTER_STEP_LENGTH_CHARS, "120")) - 11;
    }
    
    protected ScheduledFuture progressFuture;
    protected Object printLock = new Object();

    public void printFeature(FeatureToken feature) {
        getOutWriter().printf("Feature: %-84s%-7s %s%n", feature.getNameWithConfiguration(), "", "");
        getOutWriter().flush();
    }

    public void printScenario(ScenarioToken scenario) {
        getOutWriter().printf("  Scenario: %s%n", scenario.getName());
        getOutWriter().flush();
    }

    public abstract void printStepStart(StepToken step, int depth);

    public abstract void printStepEnd(StepToken step, int depth);

    protected void printStepWithoutEndState(StepToken step, StringBuilder depthPadding, int maxStepTextChars, String terminator) {
        getOutWriter().printf("    " + depthPadding + "%-" + maxStepTextChars + "s" + terminator, step.toString());
        getOutWriter().flush();
    }

    protected void printCompletedStep(StepToken step, StringBuilder depthPadding, int stepLengthChars) {
        getOutWriter().printf("    " + depthPadding + "%-" + stepLengthChars + "s%-7s %s%n", step.toString(), step.getEndState(), step.getMessage());
        getOutWriter().flush();
    }

    protected void startProgressTask(Runnable progress, int frameRate) {
        progressFuture = stepProgressExecutorService.scheduleWithFixedDelay(progress, frameRate, frameRate, TimeUnit.MILLISECONDS);
    }

    protected void cancelStepAnimation() {
        synchronized (printLock) {
            if ( progressFuture != null) {
                progressFuture.cancel(false);
                progressFuture = null;
            }
        }
    }
    
    protected StringBuilder getDepthPadding(int depth) {
        StringBuilder sb = new StringBuilder();
        for ( int loop=1; loop < depth; loop++ ) {
            sb.append("..");
        }
        if ( depth > 1 ) {
            sb.append(" ");
        }
        return sb;
    }

    public void printResults(ResultsSummary s) {
        if (s != null) {
            //only show the pending count if there were pending steps, makes the summary more legible
            if ( s.getFeaturesPending() > 0) {
                printMessage(String.format("%nFeatures  (total:%d) (passed:%d) (pending:%d) (failed:%d)",
                        s.getTotalFeatures(),
                        s.getFeaturesPassed(),
                        s.getFeaturesPending(),
                        s.getFeaturesFailed()));
            } else {
                printMessage(String.format("%nFeatures  (total:%d) (passed:%d) (failed:%d)",
                        s.getTotalFeatures(),
                        s.getFeaturesPassed(),
                        s.getFeaturesFailed()));
            }

            //only show the pending count if there were pending steps, makes the summary more legible
            if ( s.getScenariosPending() > 0 ) {
                //print scenarios summary
                printMessage(String.format("Scenarios (total:%d) (passed:%d) (pending:%d) (failed:%d)",
                        s.getTotalScenarios(),
                        s.getScenariosPassed(),
                        s.getScenariosPending(),
                        s.getScenariosFailed()));
            } else {
                //print scenarios summary
                printMessage(String.format("Scenarios (total:%d) (passed:%d) (failed:%d)",
                        s.getTotalScenarios(),
                        s.getScenariosPassed(),
                        s.getScenariosFailed()));
            }

            //print steps summary
            printMessage(String.format("Steps     (total:%d) (passed:%d) (failed:%d) (undefined:%d) (pending:%d) (skipped:%d)",
                    s.getStepsPassed() + s.getStepsFailed() + s.getStepsUndefined() + s.getStepsPending() + s.getStepsSkipped(),
                    s.getStepsPassed(),
                    s.getStepsFailed(),
                    s.getStepsUndefined(),
                    s.getStepsPending(),
                    s.getStepsSkipped()));
        }
    }

    public void printStackTrace(String stackTrace) {
        getOutWriter().print(stackTrace);
        getOutWriter().flush();
    }

    public void printMessage(String message) {
        getOutWriter().printf("%s%n", message);
        getOutWriter().flush();
    }

    public void log(LogLevel level, Object message) {
        if ( level == LogLevel.ERROR ) {
            logErr(level, message);
        } else {
            logOut(level, message);
        }
    }

    public void logError(LogLevel level, Throwable t) {
        if ( level == LogLevel.ERROR ) {
            t.printStackTrace(ChorusOut.err);
        } else {
            t.printStackTrace(getOutWriter());
            getOutWriter().flush();
        }
    }

    protected void logOut(LogLevel type, Object message) {
        //Use 'Chorus' instead of class name for logging, since we are testing the log output up to info level
        //and don't want refactoring the code to break tests if log statements move class
        getOutWriter().println(String.format("%s --> %-7s - %s", "Chorus", type, message));
        getOutWriter().flush();
    }

    protected void logErr(LogLevel type, Object message) {
        //Use 'Chorus' instead of class name for logging, since we are testing the log output up to info level
        //and don't want refactoring the code to break tests if log statements move class
        getOutWriter().println(String.format("%s --> %-7s - %s", "Chorus", type, message));
        getOutWriter().flush();
    }

    protected int getStepLengthCharCount() {
        return STEP_LENGTH_CHARS;
    }

    protected abstract class StepProgressRunnable implements Runnable {
        private StringBuilder depthPadding;
        private int maxStepTextChars;
        private StepToken step;
        private int frameCount = 0;

        public StepProgressRunnable(StringBuilder depthPadding, int maxStepTextChars, StepToken step) {
            this.depthPadding = depthPadding;
            this.maxStepTextChars = maxStepTextChars;
            this.step = step;
        }

        public void run() {
            synchronized (printLock) {
                String terminator;
                if ( ! progressFuture.isCancelled()) {
                    frameCount++;
                    terminator = getTerminator(frameCount);
                    printStepWithoutEndState(step, depthPadding, maxStepTextChars, terminator);
                }
            }
        }

        protected abstract String getTerminator(int frameCount);
    }


    protected PrintWriter getOutWriter() {
        if ( printWriter == null || printStream != ChorusOut.out) {
            printWriter = new PrintWriter(ChorusOut.out);
            printStream = ChorusOut.out;
        }
        return printWriter;
    }

    public void dispose() {
        //Nothing to do since don't need to close system out
    }

}
