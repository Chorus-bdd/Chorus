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

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Nick E
 * 
 * The self tests were written with an earlier PlainOutputFormatter, without the use of carriage return in printStepStart
 * 
 * When running in self test mode we pass the output into a buffer which does not interpret the carriage return character and does 
 * not set to the start of the line. So with the PlainOutputFormatter we end up duplicating the step output
 * 
 * The solution is to use a modified OutputFormatter for the tests, which has the same behaviour as the original
 */
public class SelfTestOutputFormatter extends PlainOutputFormatter {

    public void printStepStart(StepToken step, int depth) {
        if ( step.isStepMacro() ) {
            StringBuilder depthPadding = getDepthPadding(depth);
            int maxStepTextChars = Math.max(89, 50);  //always show at least 50 chars of step text
            out.printf("    " + depthPadding + "%-" + maxStepTextChars + "s%-7s %s%n", step.toString(), ">>", step.getMessage());
            out.flush();
        }
    }


    public void printStepEnd(StepToken step, int depth) {
        if ( ! step.isStepMacro() ) {
            StringBuilder depthPadding = getDepthPadding(depth);
            int maxStepTextChars = Math.max(89, 50);  //always show at least 50 chars of step text
            out.printf("    " + depthPadding + "%-" + maxStepTextChars + "s%-7s %s%n", step.toString(), step.getEndState(), step.getMessage());
            out.flush();
        }
    }  
}
