/**
 *  Copyright (C) 2000-2013 The Software Conservancy and Original Authors.
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
package org.chorusbdd.chorus.util.logging;

import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.results.ResultsSummary;
import org.chorusbdd.chorus.results.ScenarioToken;
import org.chorusbdd.chorus.results.StepToken;
import org.chorusbdd.chorus.util.NamedExecutors;
import org.chorusbdd.chorus.util.config.ChorusConfigProperty;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Nick E
 */
public class ConsoleOutputFormatter extends AbstractOutputFormatter {


    private static final int PROGRESS_CURSOR_FRAME_RATE = 400;
    
    
    public void printStepStart(StepToken step, int depth) {
        StringBuilder depthPadding = getDepthPadding(depth);
        int stepLengthChars = STEP_LENGTH_CHARS - depthPadding.length();
        String terminator = step.isStepMacro() ? "%n" : "|\r";
        printStepProgress(step, depthPadding, stepLengthChars, terminator);

        if ( ! step.isStepMacro()) {
            ShowStepProgress progress = new ShowStepProgressConsole(depthPadding, stepLengthChars, step);
            startStepAnimation(progress, PROGRESS_CURSOR_FRAME_RATE);
        }
    }

    public void printStepEnd(StepToken step, int depth) {
        cancelStepAnimation();
        if ( ! step.isStepMacro()) { //we don't print results for the step macro step itself but show it for each child step
            StringBuilder depthPadding = getDepthPadding(depth);
            int stepLengthChars =  STEP_LENGTH_CHARS - depthPadding.length(); 
            out.printf("    " + depthPadding + "%-" + stepLengthChars + "s%-7s %s%n", step.toString(), step.getEndState(), step.getMessage());
            out.flush();
        }
    }

    /**
     * Show step progress with a carriage return to overwrite the previously written line and an animated cursor
     */
    private class ShowStepProgressConsole extends ShowStepProgress {
        
        public ShowStepProgressConsole(StringBuilder depthPadding, int stepLengthChars, StepToken step) {
            super(depthPadding, stepLengthChars, step);
        }

        protected String getTerminator(int frameCount) {
            String terminator;
            int i = frameCount % 3;
            switch(i) {
                case 0 : terminator =  "\\\r";
                    break;
                case 1 : terminator = "/\r";
                    break;
                case 2 :
                default: terminator = "-\r";
                    break;
            }
            return terminator;
        }
    }
}
