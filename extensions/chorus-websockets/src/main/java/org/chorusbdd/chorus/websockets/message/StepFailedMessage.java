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
package org.chorusbdd.chorus.websockets.message;

/**
 * Created by nick on 09/12/2016.
 */
public class StepFailedMessage extends AbstractTypedMessage {

    private String stepId;
    private String executionId;
    private String chorusClientId;
    private String description;
    private String errorText = "";  //optional so provide default

    /**
     * Nullary constructor required for deserialization
     */
    public StepFailedMessage() {
        super(MessageType.STEP_FAILED.name());
    }

    public StepFailedMessage(String stepId, String executionId, String chorusClientId, String description, String errorText) {
        this();
        this.stepId = stepId;
        this.executionId = executionId;
        this.chorusClientId = chorusClientId;
        this.description = description;
        this.errorText = errorText;
    }

    public String getStepId() {
        return stepId;
    }

    public void setStepId(String stepId) {
        this.stepId = stepId;
    }

    public String getExecutionId() {
        return executionId;
    }

    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }

    public String getChorusClientId() {
        return chorusClientId;
    }

    public void setChorusClientId(String chorusClientId) {
        this.chorusClientId = chorusClientId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getErrorText() {
        return errorText;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StepFailedMessage that = (StepFailedMessage) o;

        if (!stepId.equals(that.stepId)) return false;
        if (!executionId.equals(that.executionId)) return false;
        if (!chorusClientId.equals(that.chorusClientId)) return false;
        if (!description.equals(that.description)) return false;
        return errorText.equals(that.errorText);

    }

    @Override
    public int hashCode() {
        int result = stepId.hashCode();
        result = 31 * result + executionId.hashCode();
        result = 31 * result + chorusClientId.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + errorText.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "StepFailureMessage{" +
            "type='" + type + '\'' +
            ", stepId='" + stepId + '\'' +
            ", executionId='" + executionId + '\'' +
            ", chorusClientId='" + chorusClientId + '\'' +
            ", description='" + description + '\'' +
            ", errorText='" + errorText + '\'' +
            '}';
    }
}
