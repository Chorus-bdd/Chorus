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

import java.util.List;
import java.util.Map;

/**
 * Created by nick on 09/12/2016.
 */
public class ExecuteStepMessage extends AbstractTypedMessage {

    private String stepId;
    private String executionId;
    private String chorusClientId;
    private String stepTokenId;
    private String pattern;
    private int timeoutPeriodSeconds;
    private List<String> arguments;
    private Map<String, Object> contextVariables;

    /**
     * Nullary constructor required for deserialization
     */
    public ExecuteStepMessage() {
        super(MessageType.EXECUTE_STEP.name());
    }

    public ExecuteStepMessage(String chorusClientId, String stepId, String executionId, String stepTokenId, String pattern, int timeoutPeriodSeconds, List<String> arguments, Map<String, Object> contextVariables) {
        this();
        this.stepId = stepId;
        this.executionId = executionId;
        this.chorusClientId = chorusClientId;
        this.stepTokenId = stepTokenId;
        this.pattern = pattern;
        this.timeoutPeriodSeconds = timeoutPeriodSeconds;
        this.arguments = arguments;
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

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public int getTimeoutPeriodSeconds() {
        return timeoutPeriodSeconds;
    }

    public void setTimeoutPeriodSeconds(int timeoutPeriodSeconds) {
        this.timeoutPeriodSeconds = timeoutPeriodSeconds;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public void setArguments(List<String> arguments) {
        this.arguments = arguments;
    }

    public Map<String, Object> getContextVariables() {
        return contextVariables;
    }

    public void setContextVariables(Map<String, Object> contextVariables) {
        this.contextVariables = contextVariables;
    }

    public String getStepTokenId() {
        return stepTokenId;
    }

    public void setStepTokenId(String stepTokenId) {
        this.stepTokenId = stepTokenId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExecuteStepMessage that = (ExecuteStepMessage) o;

        if (timeoutPeriodSeconds != that.timeoutPeriodSeconds) return false;
        if (!stepId.equals(that.stepId)) return false;
        if (!executionId.equals(that.executionId)) return false;
        if (!chorusClientId.equals(that.chorusClientId)) return false;
        if (!stepTokenId.equals(that.stepTokenId)) return false;
        if (!pattern.equals(that.pattern)) return false;
        if (!arguments.equals(that.arguments)) return false;
        return contextVariables.equals(that.contextVariables);
    }

    @Override
    public int hashCode() {
        int result = stepId.hashCode();
        result = 31 * result + executionId.hashCode();
        result = 31 * result + chorusClientId.hashCode();
        result = 31 * result + stepTokenId.hashCode();
        result = 31 * result + pattern.hashCode();
        result = 31 * result + timeoutPeriodSeconds;
        result = 31 * result + arguments.hashCode();
        result = 31 * result + contextVariables.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ExecuteStepMessage{" +
            "stepId='" + stepId + '\'' +
            ", executionId='" + executionId + '\'' +
            ", stepTokenId='" + stepTokenId + '\'' +
            ", chorusClientId='" + chorusClientId + '\'' +
            ", pattern='" + pattern + '\'' +
            ", timeoutPeriodSeconds=" + timeoutPeriodSeconds +
            ", arguments=" + arguments +
            ", contextVariables=" + contextVariables +
            '}';
    }
}
