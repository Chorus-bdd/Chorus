package org.chorusbdd.chorus;

import org.chorusbdd.chorus.core.interpreter.ChorusExecutionListener;
import org.chorusbdd.chorus.executionlistener.SystemOutExecutionListener;
import org.chorusbdd.chorus.remoting.jmx.DynamicProxyMBeanCreator;
import org.chorusbdd.chorus.remoting.jmx.RemoteExecutionListenerMBean;
import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    private static ChorusLog log = ChorusLogFactory.getLog(Main.class);

    public List<ChorusExecutionListener> createExecutionListener(Map<String, List<String>> parsedArgs) {
        List<ChorusExecutionListener> result = new ArrayList<ChorusExecutionListener>();
        if ( parsedArgs.containsKey("remoteJmxListener")) {
            //we can have zero to many remote jmx execution listeners available
            addProxyForRemoteJmxListener(parsedArgs, result);
        }

        addSystemOutExecutionListener(parsedArgs, result);
        return result;
    }

    private void addSystemOutExecutionListener(Map<String, List<String>> parsedArgs, List<ChorusExecutionListener> result) {
        boolean trace = parsedArgs.containsKey("trace");
        boolean verbose = parsedArgs.containsKey("verbose");
        boolean showSummary = parsedArgs.containsKey("showsummary");
        result.add(new SystemOutExecutionListener(showSummary, verbose, trace));
    }

    private void addProxyForRemoteJmxListener(Map<String, List<String>> parsedArgs, List<ChorusExecutionListener> result) {
        List<String> remoteListenerHostAndPorts = parsedArgs.get("remoteJmxListener");
        for ( String hostAndPort : remoteListenerHostAndPorts ) {
            addRemoteListener(result, hostAndPort);
        }
    }

    private void addRemoteListener(List<ChorusExecutionListener> result, String hostAndPort) {
        try {
            StringTokenizer t = new StringTokenizer(hostAndPort, ":");
            String host = t.nextToken();
            int port = Integer.valueOf(t.nextToken());
            DynamicProxyMBeanCreator h = new DynamicProxyMBeanCreator(host, port);
            h.connect();
            result.add(h.createMBeanProxy(RemoteExecutionListenerMBean.JMX_EXECUTION_LISTENER_NAME, RemoteExecutionListenerMBean.class));
        } catch (Throwable t) {
            log.error("Failed to create event handler for remote execution listener at " + hostAndPort, t);
        }
    }

}
