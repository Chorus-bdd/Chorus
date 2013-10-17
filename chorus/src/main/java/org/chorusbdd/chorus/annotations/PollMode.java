package org.chorusbdd.chorus.annotations;

/**
 * User: nick
 * Date: 17/10/13
 * Time: 08:44
 */
public enum PollMode {
    /**
     * Poll the method until it passes (does not throw an error or exception)
     * When the first pass occurs stop polling and allow scenario to continue
     * If the method does not pass within the allotted period fail the step
     */
    UNTIL_FIRST_PASS,

    /**
     * Poll the method continuously expecting it to pass every time
     * If at any time an exception or error is thrown, fail the step
     */
    PASS_THROUGHOUT_PERIOD
}
