package org.chorusbdd.chorus.selftest;

import junit.framework.TestCase;
import org.chorusbdd.chorus.tools.swing.viewer.ChorusViewer;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 11/05/12
 * Time: 22:27
 *
 * A unit test which kicks off the Chorus interpreter and runs the spring-specific
 * fixtures from chorus-spring
 *
 * Run this in forking mode
 *
 * At present I can't find a way to create an idea run config which executes these directly, using the maven test
 * classpath - the only way I can find to make it work is to wrap these in a junit test.
 */
public class RunChorusViewer extends TestCase {

    @Test
    public void testChorusViewer() {

//        String[] args = new String[] {
//            "-verbose", "-showsummary", "-trace", "-f", "src/test/features", "-h", "org.chorusbdd.chorus.selftest.handlers", "-jmxListenerPort", "999"
//        };

        String[] args = new String[] {};

        try {
            ChorusViewer v = new ChorusViewer();
            if ( ! v.runFeatures(args)) {
                fail("Some tests failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
