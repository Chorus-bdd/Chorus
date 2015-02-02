package org.chorusbdd.chorus.remoting;

import org.chorusbdd.chorus.executionlistener.ExecutionListener;
import org.chorusbdd.chorus.remoting.jmx.remotingmanager.JmxRemotingManager;
import org.chorusbdd.chorus.remoting.manager.RemotingManager;
import org.chorusbdd.chorus.remoting.manager.RemotingManagerConfig;
import org.chorusbdd.chorus.stepinvoker.StepInvoker;
import org.chorusbdd.chorus.util.ChorusException;

import java.util.List;

/**
 * A RemotingManager which is protocol aware and will delegate remoting calls to the appropriate
 * underlying implementation
 */
public class ProtocolAwareRemotingManager implements RemotingManager {

    private JmxRemotingManager jmxRemotingManager = new JmxRemotingManager();

    /**
     * Find a step method in the remote component which matches the 'action' String
     * <p/>
     * This method should throw a RemoteStepNotFoundException if a matching remote step cannot be found for this component
     * For general connectivity errors or other error conditions a ChorusException should be thrown (with a cause)
     *
     * @param action        - the step text from the scenario which we want to match to a remote step
     * @param remotingInfo
     * @return the value returned by the remote component when invoking the remote step implementation
     */
    public Object performActionInRemoteComponent(String action, RemotingManagerConfig remotingInfo) {
        Object result;
        if ( JmxRemotingManager.REMOTING_PROTOCOL.equals(remotingInfo.getProtocol())) {
            result = jmxRemotingManager.performActionInRemoteComponent(action, remotingInfo);
        } else {
            throw new ChorusException("Unsupported Remoting Protocol " + remotingInfo.getProtocol());
        }
        return result;
    }

    @Override
    public List<StepInvoker> getStepInvokers(RemotingManagerConfig remotingConfig) {
        List<StepInvoker> result = null;
        if ( JmxRemotingManager.REMOTING_PROTOCOL.equals(remotingConfig.getProtocol())) {
            result = jmxRemotingManager.getStepInvokers(remotingConfig);
        } else {
            throw new ChorusException("Unsupported Remoting Protocol " + remotingConfig.getProtocol());
        }
        return result;
    }

    public void closeConnections(List<RemotingManagerConfig> connections) {
        jmxRemotingManager.closeConnections(connections);
    }

    public void closeAllConnections() {
        jmxRemotingManager.closeAllConnections();
    }

    public ExecutionListener getExecutionListener() {
        return jmxRemotingManager.getExecutionListener();
    }
}
