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
package org.chorusbdd.chorus.tools.webagent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chorusbdd.chorus.executionlistener.ExecutionListener;
import org.chorusbdd.chorus.executionlistener.ExecutionListenerSupport;
import org.chorusbdd.chorus.executionlistener.RemoteExecutionListener;
import org.chorusbdd.chorus.executionlistener.RemoteExecutionListenerMBean;
import org.chorusbdd.chorus.results.*;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 24/12/12
 * Time: 14:02
 *
 * Create and register a local MBean server to receive test suite events from a remote interpreter
 */
public class WebAgentSuiteListener implements ExecutionListener {

    private static final Log log = LogFactory.getLog(JmxManagementServerExporter.class);

    private static final Executor eventPropagator = Executors.newSingleThreadExecutor();
    private int localPort;
    private final JmxManagementServerExporter jmxManagementServerExporter;
    private ExecutionListenerSupport executionListenerSupport = new ExecutionListenerSupport();

    public WebAgentSuiteListener(int localPort, boolean usePlatformMBeanServer) {
        this.localPort = localPort;
        this.jmxManagementServerExporter = new JmxManagementServerExporter(localPort, usePlatformMBeanServer);
    }

    public void start() throws Exception {
        log.info(this + " starting MBean server");
        jmxManagementServerExporter.startServer();
        RemoteExecutionListener r = new RemoteExecutionListener(this);
        MBeanServer mBeanServer = jmxManagementServerExporter.getmBeanServer();
        try {
            mBeanServer.registerMBean(r, new ObjectName(RemoteExecutionListenerMBean.JMX_EXECUTION_LISTENER_NAME));
        } catch (Exception e) {
            log.error("Failed to register jmx execution listener", e);
        }
    }

    public void stop() throws IOException {
        log.info(this + " stopping MBean server");
        MBeanServer mBeanServer = jmxManagementServerExporter.getmBeanServer();
        try {
            mBeanServer.unregisterMBean(new ObjectName(RemoteExecutionListenerMBean.JMX_EXECUTION_LISTENER_NAME));
        } catch (Exception e) {
            log.error("Failed to unregister jmx execution listener", e);
        }
        jmxManagementServerExporter.stopServer();
    }

    public String toString() {
        return getClass().getSimpleName() + " on localhost " + localPort;
    }

    //delegate to  executionListenerSupport to enable adding / removing listeners

    public void addExecutionListener(ExecutionListener... listeners) {
        executionListenerSupport.addExecutionListeners(listeners);
    }

    public boolean removeExecutionListener(ExecutionListener... listeners) {
        return executionListenerSupport.removeExecutionListeners(listeners);
    }

    public void addExecutionListener(Collection<ExecutionListener> listeners) {
        executionListenerSupport.addExecutionListeners(listeners);
    }

    public void removeExecutionListeners(List<ExecutionListener> listeners) {
        executionListenerSupport.removeExecutionListeners(listeners);
    }

    public List<ExecutionListener> getListeners() {
        return executionListenerSupport.getListeners();
    }

    //////////////// Implement ExecutionListener to forward received events via event propagation thread

    @Override
    public void testsStarted(final ExecutionToken testExecutionToken, final List<FeatureToken> features) {
        eventPropagator.execute(new Runnable() {
            public void run() {
                executionListenerSupport.notifyTestsStarted(testExecutionToken, features);
            }
        });
    }

    @Override
    public void testsCompleted(final ExecutionToken testExecutionToken, final List<FeatureToken> features, final Set<CataloguedStep> cataloguedSteps
    ) {
        eventPropagator.execute(new Runnable() {
            public void run() {
                executionListenerSupport.notifyTestsCompleted(testExecutionToken, features, cataloguedSteps);
            }
        });
    }

    @Override
    public void featureStarted(final ExecutionToken testExecutionToken, final FeatureToken feature) {
        eventPropagator.execute(new Runnable() {
            public void run() {
                executionListenerSupport.notifyFeatureStarted(testExecutionToken, feature);
            }
        });
    }

    @Override
    public void featureCompleted(final ExecutionToken testExecutionToken, final FeatureToken feature) {
        eventPropagator.execute(new Runnable() {
            public void run() {
                executionListenerSupport.notifyFeatureCompleted(testExecutionToken, feature);
            }
        });
    }

    @Override
    public void scenarioStarted(final ExecutionToken testExecutionToken, final ScenarioToken scenario) {
        eventPropagator.execute(new Runnable() {
            public void run() {
                executionListenerSupport.notifyScenarioStarted(testExecutionToken, scenario);
            }
        });
    }

    @Override
    public void scenarioCompleted(final ExecutionToken testExecutionToken, final ScenarioToken scenario) {
        eventPropagator.execute(new Runnable() {
            public void run() {
                executionListenerSupport.notifyScenarioCompleted(testExecutionToken, scenario);
            }
        });
    }

    @Override
    public void stepStarted(final ExecutionToken testExecutionToken, final StepToken step) {
        eventPropagator.execute(new Runnable() {
            public void run() {
                executionListenerSupport.notifyStepStarted(testExecutionToken, step);
            }
        });
    }

    @Override
    public void stepCompleted(final ExecutionToken testExecutionToken, final StepToken step) {
        eventPropagator.execute(new Runnable() {
            public void run() {
                executionListenerSupport.notifyStepCompleted(testExecutionToken, step);
            }
        });
    }
}
