package org.chorusbdd.chorus.stepserver.message;

import java.util.Map;

/**
 * Created by nick on 09/12/2016.
 */
public class StepSucceededMessage extends AbstractTypedMessage {

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

    public StepSucceededMessage(String executionId, String chorusClientId, Object result, Map<String, Object> contextVariables) {
        this();
        this.executionId = executionId;
        this.chorusClientId = chorusClientId;
        this.result = result;
        this.contextVariables = contextVariables;
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
    public String toString() {
        return "StepSuccessMessage{" +
            "type='" + type + '\'' +
            ", executionId='" + executionId + '\'' +
            ", chorusClientId='" + chorusClientId + '\'' +
            ", result='" + result + '\'' +
            ", contextVariables=" + contextVariables +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StepSucceededMessage that = (StepSucceededMessage) o;

        if (!executionId.equals(that.executionId)) return false;
        if (!chorusClientId.equals(that.chorusClientId)) return false;
        if (!result.equals(that.result)) return false;
        return contextVariables.equals(that.contextVariables);

    }

    @Override
    public int hashCode() {
        int result1 = executionId.hashCode();
        result1 = 31 * result1 + chorusClientId.hashCode();
        result1 = 31 * result1 + result.hashCode();
        result1 = 31 * result1 + contextVariables.hashCode();
        return result1;
    }


}
