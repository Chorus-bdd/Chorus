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

import org.chorusbdd.chorus.results.StepToken;

/**
 * Nick E
 */
public final class ConsoleOutputWriter extends AbstractChorusOutputWriter {

    private static final int PROGRESS_CURSOR_FRAME_RATE = 400;
    
    public void printStepStart(StepToken step, int depth) {
        StringBuilder depthPadding = getDepthPadding(depth);
        int stepLengthChars = getStepLengthCharCount() - depthPadding.length();
        String terminator = step.isStepMacro() ? "%n" : "|\r";
        printStepWithoutEndState(step, depthPadding, stepLengthChars, terminator);

        if ( ! step.isStepMacro()) {
            StepProgressRunnable progress = new ShowStepProgressConsoleTask(depthPadding, stepLengthChars, step);
            startProgressTask(progress, PROGRESS_CURSOR_FRAME_RATE);
        }
    }

    public void printStepEnd(StepToken step, int depth) {
        cancelStepAnimation();
        if ( ! step.isStepMacro()) { //we don't print results for the step macro step itself but show it for each child step
            StringBuilder depthPadding = getDepthPadding(depth);
            int stepLengthChars =  getStepLengthCharCount() - depthPadding.length();
            getOutWriter().printf("    " + depthPadding + "%-" + stepLengthChars + "s%-7s %s%n", step.toString(), step.getEndState(), step.getMessage());
            getOutWriter().flush();
        }
    }

    /**
     * Show step progress with a carriage return to overwrite the previously written line and an animated cursor
     */
    private class ShowStepProgressConsoleTask extends StepProgressRunnable {
        
        public ShowStepProgressConsoleTask(StringBuilder depthPadding, int stepLengthChars, StepToken step) {
            super(depthPadding, stepLengthChars, step);
        }

        protected String getTerminator(int frameCount) {
            String terminator;
            int i = frameCount % 3;
            switch(i) {
                case 0 :
                default: terminator = "\\\r";
                    break;
                case 1 : terminator =  "/\r";
                    break;
                case 2 : terminator = "-\r";
                    break;
            }
            return terminator;
        }
    }
}
