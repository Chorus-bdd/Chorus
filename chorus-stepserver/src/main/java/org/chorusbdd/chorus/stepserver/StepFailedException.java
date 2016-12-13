package org.chorusbdd.chorus.stepserver;

/**
 * Created by nick on 13/12/2016.
 */
public final class StepFailedException extends Exception {

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

    @Override
    public String toString() {
        return "Step failed in remote StepServer client, " + description + " [" + errorText + "]";
    }
}
