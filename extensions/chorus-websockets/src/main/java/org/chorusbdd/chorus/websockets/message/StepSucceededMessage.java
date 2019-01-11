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

import java.util.Map;

/**
 * Created by nick on 09/12/2016.
 */
public class StepSucceededMessage extends AbstractTypedMessage {

    private String stepId;
    private String executionId;
    private String chorusClientId;
    private Object result;
    private Map<String, Object> contextVariables;

    /**
     * Nullary constructor required for deserialization
     */
    public StepSucceededMessage() {
        super(MessageType.STEP_SUCCEEDED.name());
    }

    public StepSucceededMessage(String stepId, String executionId, String chorusClientId, Object result, Map<String, Object> contextVariables) {
        this();
        this.stepId = stepId;
        this.executionId = executionId;
        this.chorusClientId = chorusClientId;
        this.result = result;
        this.contextVariables = contextVariables;
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

    public Object getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Map<String, Object> getContextVariables() {
        return contextVariables;
    }

    public void setContextVariables(Map<String, Object> contextVariables) {
        this.contextVariables = contextVariables;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StepSucceededMessage that = (StepSucceededMessage) o;

        if (!stepId.equals(that.stepId)) return false;
        if (!executionId.equals(that.executionId)) return false;
        if (!chorusClientId.equals(that.chorusClientId)) return false;
        if (!result.equals(that.result)) return false;
        return contextVariables.equals(that.contextVariables);

    }

    @Override
    public int hashCode() {
        int result1 = stepId.hashCode();
        result1 = 31 * result1 + executionId.hashCode();
        result1 = 31 * result1 + chorusClientId.hashCode();
        result1 = 31 * result1 + result.hashCode();
        result1 = 31 * result1 + contextVariables.hashCode();
        return result1;
    }

    @Override
    public String toString() {
        return "StepSuccessMessage{" +
            "type='" + type + '\'' +
            ", stepId='" + stepId + '\'' +
            ", executionId='" + executionId + '\'' +
            ", chorusClientId='" + chorusClientId + '\'' +
            ", result='" + result + '\'' +
            ", contextVariables=" + contextVariables +
            '}';
    }

}
