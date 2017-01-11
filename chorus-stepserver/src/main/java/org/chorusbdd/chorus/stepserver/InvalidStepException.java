package org.chorusbdd.chorus.stepserver;

/**
 * Created by nick on 13/12/2016.
 */
public final class InvalidStepException extends Exception {

    public InvalidStepException(String description, Exception e) {
        super(description, e);
    }
}
