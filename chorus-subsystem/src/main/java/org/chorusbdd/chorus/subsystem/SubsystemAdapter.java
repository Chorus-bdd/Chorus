package org.chorusbdd.chorus.subsystem;

import org.chorusbdd.chorus.executionlistener.ExecutionListener;
import org.chorusbdd.chorus.executionlistener.NullExecutionListener;

/**
 * Created by nick on 15/10/2014.
 */
public class SubsystemAdapter implements Subsystem {

    public ExecutionListener getExecutionListener() {
        return NullExecutionListener.NULL_LISTENER;
    }
}
