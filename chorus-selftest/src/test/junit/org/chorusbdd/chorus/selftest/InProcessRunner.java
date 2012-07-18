package org.chorusbdd.chorus.selftest;

import org.apache.log4j.Logger;
import org.apache.log4j.WriterAppender;
import org.chorusbdd.chorus.Main;
import org.chorusbdd.chorus.util.ChorusOut;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: GA2EBBU
 * Date: 05/07/12
 * Time: 11:42
 *
 * Fork an interpreter process and capture the std out, std err and exit code into ChorusSelfTestResults
 */
public class InProcessRunner implements ChorusSelfTestRunner {

    public ChorusSelfTestResults runChorusInterpreter(Properties sysPropsForTest) {
         //use log4j configuration for local tests
         sysPropsForTest.put("log4j.configuration", "org/chorusbdd/chorus/selftest/log4j-inprocess.xml");

         setSystemProperties(sysPropsForTest);

         ByteArrayOutputStream out = new ByteArrayOutputStream();
         PrintStream outStream = new PrintStream(out);

         ByteArrayOutputStream err = new ByteArrayOutputStream();
         PrintStream errStream = new PrintStream(err);

         boolean success = false;
         try {
             ChorusOut.setStdOutStream(outStream);
             ChorusOut.setStdErrStream(errStream);

             //there's a bit of jiggery pokery necessary here to get the log4j appender and
             //change it's output stream so that it writes to the buffers we created for this particular test
             WriterAppender a = (WriterAppender)Logger.getRootLogger().getAppender("chorusOut");
             a.setWriter(new PrintWriter(ChorusOut.out));

             try {
                Main main = new Main(new String[0]);
                success = main.run();
             } catch (Exception e) {
                System.err.println("Failed while running tests in line " + e.getMessage() + e);
                e.printStackTrace();
             }

         } finally {
             ChorusOut.setStdOutStream(System.out);
             ChorusOut.setStdErrStream(System.err);
         }

         return new ChorusSelfTestResults(out.toString(), err.toString(), success ? 0 : 1);
    }


    private void setSystemProperties(Properties testProperties) {
        //clear any existing chorus sys props
        Iterator<Map.Entry<Object,Object>> i = System.getProperties().entrySet().iterator();
        while(i.hasNext()) {
            Map.Entry<Object,Object> e = i.next();
            if ( e.getKey().toString().startsWith("chorus")) {
                i.remove();
            }
        }

        //set new chorus sys props
        i = testProperties.entrySet().iterator();
        while(i.hasNext()) {
            Map.Entry<Object,Object> e = i.next();
            System.setProperty(e.getKey().toString(), e.getValue().toString());
        }
    }
}
