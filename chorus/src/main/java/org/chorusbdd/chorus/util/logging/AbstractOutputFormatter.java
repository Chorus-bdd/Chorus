package org.chorusbdd.chorus.util.logging;

import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.results.ResultsSummary;
import org.chorusbdd.chorus.results.ScenarioToken;
import org.chorusbdd.chorus.results.StepToken;
import org.chorusbdd.chorus.util.config.ChorusConfigProperty;

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

    protected static int STEP_LENGTH_CHARS;

    static {
        //why -11? we are aiming for max of 120 chars, allow for a 7 char state and a 4 char leading indent
        STEP_LENGTH_CHARS = Integer.parseInt(System.getProperty(ChorusConfigProperty.OUTPUT_FORMATTER_STEP_LENGTH_CHARS, "120")) - 11;
    }
    
    protected PrintWriter out;
    protected ScheduledFuture progressFuture;
    protected Object printLock = new Object();

    public void setPrintStream(PrintStream outStream) {
        out = new PrintWriter(outStream);
    }

    public void printFeature(FeatureToken feature) {
        out.printf("Feature: %-84s%-7s %s%n", feature.getNameWithConfiguration(), "", "");
        out.flush();
    }

    public void printScenario(ScenarioToken scenario) {
        out.printf("  Scenario: %s%n", scenario.getName());
        out.flush();
    }

    public abstract void printStepStart(StepToken step, int depth);

    public abstract void printStepEnd(StepToken step, int depth);

    protected void printStepProgress(StepToken step, StringBuilder depthPadding, int maxStepTextChars, String terminator) {
        out.printf("    " + depthPadding + "%-" + maxStepTextChars + "s" + terminator, step.toString());
        out.flush();
    }

    protected void printCompletedStep(StepToken step, StringBuilder depthPadding, int stepLengthChars) {
        out.printf("    " + depthPadding + "%-" + stepLengthChars + "s%-7s %s%n", step.toString(), step.getEndState(), step.getMessage());
        out.flush();
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
        out.print(stackTrace);
        out.flush();
    }

    public void printMessage(String message) {
        out.printf("%s%n", message);
        out.flush();
    }

    public void log(LogLevel level, Object message) {
        if ( level == LogLevel.ERROR ) {
            logErr(level, message);
        } else {
            logOut(level, message);
        }
    }

    public void logThrowable(LogLevel level, Throwable t) {
        if ( level == LogLevel.ERROR ) {
            t.printStackTrace(ChorusOut.err);
        } else {
            t.printStackTrace(out);
            out.flush();
        }
    }

    protected void logOut(LogLevel type, Object message) {
        //Use 'Chorus' instead of class name for logging, since we are testing the log output up to info level
        //and don't want refactoring the code to break tests if log statements move class
        out.println(String.format("%s --> %-7s - %s", "Chorus", type, message));
        out.flush();
    }

    protected void logErr(LogLevel type, Object message) {
        //Use 'Chorus' instead of class name for logging, since we are testing the log output up to info level
        //and don't want refactoring the code to break tests if log statements move class
        out.println(String.format("%s --> %-7s - %s", "Chorus", type, message));
        out.flush();
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
                    printStepProgress(step, depthPadding, maxStepTextChars, terminator);
                }
            }
        }

        protected abstract String getTerminator(int frameCount);
    }
}
