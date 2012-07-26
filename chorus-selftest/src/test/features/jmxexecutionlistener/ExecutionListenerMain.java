package jmxexecutionlistener;

import org.chorusbdd.chorus.core.interpreter.ExecutionListener;
import org.chorusbdd.chorus.executionlistener.SystemOutExecutionListener;
import org.chorusbdd.chorus.remoting.jmx.RemoteExecutionListener;
import org.chorusbdd.chorus.remoting.jmx.RemoteExecutionListenerMBean;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 04/07/12
 * Time: 09:16
 */
public class ExecutionListenerMain {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting Remote JMX execution listener");
        startJmx();
        Thread.sleep(10000);
    }

    private static void startJmx() {
        ExecutionListener l = new SystemOutExecutionListener(true, false);
        RemoteExecutionListener r = new RemoteExecutionListener(l);
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        try {
            mbs.registerMBean(r, new ObjectName(RemoteExecutionListenerMBean.JMX_EXECUTION_LISTENER_NAME));
        } catch (Exception e) {
            System.err.println("Failed to register jmx execution listener " + e);
        }
    }

}
