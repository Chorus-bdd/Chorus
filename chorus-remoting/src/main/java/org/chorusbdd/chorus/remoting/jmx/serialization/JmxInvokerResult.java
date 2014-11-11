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

    public static final String SERIALIZE_VERSION = "SERIALIZE_VERSION";
    public static final String STEP_ID = "STEP_ID";
    public static final String PENDING_MSG = "PENDING_MSG";
    public static final String PATTERN = "PATTERN";
    public static final String TECHNICAL_DESCRIPTION = "TECHNICAL_DESCRIPTION";
    public static final String ID = "ID";

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

}
