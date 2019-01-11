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

import org.chorusbdd.chorus.results.StepToken;

/**
 * Nick E
 * 
 * A plain formatter which does not require a console and does not use carriage return to provide animated step progress
 * 
 * Long running steps have time logged every 10 seconds
 * Set the system property chorusOutputFormatterStepLogRate to -1 to turn this off, or to an alternative length in milliseconds
 */
public final class PlainOutputWriter extends AbstractChorusOutputWriter {

    /**
     * Set to -1 to turn off progress step logging
     */
    private static int PROGRESS_CURSOR_FRAME_RATE;
    
    static {
        try {
            PROGRESS_CURSOR_FRAME_RATE = Integer.parseInt(System.getProperty(OUTPUT_FORMATTER_STEP_LOG_RATE, "10")) * 1000;
        } catch (NumberFormatException e) {
            System.err.println("Sys property " + OUTPUT_FORMATTER_STEP_LOG_RATE + " must be an integer number of seconds");
            PROGRESS_CURSOR_FRAME_RATE = 10000;
        }
    }

    /**
     * Create a results formatter which outputs results
     * All OutputFormatter must have a no argument constructor
     */
    public PlainOutputWriter() {}

    public void printStepStart(StepToken step, int depth) {
        StringBuilder depthPadding = getDepthPadding(depth);
        int stepLengthChars = getStepLengthCharCount() - depthPadding.length();
        if ( step.isStepMacro() ) {
            printStepWithoutEndState(step, depthPadding, stepLengthChars, "%n");
        } else {
            startProgressTask(step, depthPadding, stepLengthChars);
        }
    }

    private void startProgressTask(StepToken step, StringBuilder depthPadding, int stepLengthChars) {
        if ( PROGRESS_CURSOR_FRAME_RATE > 0) {
            StepProgressRunnable progress = new ShowStepProgressPlainOutputTask(depthPadding, stepLengthChars, step);
            startProgressTask(progress, PROGRESS_CURSOR_FRAME_RATE);
        }
    }

    /**
     * Show step progress but only if a step does not complete after 5 seconds
     * (this helps to make it clearer which step is blocked)
     */
    private class ShowStepProgressPlainOutputTask extends StepProgressRunnable {
        
        public ShowStepProgressPlainOutputTask(StringBuilder depthPadding, int stepLengthChars, StepToken step) {
            super(depthPadding, stepLengthChars, step);
        }

        protected String getTerminator(int frameCount) {
            String terminator = "(running for " + (frameCount * PROGRESS_CURSOR_FRAME_RATE) / 1000 + "s..)%n";
            return terminator;
        }
    }
}
