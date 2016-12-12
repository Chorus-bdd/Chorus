package org.chorusbdd.chorus.stepserver.message;

import java.util.List;
import java.util.Map;

/**
 * Created by nick on 09/12/2016.
 */
public class ExecuteStepMessage extends AbstractTypedMessage {

    private String stepId;
    private String executionId;
    private String chorusClientId;
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

    public ExecuteStepMessage(String chorusClientId, String stepId, String executionId, String pattern, int timeoutPeriodSeconds, List<String> arguments, Map<String, Object> contextVariables) {
        this();
        this.stepId = stepId;
        this.executionId = executionId;
        this.chorusClientId = chorusClientId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExecuteStepMessage that = (ExecuteStepMessage) o;

        if (timeoutPeriodSeconds != that.timeoutPeriodSeconds) return false;
        if (!stepId.equals(that.stepId)) return false;
        if (!executionId.equals(that.executionId)) return false;
        if (!chorusClientId.equals(that.chorusClientId)) return false;
        if (!pattern.equals(that.pattern)) return false;
        if (!arguments.equals(that.arguments)) return false;
        return contextVariables.equals(that.contextVariables);

    }

    @Override
    public int hashCode() {
        int result = stepId.hashCode();
        result = 31 * result + executionId.hashCode();
        result = 31 * result + chorusClientId.hashCode();
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
            ", chorusClientId='" + chorusClientId + '\'' +
            ", pattern='" + pattern + '\'' +
            ", timeoutPeriodSeconds=" + timeoutPeriodSeconds +
            ", arguments=" + arguments +
            ", contextVariables=" + contextVariables +
            '}';
    }
}
