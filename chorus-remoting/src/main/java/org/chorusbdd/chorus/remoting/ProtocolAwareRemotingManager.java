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
     * @param remotingConfig
     * @return the value returned by the remote component when invoking the remote step implementation
     */
    public Object performActionInRemoteComponent(String action, RemotingManagerConfig remotingConfig) {
        Object result;
        if ( JmxRemotingManager.REMOTING_PROTOCOL.equals(remotingConfig.getProtocol())) {
            result = jmxRemotingManager.performActionInRemoteComponent(action, remotingConfig);
        } else {
            throw new ChorusException("Unsupported Remoting Protocol " + remotingConfig.getProtocol());
        }
        return result;
    }

    @Override
    public void connect(RemotingManagerConfig remotingConfig) {
        if ( JmxRemotingManager.REMOTING_PROTOCOL.equals(remotingConfig.getProtocol())) {
            jmxRemotingManager.connect(remotingConfig);
        } else {
            throw new ChorusException("Unsupported Remoting Protocol " + remotingConfig.getProtocol());
        }
    }

    @Override
    public List<StepInvoker> getStepInvokers() {
        return jmxRemotingManager.getStepInvokers();
    }

    public void closeAllConnections() {
        jmxRemotingManager.closeAllConnections();
    }

    public ExecutionListener getExecutionListener() {
        return jmxRemotingManager.getExecutionListener();
    }
}
