package org.chorusbdd.chorus.websockets;

/**
 * Created by nick on 13/12/2016.
 */
public final class StepFailedException extends RuntimeException {

    private String description;
    private String errorText;

    public StepFailedException(String description, String errorText) {
        super(description);
        this.description = description;
        this.errorText = errorText;
    }

    public String getErrorText() {
        return errorText;
    }
}
