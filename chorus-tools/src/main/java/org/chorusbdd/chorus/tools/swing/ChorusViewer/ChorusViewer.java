package org.chorusbdd.chorus.tools.swing.chorusviewer;

import org.chorusbdd.chorus.Main;
import org.chorusbdd.chorus.core.interpreter.ChorusExecutionListener;
import org.chorusbdd.chorus.remoting.jmx.RemoteExecutionListener;
import org.chorusbdd.chorus.remoting.jmx.RemoteExecutionListenerMBean;
import org.chorusbdd.chorus.tools.util.AwtSafeListener;
import org.chorusbdd.chorus.util.CommandLineParser;
import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;

import javax.management.*;
import javax.swing.*;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 15/05/12
 * Time: 15:03
 *
 * A simple viewer application for Chorus test progress
 */
public class ChorusViewer {

    private static ChorusLog log = ChorusLogFactory.getLog(ChorusViewer.class);

    private ChorusViewerMainFrame frame;

    public ChorusViewer() throws Exception {

        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
            frame = new ChorusViewerMainFrame();
            frame.setVisible(true);
            }
        });
    }

    public boolean runFeatures(String[] args) throws Exception {
        boolean success = true;
        if ( args.length > 0 ) {
            //we are executing in standalone one off test mode
            //run the tests, adding the ChorusViewer as the execution listener
            Map<String, List<String>> parsedArgs = CommandLineParser.parseArgs(args);
            ChorusExecutionListener l = AwtSafeListener.getAwtInvokeLaterListener(frame, ChorusExecutionListener.class);
            success = Main.run(parsedArgs, l);
        }
        setUpJmxExecutionListener();
        return success;
    }

    public static void main(String[] args) throws Exception {
        ChorusViewer v = new ChorusViewer();
        v.runFeatures(args);
    }

    private void setUpJmxExecutionListener() {
        if ( System.getProperty("com.sun.management.jmxremote") != null) {
            ChorusExecutionListener l = AwtSafeListener.getAwtInvokeLaterListener(frame, ChorusExecutionListener.class);
            RemoteExecutionListener r = new RemoteExecutionListener(l);
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            try {
                mbs.registerMBean(r, new ObjectName(RemoteExecutionListenerMBean.JMX_EXECUTION_LISTENER_NAME));
            } catch (Exception e) {
                log.error("Failed to register jmx execution listener", e);
            }
        }
    }

}
