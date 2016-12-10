package org.chorusbdd.chorus.stepserver.message;

/**
 * Created by nick on 09/12/2016.
 */
public class PublishStep extends AbstractTypedMessage {

    private String stepId;
    private String chorusClientId;
    private String pattern;
    private boolean isPending;
    private String pendingMessage = "";  //optional so provide default
    private String technicalDescription;

    public PublishStep() {
        super(MessageType.PUBLISH_STEP.name());
    }

    public PublishStep(String stepId, String chorusClientId, String pattern, boolean isPending, String pendingMessage, String technicalDescription) {
        this();
        this.stepId = stepId;
        this.chorusClientId = chorusClientId;
        this.pattern = pattern;
        this.isPending = isPending;
        this.pendingMessage = pendingMessage;
        this.technicalDescription = technicalDescription;
    }

    public String getStepId() {
        return stepId;
    }

    public void setStepId(String stepId) {
        this.stepId = stepId;
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

    public boolean isPending() {
        return isPending;
    }

    public void setPending(boolean pending) {
        isPending = pending;
    }

    public String getPendingMessage() {
        return pendingMessage;
    }

    public void setPendingMessage(String pendingMessage) {
        this.pendingMessage = pendingMessage;
    }

    public String getTechnicalDescription() {
        return technicalDescription;
    }

    public void setTechnicalDescription(String technicalDescription) {
        this.technicalDescription = technicalDescription;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PublishStep that = (PublishStep) o;

        if (isPending != that.isPending) return false;
        if (!stepId.equals(that.stepId)) return false;
        if (!chorusClientId.equals(that.chorusClientId)) return false;
        if (!pattern.equals(that.pattern)) return false;
        if (!pendingMessage.equals(that.pendingMessage)) return false;
        return technicalDescription.equals(that.technicalDescription);

    }

    @Override
    public int hashCode() {
        int result = stepId.hashCode();
        result = 31 * result + chorusClientId.hashCode();
        result = 31 * result + pattern.hashCode();
        result = 31 * result + (isPending ? 1 : 0);
        result = 31 * result + pendingMessage.hashCode();
        result = 31 * result + technicalDescription.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "PublishStep{" +
            "type='" + type + "\'" +
            ", stepId='" + stepId + '\'' +
            ", chorusClientId='" + chorusClientId + '\'' +
            ", pattern='" + pattern + '\'' +
            ", isPending=" + isPending +
            ", pendingMessage='" + pendingMessage + '\'' +
            ", technicalDescription='" + technicalDescription + '\'' +
            '}';
    }

}
