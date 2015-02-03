package org.chorusbdd.chorus.remoting;

import org.chorusbdd.chorus.executionlistener.ExecutionListener;
import org.chorusbdd.chorus.remoting.jmx.remotingmanager.JmxRemotingManager;
import org.chorusbdd.chorus.remoting.manager.RemotingManager;
import org.chorusbdd.chorus.stepinvoker.StepInvoker;

import java.util.List;
import java.util.Properties;

/**
 * A RemotingManager which is protocol aware and will delegate remoting calls to the appropriate
 * underlying implementation
 *
 * TODO at present this only delegates to the JMX remoting handler, this needs to be changed when new remoting added
 */
public class ProtocolAwareRemotingManager implements RemotingManager {

    private JmxRemotingManager jmxRemotingManager = new JmxRemotingManager();

    /**
     * Find a step method in the remote component which matches the 'action' String
     * <p/>
     * This method should throw a RemoteStepNotFoundException if a matching remote step cannot be found for this component
     * For general connectivity errors or other error conditions a ChorusException should be thrown (with a cause)
     *
     * @param configName
     * @param remotingConfig
     * @param action        - the step text from the scenario which we want to match to a remote step
     * @return the value returned by the remote component when invoking the remote step implementation
     */
    public Object performActionInRemoteComponent(String configName, Properties remotingConfig, String action) {
        return jmxRemotingManager.performActionInRemoteComponent(configName, remotingConfig, action);
    }

    @Override
    public void connect(String configName, Properties remotingConfig) {
        jmxRemotingManager.connect(configName, remotingConfig);
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
