/**
 *  Copyright (C) 2000-2012 The Software Conservancy as Trustee.
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
package org.chorusbdd.chorus.core.interpreter.results;

/**
 * Created by: Steve Neal
 * Date: 30/09/11
 */
public class StepToken implements ResultToken {

    private static final long serialVersionUID = 1;

    private final String type;
    private final String action;

    private StepEndState endState;
    private String message = "";
    private Throwable throwable;

    public StepToken(String type, String action) {
        this.type = type;
        this.action = action;
    }

    public StepToken deepCopy() {
        StepToken copy = new StepToken(this.type, this.action);
        copy.endState = this.endState;
        copy.message = this.message;
        copy.throwable = this.throwable;
        return copy;
    }

    public String getType() {
        return type;
    }

    public String getAction() {
        return action;
    }

    public StepEndState getEndState() {
        return endState;
    }

    public void setEndState(StepEndState endState) {
        this.endState = endState;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    /**
     * Useful where we show step results as one of three categories, passed, failed, and anything else in between
     * this means that 'dry run' also counts as passed
     */
    public boolean isUndefinedPendingOrSkipped() {
        return endState == StepEndState.UNDEFINED || endState == StepEndState.PENDING || endState == StepEndState.SKIPPED;
    }

    public boolean isFullyImplemented() {
        return endState != StepEndState.UNDEFINED && endState != StepEndState.PENDING;
    }

    public boolean isPassed() {
        //this means 'dry run' also counts as passed
        return endState != StepEndState.FAILED;
    }

    @Override
    public String toString() {
        return String.format("%s %s", type, action);
    }

}
