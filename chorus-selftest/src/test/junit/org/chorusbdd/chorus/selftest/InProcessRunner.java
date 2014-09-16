/**
 *  Copyright (C) 2000-2013 The Software Conservancy and Original Authors.
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
package org.chorusbdd.chorus.selftest;

import org.apache.log4j.Logger;
import org.apache.log4j.WriterAppender;
import org.chorusbdd.chorus.Chorus;
import org.chorusbdd.chorus.config.ConfigProperties;
import org.chorusbdd.chorus.config.ConfigurationProperty;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.logging.ChorusLogProvider;
import org.chorusbdd.chorus.logging.ChorusOut;
import org.chorusbdd.chorus.output.OutputFactory;
import org.chorusbdd.chorus.output.OutputFormatter;
import org.chorusbdd.chorus.output.OutputFormatterLogProvider;
import org.chorusbdd.chorus.output.PlainOutputFormatter;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
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

    static {
        //initialize the logging subsystem up front, with defaults
        //this fixes the OutputFormatter and LogProvider instance for all the in process tests - this is important
        //since during testing we set the formatters print stream to make it write to our test byte array buffers for 
        //output comparison
        OutputFormatter outputFormatter = new PlainOutputFormatter();
        OutputFactory.setOutputFormatter(outputFormatter);

        OutputFormatterLogProvider logProvider = new OutputFormatterLogProvider();
        logProvider.setOutputFormatter(outputFormatter);
        ChorusLogFactory.setLogProvider(logProvider);
    }
    
    public ChorusSelfTestResults runChorusInterpreter(Properties sysPropsForTest) {
        
         //use log4j configuration
         //this will avoid the log4j warning for tests which use Spring and hence pull in log4j
         //this will only be used for Chorus log output if a test configures
         //System.setProperty("chorusLogProvider", "org.chorusbdd.chorus.util.logging.ChorusCommonsLogProvider");
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
             
             OutputFactory.getOutputFormatter().setPrintStream(outStream);

             //there's a bit of jiggery pokery necessary here to get the log4j appender and
             //change it's output stream so that it writes to the buffers we created for this particular test
             //none of this is required unless we are running with ChorusCommonsLogProvider
             WriterAppender a = (WriterAppender)Logger.getRootLogger().getAppender("chorusOut");
             a.setWriter(new PrintWriter(ChorusOut.out));

             try {
                Chorus chorus = new Chorus(new String[0]);
                success = chorus.run();
             } catch (Exception e) {
                System.err.println("Failed while running tests in line " + e.getMessage() + e);
                e.printStackTrace();
             }

         } finally {
             ChorusOut.setStdOutStream(System.out);
             ChorusOut.setStdErrStream(System.err);
             OutputFactory.getOutputFormatter().setPrintStream(System.out);
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

    //a null properties to use when initializing the logging subsystem for the tests, we will end up with the defaults
    private static class NullConfigProperties implements ConfigProperties {
        public void setProperty(ConfigurationProperty property, List<String> values) {
        }

        public List<String> getValues(ConfigurationProperty property) {
            return null;
        }

        public String getValue(ConfigurationProperty property) {
            return null;
        }

        public boolean isSet(ConfigurationProperty property) {
            return false;
        }

        public boolean isTrue(ConfigurationProperty property) {
            return false;
        }

        public ConfigProperties deepCopy() {
            return null;
        }
    }
}
