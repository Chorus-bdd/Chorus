package org.chorusbdd.chorus.tools.swing.ChorusViewer;

import org.chorusbdd.chorus.Main;
import org.chorusbdd.chorus.core.interpreter.ChorusExecutionListener;
import org.chorusbdd.chorus.util.CommandLineParser;

import javax.swing.*;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: GA2EBBU
 * Date: 15/05/12
 * Time: 15:03
 *
 * A simple viewer application for Chorus test progress
 */
public class ChorusViewer {

    ChorusViewerMainFrame frame;

    public ChorusViewer(String[] args) throws Exception {

        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                frame = new ChorusViewerMainFrame();
                frame.setVisible(true);
            }
        });

        if ( args.length > 0 ) {
            //we are executing in standalone one off test mode
            //run the tests, adding the ChorusViewer as the execution listener
            Map<String, List<String>> parsedArgs = CommandLineParser.parseArgs(args);
            ChorusExecutionListener l = AwtSafeListener.getAwtSafeListener(frame, ChorusExecutionListener.class);
            Main.run(parsedArgs, l);
        }
    }

    public static void main(String[] args) throws Exception {
        new ChorusViewer(args);
    }

}
