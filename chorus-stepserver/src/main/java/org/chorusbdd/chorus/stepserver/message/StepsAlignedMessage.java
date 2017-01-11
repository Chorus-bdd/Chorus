package org.chorusbdd.chorus.stepserver.message;

/**
 * Created by nick on 09/12/2016.
 */
public class StepsAlignedMessage extends AbstractTypedMessage {

    private String chorusClientId;

    /**
     * Nullary constructor required for deserialization
     */
    public StepsAlignedMessage() {
        super(MessageType.STEPS_ALIGNED.name());
    }

    public StepsAlignedMessage(String chorusClientId) {
        this();
        this.chorusClientId = chorusClientId;
    }

    public String getChorusClientId() {
        return chorusClientId;
    }

    public void setChorusClientId(String chorusClientId) {
        this.chorusClientId = chorusClientId;
    }


    @Override
    public String toString() {
        return "StepsAligned{" +
            "type='" + type + '\'' +
            ", chorusClientId='" + chorusClientId + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StepsAlignedMessage that = (StepsAlignedMessage) o;

        return chorusClientId.equals(that.chorusClientId);

    }

    @Override
    public int hashCode() {
        return chorusClientId.hashCode();
    }
}
