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
package org.chorusbdd.chorus.tools.swing.viewer;

import org.chorusbdd.chorus.Chorus;
import org.chorusbdd.chorus.core.interpreter.ExecutionListener;
import org.chorusbdd.chorus.remoting.jmx.RemoteExecutionListener;
import org.chorusbdd.chorus.remoting.jmx.RemoteExecutionListenerMBean;
import org.chorusbdd.chorus.tools.util.AwtSafeListener;
import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.swing.*;
import java.lang.management.ManagementFactory;

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

    public ChorusViewer() {}

    private void createUI() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                frame = new ChorusViewerMainFrame();
                frame.setVisible(true);
            }
        });
    }

    private boolean runFeatures(String[] args) throws Exception {
        boolean success = true;
        if ( args.length > 0 ) {
            //we are executing in standalone one off test mode
            //run the tests, adding the ChorusViewer as the execution listener
            ExecutionListener l = AwtSafeListener.getAwtInvokeLaterListener(frame, ExecutionListener.class);
            Chorus chorus = new Chorus(args);
            chorus.setExecutionListener(l);
            chorus.run();
        }
        setUpJmxExecutionListener();
        return success;
    }

    public static void main(String[] args) throws Exception {
        ChorusViewer v = new ChorusViewer();
        v.createUI();
        v.runFeatures(args);
    }

    private void setUpJmxExecutionListener() {
        if ( System.getProperty("com.sun.management.jmxremote") != null) {
            ExecutionListener l = AwtSafeListener.getAwtInvokeLaterListener(frame, ExecutionListener.class);
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
