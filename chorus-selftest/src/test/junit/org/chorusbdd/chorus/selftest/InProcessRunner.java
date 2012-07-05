package org.chorusbdd.chorus.selftest;

import org.chorusbdd.chorus.Main;
import org.chorusbdd.chorus.util.ChorusOut;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
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

    public ChorusSelfTestResults runChorusInterpreter(Properties systemProperties) {
         //some may only be applied / detected statically once per JVM session,
         //nothing we can do about that - that just won't work for 'in process' testing
         //and where this is important we may have to use just the 'forked runner'
         clearAndResetChorusSysProperties(systemProperties);

         ByteArrayOutputStream out = new ByteArrayOutputStream();
         PrintStream outStream = new PrintStream(out);

         ByteArrayOutputStream err = new ByteArrayOutputStream();
         PrintStream errStream = new PrintStream(err);

         boolean success = false;
         try {
             ChorusOut.setStdOutStream(outStream);
             ChorusOut.setStdErrStream(errStream);

             try {
                Main main = new Main(new String[0]);
                success = main.run();
             } catch (Exception e) {
                System.err.println(e.getMessage());
             }

         } finally {
             ChorusOut.setStdOutStream(System.out);
             ChorusOut.setStdErrStream(System.err);
         }

         return new ChorusSelfTestResults(out.toString(), err.toString(), success ? 0 : 1);
    }


    private void clearAndResetChorusSysProperties(Properties systemProperties) {
        //clear any existing chorus sys props
        Iterator<Map.Entry<Object,Object>> i = System.getProperties().entrySet().iterator();
        while(i.hasNext()) {
            Map.Entry<Object,Object> e = i.next();
            if ( e.getKey().toString().startsWith("chorus")) {
                i.remove();
            }
        }

        //set new chorus sys props
        i = systemProperties.entrySet().iterator();
        while(i.hasNext()) {
            Map.Entry<Object,Object> e = i.next();
            System.setProperty(e.getKey().toString(), e.getValue().toString());
        }
    }
}
