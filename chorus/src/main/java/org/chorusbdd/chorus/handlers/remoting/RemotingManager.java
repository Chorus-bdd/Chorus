package org.chorusbdd.chorus.handlers.remoting;

import org.chorusbdd.chorus.util.ChorusException;

import java.util.Map;

/**
 * Created by nick on 30/08/2014.
 * 
 * A RemotingManager implements the remoting/network handling for a remoting protocol supported by the Chorus 
 * interpreter
 * 
 * A new instance of the RemotingManager for each supported protocol is created at the start of each scenario 
 * which uses RemotingHandler
 */
public interface RemotingManager {

    /**
     * Find a step method in the remote component which matches the 'action' String
     * 
     * This method should throw a RemoteStepNotFoundException if a matching remote step cannot be found for this component
     * For general connectivity errors or other error conditions a ChorusException should be thrown (with a cause)
     * 
     * @param action            - the step text from the scenario which we want to match to a remote step
     * @param componentName     - the name of the component we want to connect to
     * @return                    the value returned by the remote component when invoking the remote step implementation
     **/
    Object performActionInRemoteComponent(String action, String componentName, Map<String, RemotingConfig> remotingConfigMap);

    void destroy();
}
