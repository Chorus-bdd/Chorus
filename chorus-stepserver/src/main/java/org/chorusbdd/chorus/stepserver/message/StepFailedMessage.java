package org.chorusbdd.chorus.stepserver.message;

/**
 * Created by nick on 09/12/2016.
 */
public class StepFailedMessage extends AbstractTypedMessage {

    private String executionId;
    private String chorusClientId;
    private String description;
    private String errorText = "";

    /**
     * Nullary constructor required for deserialization
     */
    public StepFailedMessage() {
        super("STEP_FAILED");
    }

    public StepFailedMessage(String executionId, String chorusClientId, String description, String errorText) {
        this();
        this.executionId = executionId;
        this.chorusClientId = chorusClientId;
        this.description = description;
        this.errorText = errorText;
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

        if (!executionId.equals(that.executionId)) return false;
        if (!chorusClientId.equals(that.chorusClientId)) return false;
        if (!description.equals(that.description)) return false;
        return errorText.equals(that.errorText);

    }

    @Override
    public int hashCode() {
        int result = executionId.hashCode();
        result = 31 * result + chorusClientId.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + errorText.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "StepFailureMessage{" +
            "type='" + type + '\'' +
            ", executionId='" + executionId + '\'' +
            ", chorusClientId='" + chorusClientId + '\'' +
            ", description='" + description + '\'' +
            ", errorText='" + errorText + '\'' +
            '}';
    }
}
