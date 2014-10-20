package org.chorusbdd.chorus.processes.manager;

import org.chorusbdd.chorus.util.ChorusException;

import java.util.concurrent.TimeUnit;

/**
 * Created by nick on 20/10/2014.
 *
 * Throw an error if the user attempts to pattern match when the process output mode does not support it
 */
public class WarnOnMatchPatternMatcher implements ProcessOutputPatternMatcher {

    private String streamDescription;

    public WarnOnMatchPatternMatcher(String streamDescription) {
        this.streamDescription = streamDescription;
    }

    public void waitForMatch(String pattern, boolean searchWithinLines, TimeUnit timeUnit, long length) {
        throw new ChorusException("Process " + streamDescription + " mode cannot be INLINE when pattern matching");
    }

    public void close() {
    }
}
