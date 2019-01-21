/**
 * MIT License
 *
 * Copyright (c) 2019 Chorus BDD Organisation.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.chorusbdd.chorus.remoting;

import org.chorusbdd.chorus.executionlistener.ExecutionListener;
import org.chorusbdd.chorus.handlerconfig.configproperty.ConfigBuilderException;
import org.chorusbdd.chorus.handlerconfig.configproperty.ConfigurationProperty;
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
