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
package org.chorusbdd.chorus.results;

import org.chorusbdd.chorus.util.ExceptionHandling;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by: Steve Neal
 * Date: 30/09/11
 */
public class StepToken extends AbstractToken {

    private static final long serialVersionUID = 3;

    private final String type;
    private String action;

    private StepEndState endState = StepEndState.NOT_RUN;
    private String message = "";

    //not serializing throwable, there's a chance remote processes will not be able deserialize
    //and non primative fields also make it difficult to reload steps from XML
    private transient Throwable throwable;

    private String stackTrace;  //exception stack trace
    private String exception;  //toString() on throwable

    /**
     * Step macro are composite steps which contain child steps
     */
    private List<StepToken> childSteps = new ArrayList<StepToken>();

    private long timeTaken = 0;  //time taken to run the step

    public StepToken(String type, String action) {
        this(getNextId(), type, action);
    }

    private StepToken(long id, String type, String action) {
        super(id);
        this.type = type;
        this.action = action;
    }

    public String getType() {
        return type;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
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

    /**
     * This is a String representation of any value returned by the step method
     */
    public void setMessage(String message) {
        this.message = message;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
        this.exception = throwable.toString();
        this.stackTrace = ExceptionHandling.getStackTraceAsString(throwable);
    }

    public boolean inOneOf(StepEndState... states) {
        boolean result = false;
        for ( StepEndState s : states) {
            if ( s == getEndState()) {
                result = true;
                break;

            }
        }
        return result;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    /**
     * @return time taken to run the step in milliseconds
     */
    public long getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(long timeTaken) {
        this.timeTaken = timeTaken;
    }

    public void addChildStep(StepToken childToken) {
        childSteps.add(childToken);
    }

    public List<StepToken> getChildSteps() {
        return childSteps;
    }

    /**
     * If this is a leaf step, total count will be 1
     * If this step is a step macro, total count will equal the number of (direct or indirect) leaf step descendants
     *
     * @return the total number of steps represented by this StepToken
     */
    public int getTotalStepCountWithDescendants() {
        int descendantCount = isStepMacro() ? 0 : 1;
        for ( StepToken c : childSteps) {
            descendantCount += c.getTotalStepCountWithDescendants();
        }
        return descendantCount;
    }

    public void accept(TokenVisitor tokenVisitor) {
        tokenVisitor.visit(this);
    }

    public boolean isStepMacro() {
        return childSteps.size() > 0;
    }

    public StepToken deepCopy() {
        StepToken copy = new StepToken(getNextId(), this.type, this.action);
        copy.endState = this.endState;
        copy.message = this.message;
        copy.throwable = this.throwable;
        copy.exception = this.exception;
        copy.stackTrace = this.stackTrace;
        copy.timeTaken = this.timeTaken;
        copy.childSteps = recursiveCopy(childSteps);
        return copy;
    }

    private List<StepToken> recursiveCopy(List<StepToken> childSteps) {
        List<StepToken> l = new ArrayList<StepToken>();
        for ( StepToken t : childSteps) {
            l.add(t.deepCopy());
        }
        return l;
    }

    @Override
    public String toString() {
        return String.format("%s %s", type, action);
    }

}
