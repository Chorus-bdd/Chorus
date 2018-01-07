package org.chorusbdd.chorus.stepregistry.message;

/**
 * Created by nick on 09/12/2016.
 */
public class AbstractTypedMessage {

    protected String type;

    public AbstractTypedMessage(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
