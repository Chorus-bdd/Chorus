package org.chorusbdd.chorus.processes.manager.config;

/**
 * Created by nick on 29/10/14.
 */
public enum StartMode {

    /**
     * Automatically started at the start of the scenario (SCENARIO scoped process) or FEATURE (if feature scoped process)
     */
    AUTOMATIC,

    /**
     * Started during a scenario with the Processes handler step 'I start a myProcessName process'
     */
    STEP
}
