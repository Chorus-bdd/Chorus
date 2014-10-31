package org.chorusbdd.chorus.remoting.jmx.serialization;

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.remoting.jmx.remotingmanager.ChorusHandlerJmxProxy;
import org.chorusbdd.chorus.remoting.jmx.remotingmanager.RemoteStepInvoker;
import org.chorusbdd.chorus.stepinvoker.StepInvoker;

/**
 * Created by nick on 25/10/14.
 *
 * Convert an Invoker to a Map and back again
 *
 * This avoids serializing the invoker instance across the wire, making it easier to maintain backwards compatibility
 * when attributes are added or removed from StepInvoker
 */
public class JmxInvokerResult extends AbstractJmxResult {

    private static ChorusLog log = ChorusLogFactory.getLog(JmxInvokerResult.class);

    private static final long serialVersionUID = 1;

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
    public JmxInvokerResult(StepInvoker i) {
        put(SERIALIZE_VERSION, CURRENT_SERIALIZE_VERSION);
        put(STEP_ID, i.getId());
        put(PENDING_MSG, i.getPendingMessage());
        put(PATTERN, i.getStepPattern().toString());
        put(TECHNICAL_DESCRIPTION, i.getTechnicalDescription());
        put(ID, i.getId());
    }

    /**
     * @return a StepInvoker which will invoke the remote method
     */
    public RemoteStepInvoker toRemoteStepInvoker(ChorusHandlerJmxProxy jmxProxy) {
        String remoteStepId = (String) get(STEP_ID);
        String regex = (String) get(PATTERN);
        String pending = (String) get(PENDING_MSG);
        String technicalDescription = (String)get(TECHNICAL_DESCRIPTION);

        //at present we just use the remoteStepInvoker to allow the extractGroups to work but should refactor
        //to actually invoke the remote method with it
        RemoteStepInvoker stepInvoker = new RemoteStepInvoker(regex, jmxProxy, remoteStepId, pending, technicalDescription);
        return stepInvoker;
    }
}
