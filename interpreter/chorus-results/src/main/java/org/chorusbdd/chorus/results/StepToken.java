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
package org.chorusbdd.chorus.results;

import java.util.*;

/**
 * Represents a Scenario step
 */
public class StepToken extends AbstractToken {

    private static final long serialVersionUID = 5;

    public static final String DIRECTIVE_TYPE = "#!";

    private final String type;
    private String action;

    private StepEndState endState = StepEndState.NOT_RUN;
    private String message = "";
    private String errorDetails = "";

    private int retryAttempts;

    /**
     * This field is included for future proofing
     * It may be possible to attach resources to steps (e.g. screen shots following a failure)
     */
    private Map attachments;

    /**
     * Step macro are composite steps which contain child steps
     */
    private List<StepToken> childSteps = new ArrayList<>();

    private long timeTaken = 0;  //time taken to run the step

    /**
     * Use the static factory methods to create an instance of a StepToken
     */
    private StepToken(String type, String action) {
        Objects.requireNonNull(action, "action cannot be null");
        Objects.requireNonNull(type, "type cannot be null");
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
        Objects.requireNonNull(action, "action cannot be null");
        this.action = action;
    }

    public StepEndState getEndState() {
        return endState;
    }

    public void setEndState(StepEndState endState) {
        this.endState = endState;
    }

    /**
     * @return The message associated with the step post step-execution, or an empty string if no message exists
     */
    public String getMessage() {
        return message;
    }

    /**
     * Set the message which describes the result of executing the step
     * 
     * If the step was successful this will be either an empty String, or the String representation of a result value returned from the step implementation
     * If the step failed, this will be the 'error cause' 
     */
    public void setMessage(String message) {
        Objects.requireNonNull(message, "message cannot be null");
        this.message = message;
    }

    /**
     * Provides technical details which supplement the message if a step has failed
     * 
     * @return error details description or an empty string if there are no error details available
     */
    public String getErrorDetails() {
        return errorDetails;
    }

    public void setErrorDetails(String errorDetails) {
        Objects.requireNonNull(errorDetails, "error details cannot be null");
        this.errorDetails = errorDetails;
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

    /**
     * @return time taken to run the step in milliseconds
     */
    public long getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(long timeTaken) {
        this.timeTaken = timeTaken;
    }

    /**
     * Some steps which check conditions may be configured to be retried if they fail on first attempt
     * This can be used to avoid failures occurring due to latency / asynchronous behaviour in the system under test
     *
     * @return the number of times a step was re-run before the step was failed
     */
    public int getRetryAttempts() {
        return retryAttempts;
    }

    public void setRetryAttempts(int retryAttempts) {
        this.retryAttempts = retryAttempts;
    }

    public void addChildStep(StepToken childToken) {
        if ( isDirective()) {
            //directives may not be step macro / may not have child steps
            throw new UnsupportedOperationException("Directives may not have child steps");
        }
        childSteps.add(childToken);
    }

    public List<StepToken> getChildSteps() {
        return childSteps;
    }

    public boolean isDirective() {
        return DIRECTIVE_TYPE.equals(type);
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
        tokenVisitor.startVisit(this);
        if ( isStepMacro()) {
            childSteps.forEach(c -> c.accept(tokenVisitor));
        }
        tokenVisitor.endVisit(this);
    }

    public boolean isStepMacro() {
        return !childSteps.isEmpty();
    }

    public static StepToken createDirective(String action) {
        return new StepToken(DIRECTIVE_TYPE, action);
    }

    public static StepToken createStep(String type, String action) {
        return new StepToken(type, action);
    }

    public StepToken deepCopy() {
        StepToken copy = new StepToken(this.type, this.action);
        super.deepCopy(copy);
        copy.endState = this.endState;
        copy.message = this.message;
        copy.throwable = this.throwable;
        copy.timeTaken = this.timeTaken;
        copy.childSteps = recursiveCopy(childSteps);
        return copy;
    }

    private List<StepToken> recursiveCopy(List<StepToken> childSteps) {
        List<StepToken> l = new ArrayList<>();
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
