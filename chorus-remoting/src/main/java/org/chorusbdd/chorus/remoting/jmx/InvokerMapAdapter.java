package org.chorusbdd.chorus.remoting.jmx;

import org.chorusbdd.chorus.remoting.jmx.remotingmanager.ChorusHandlerJmxProxy;
import org.chorusbdd.chorus.remoting.jmx.remotingmanager.RemoteStepInvoker;
import org.chorusbdd.chorus.remoting.jmx.util.MethodUID;
import org.chorusbdd.chorus.stepinvoker.StepInvoker;

import java.util.HashMap;
import java.util.List;
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

    private static final String SERIALIZE_VERSION = "SERIALIZE_VERSION";
    private static final String STEP_METHOD_UID = "STEP_METHOD_UID";
    private static final String PARAMETER_TYPES = "PARAMETER_TYPES";
    private static final String PENDING_MSG = "PENDING_MSG";
    private static final String PATTERN = "PATTERN";
    private static final String TECHNICAL_DESCRIPTION = "TECHNICAL_DESCRIPTION";
    private static final String ID = "ID";

    //the current version for use if we need to version/change the serialization propertiesv
    private static final int CURRENT_SERIALIZE_VERSION = 1;

    /**
     * @return a map of properties representing a step invoker exported over the network using RMI protocol
     */
    public Map toMap(String stepMethodUid, StepInvoker i) {
        HashMap map = new HashMap();
        map.put(SERIALIZE_VERSION, CURRENT_SERIALIZE_VERSION);
        map.put(STEP_METHOD_UID, stepMethodUid);
        map.put(PARAMETER_TYPES, i.getParameterTypes());
        map.put(PENDING_MSG, i.getPendingMessage());
        map.put(PATTERN, i.getStepPattern().toString());
        map.put(TECHNICAL_DESCRIPTION, i.getTechnicalDescription());
        map.put(ID, i.getId());
        return map;
    }

    /**
     * @return a StepInvoker which will invoke the remote method
     */
    public StepInvoker toRemoteStepInvoker(ChorusHandlerJmxProxy jmxProxy, Map invoker) {
        String methodUid = (String) invoker.get(STEP_METHOD_UID);
        String regex = (String) invoker.get(PATTERN);
        String pending = (String) invoker.get(PENDING_MSG);
        Class[] types = (Class[])invoker.get(PARAMETER_TYPES);

        //at present we just use the remoteStepInvoker to allow the extractGroups to work but should refactor
        //to actually invoke the remote method with it
        StepInvoker stepInvoker = new RemoteStepInvoker(regex, types, jmxProxy, methodUid, pending);
        return stepInvoker;
    }
}
