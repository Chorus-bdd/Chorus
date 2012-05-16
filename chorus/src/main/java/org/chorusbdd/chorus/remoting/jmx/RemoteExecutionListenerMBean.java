package org.chorusbdd.chorus.remoting.jmx;

import org.chorusbdd.chorus.core.interpreter.ChorusExecutionListener;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 16/05/12
 * Time: 16:48
 *
 */
public interface RemoteExecutionListenerMBean extends ChorusExecutionListener {

    public static String JMX_EXECUTION_LISTENER_NAME = "org.chorusbdd.chorus:name=chorus_execution_listener";

}
