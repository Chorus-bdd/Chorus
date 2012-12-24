/**
 *  Copyright (C) 2000-2012 The Software Conservancy and Original Authors.
 *  All rights reserved.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to
 *  deal in the Software without restriction, including without limitation the
 *  rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 *  sell copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 *  IN THE SOFTWARE.
 *
 *  Nothing in this notice shall be deemed to grant any rights to trademarks,
 *  copyrights, patents, trade secrets or any other intellectual property of the
 *  licensor or any contributor except as expressly stated herein. No patent
 *  license is granted separate from the Software, for code that you delete from
 *  the Software, or for combinations of the Software with other software or
 *  hardware.
 */
package org.chorusbdd.chorus;

import org.chorusbdd.chorus.executionlistener.ExecutionListener;
import org.chorusbdd.chorus.executionlistener.SystemOutExecutionListener;
import org.chorusbdd.chorus.remoting.jmx.DynamicProxyMBeanCreator;
import org.chorusbdd.chorus.remoting.jmx.RemoteExecutionListenerMBean;
import org.chorusbdd.chorus.util.config.ChorusConfigProperty;
import org.chorusbdd.chorus.util.config.ConfigProperties;
import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 16/05/12
 * Time: 20:33
 *
 * Create the appropriate execution listeners, based on system paramters and switches passed
 * to the interpreter
 */
public class ExecutionListenerFactory {

    private static ChorusLog log = ChorusLogFactory.getLog(Chorus.class);

    public List<ExecutionListener> createExecutionListener(ConfigProperties config) {
        List<ExecutionListener> result = new ArrayList<ExecutionListener>();
        if ( config.isSet(ChorusConfigProperty.JMX_LISTENER)) {
            //we can have zero to many remote jmx execution listeners available
            addProxyForRemoteJmxListener(config.getValues(ChorusConfigProperty.JMX_LISTENER), result);
        }

        addSystemOutExecutionListener(config, result);
        return result;
    }

    private void addSystemOutExecutionListener(ConfigProperties config, List<ExecutionListener> result) {
        boolean verbose = config.isTrue(ChorusConfigProperty.SHOW_ERRORS);
        boolean showSummary = config.isTrue(ChorusConfigProperty.SHOW_SUMMARY);
        result.add(new SystemOutExecutionListener(showSummary, verbose));
    }

    private void addProxyForRemoteJmxListener(List<String> remoteListenerHostAndPorts, List<ExecutionListener> result) {
        for ( String hostAndPort : remoteListenerHostAndPorts ) {
            addRemoteListener(result, hostAndPort);
        }
    }

    private void addRemoteListener(List<ExecutionListener> result, String hostAndPort) {
        try {
            StringTokenizer t = new StringTokenizer(hostAndPort, ":");
            String host = t.nextToken();
            int port = Integer.valueOf(t.nextToken());
            DynamicProxyMBeanCreator h = new DynamicProxyMBeanCreator(host, port);
            h.connect();
            result.add(h.createMBeanProxy(RemoteExecutionListenerMBean.JMX_EXECUTION_LISTENER_NAME, RemoteExecutionListenerMBean.class));
        } catch (Throwable t) {
            log.warn("Failed to create proxy for jmx execution listener at " + hostAndPort);
        }
    }

}
