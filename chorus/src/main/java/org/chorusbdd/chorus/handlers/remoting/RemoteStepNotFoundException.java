package org.chorusbdd.chorus.handlers.remoting;

import org.chorusbdd.chorus.util.ChorusException;

/**
 * Created by nick on 30/08/2014.
 */
public class RemoteStepNotFoundException extends ChorusException {
    
    public RemoteStepNotFoundException(String action, String componentName) {
        super("Could not find a step '" + action + "' in component " + componentName);
    }
}
