package org.chorusbdd.chorus.websockets.message;

/**
 * Created by nick on 09/12/2016.
 */
public class ConnectMessage extends AbstractTypedMessage {

    private String chorusClientId;
    private String description;

    /**
     * Nullary constructor required for deserialization
     */
    public ConnectMessage() {
        super(MessageType.CONNECT.name());
    }

    public ConnectMessage(String chorusClientId, String description) {
        this();
        this.chorusClientId = chorusClientId;
        this.description = description;
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

    @Override
    public String toString() {
        return "Connect{" +
            "type='" + type + '\'' +
            ", chorusClientId='" + chorusClientId + '\'' +
            ", description='" + description + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConnectMessage that = (ConnectMessage) o;

        if (!chorusClientId.equals(that.chorusClientId)) return false;
        return description.equals(that.description);

    }

    @Override
    public int hashCode() {
        int result = chorusClientId.hashCode();
        result = 31 * result + description.hashCode();
        return result;
    }
}
