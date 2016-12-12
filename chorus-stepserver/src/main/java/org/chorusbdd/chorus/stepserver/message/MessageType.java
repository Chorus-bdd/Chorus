package org.chorusbdd.chorus.stepserver.message;

/**
 * Created by nick on 10/12/2016.
 */
public enum MessageType {

    CONNECT,
    PUBLISH_STEP,
    STEP_FAILED,
    STEP_SUCCEEDED,
    STEPS_ALIGNED,
    EXECUTE_STEP,
    UNKNOWN;


    public static MessageType fromString(String type) {
        MessageType result;
        try {
            result = valueOf(type);
        } catch ( IllegalArgumentException e ) {
            result = MessageType.UNKNOWN;
        }
        return result;
    }
}
