package org.chorusbdd.chorus.stepinvoker;

import org.chorusbdd.chorus.util.ChorusException;

/**
 * Created by nick on 16/01/15.
 */
public class DuplicateStepMatchException extends ChorusException {

    public DuplicateStepMatchException(String description) {
        super(description);
    }
}
