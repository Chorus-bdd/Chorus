package org.chorusbdd.chorus.remoting.jmx;

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.remoting.jmx.remotingmanager.ChorusHandlerJmxProxy;
import org.chorusbdd.chorus.remoting.jmx.remotingmanager.RemoteStepInvoker;
import org.chorusbdd.chorus.stepinvoker.StepInvoker;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nick on 25/10/14.
 *
 * Convert an Invoker to a Map and back again
 *
 * This avoids serializing the invoker instance across the wire, making it easier to maintain backwards compatibility
 * when attributes are added or removed from StepInvoker
 */
public class InvokerMapAdapter {

    private static ChorusLog log = ChorusLogFactory.getLog(InvokerMapAdapter.class);
    
    private static final String SERIALIZE_VERSION = "SERIALIZE_VERSION";
    private static final String STEP_ID = "STEP_ID";
    private static final String PENDING_MSG = "PENDING_MSG";
    private static final String PATTERN = "PATTERN";
    private static final String TECHNICAL_DESCRIPTION = "TECHNICAL_DESCRIPTION";
    private static final String ID = "ID";

    //the current version of this serialization
    //for use if we need to change the serialization properties and support backwards compatibility
    private static final int CURRENT_SERIALIZE_VERSION = 1;

    /**
     * @return a map of properties representing a step invoker exported over the network using RMI protocol,
     * or null if the step invoker cannot be converted for remoting
     */
    public Map toMap(StepInvoker i) {
        Map result = new HashMap();
        result.put(SERIALIZE_VERSION, CURRENT_SERIALIZE_VERSION);
        result.put(STEP_ID, i.getId());
        result.put(PENDING_MSG, i.getPendingMessage());
        result.put(PATTERN, i.getStepPattern().toString());
        result.put(TECHNICAL_DESCRIPTION, i.getTechnicalDescription());
        result.put(ID, i.getId());
        return result;
    }

    /**
     * @return a StepInvoker which will invoke the remote method
     */
    public StepInvoker toRemoteStepInvoker(ChorusHandlerJmxProxy jmxProxy, Map invoker) {
        String remoteStepId = (String) invoker.get(STEP_ID);
        String regex = (String) invoker.get(PATTERN);
        String pending = (String) invoker.get(PENDING_MSG);
        String technicalDescription = (String)invoker.get(TECHNICAL_DESCRIPTION);

        //at present we just use the remoteStepInvoker to allow the extractGroups to work but should refactor
        //to actually invoke the remote method with it
        StepInvoker stepInvoker = new RemoteStepInvoker(regex, jmxProxy, remoteStepId, pending, technicalDescription);
        return stepInvoker;
    }
}
