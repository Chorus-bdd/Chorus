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

import org.chorusbdd.chorus.results.StepEndState;
import org.chorusbdd.chorus.results.StepToken;
import org.chorusbdd.chorus.util.ChorusConstants;

/**
 * Nick E
 */
public final class ConsoleOutputWriter extends AbstractChorusOutputWriter {

    //must be a factor of 10,000 since we do special 'running for' logging every 10s
    private static final int PROGRESS_CURSOR_FRAME_RATE = 400;
    
    private static final boolean disableColours = Boolean.getBoolean(ChorusConstants.CONSOLE_COLOUR_OUTPUT_DISABLED_PROPERTY);

    private static final ConsoleColours consoleColours = new ConsoleColours();
    
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

    /**
     * Show step progress with a carriage return to overwrite the previously written line and an animated cursor
     */
    private class ShowStepProgressConsoleTask extends StepProgressRunnable {
        
        public ShowStepProgressConsoleTask(StringBuilder depthPadding, int stepLengthChars, StepToken step) {
            super(depthPadding, stepLengthChars, step);
        }

        protected String getTerminator(int frameCount) {
            String terminator;

            int iterationsPerTenSeconds = 10000 / PROGRESS_CURSOR_FRAME_RATE;
            if ( frameCount % iterationsPerTenSeconds == 0) {
                terminator = "(running for " + (frameCount * PROGRESS_CURSOR_FRAME_RATE) / 1000 + "s..)%n";
            } else {
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
            }
            return terminator;
        }
    }

    /**
     * @return End state as a String containing terminal control codes to colourize output, unless disableColours sys prop is set true
     */
    protected String getEndState(StepToken step) {
        StepEndState endState = step.getEndState();
        return disableColours ? 
                endState.toString() : 
                colourizeEndState(endState);
    }

    private String colourizeEndState(StepEndState endState) {
        String result;
        switch(endState) {
            case PASSED:
                result = consoleColours.highlightGreen(endState.toString());
                break;
            case FAILED:
                result = consoleColours.highlightRed(endState.toString());
                break;
            case PENDING:
                result = consoleColours.highlightYellow(endState.toString());
                break;
            case SKIPPED:
                result = consoleColours.highlightYellow(endState.toString());
                break;
            case UNDEFINED:
                result = consoleColours.highlightRed(endState.toString());
                break;
            case DRYRUN:
                result = consoleColours.highlightGreen(endState.toString());
                break;
            case TIMEOUT:
                result = consoleColours.highlightRed(endState.toString());
                break;
            default :
                result = endState.toString();
        }
        return result;
    }
}



        
