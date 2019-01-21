/**
 * MIT License
 *
 * Copyright (c) 2019 Chorus BDD Organisation.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.chorusbdd.chorus.selftest;

import org.chorusbdd.chorus.config.ExecutionProperty;
import org.chorusbdd.chorus.handlerconfig.configproperty.ConfigBuilder;
import org.chorusbdd.chorus.output.AbstractChorusOutputWriter;
import org.chorusbdd.chorus.interpreter.startup.ChorusConfigProperty;
import org.chorusbdd.chorus.processes.manager.commandlinebuilder.JavaProcessCommandLineBuilder;
import org.chorusbdd.chorus.processes.manager.config.ProcessConfigBean;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: Nick E
 * Date: 05/07/12
 * Time: 11:44
 *
 * Fork an interpreter process and capture the std out, std err and exit code into ChorusSelfTestResults
 */
public class ForkedRunner implements ChorusSelfTestRunner {

    //these are only ever set with a system property not a switch
    public static final List SYS_PROP_ONLY_PROPERTIES = Arrays.asList(
            AbstractChorusOutputWriter.OUTPUT_FORMATTER_STEP_LENGTH_CHARS,
            AbstractChorusOutputWriter.OUTPUT_FORMATTER_STEP_LOG_RATE
    );

    public ChorusSelfTestResults runChorusInterpreter(Properties sysPropsForTest) throws Exception {
        return runForked(sysPropsForTest, "org.chorusbdd.Chorus", System.out, 30000);
    }

    public ChorusSelfTestResults runForked(Properties sysPropsForTest, String mainClass, PrintStream stdOutStream, int timeout) throws Exception {

        String switches = getSwitches(sysPropsForTest);

        StringBuilder jvmArgs = getJvmArgs(sysPropsForTest);

        Properties properties = new Properties();
        properties.put(ProcessConfigBean.JVMARGS_PROPERTY, jvmArgs.toString());
        properties.put(ProcessConfigBean.ARGS_PROPERTY, switches);
        properties.put(ProcessConfigBean.MAINCLASS_PROPERTY, mainClass);

        ProcessConfigBean config = new ConfigBuilder().buildConfig(ProcessConfigBean.class, properties);
        JavaProcessCommandLineBuilder javaProcessCommandLineBuilder = new JavaProcessCommandLineBuilder(
            new File(System.getProperty("user.dir")),
            config,
            "forkedTests"
        );

        List<String> command = javaProcessCommandLineBuilder.buildCommandLine();
//        System.out.println("About to run process: " + command);

        ByteArrayOutputStream processOut = new ByteArrayOutputStream();
        ByteArrayOutputStream processErr = new ByteArrayOutputStream();

        Process process = Runtime.getRuntime().exec(command.toArray(new String[command.size()]));
        ProcessRedirector outRedirector = new ProcessRedirector(process.getInputStream(), false, new PrintStream(processOut), stdOutStream);  //dumping both to out
        ProcessRedirector errRedirector = new ProcessRedirector(process.getErrorStream(), false, new PrintStream(processErr), stdOutStream);  //tend to get more consistent ordering

        Thread outThread = new Thread(outRedirector, "stdout-redirector");
        outThread.setDaemon(true);
        outThread.start();
        Thread errThread = new Thread(errRedirector, "stderr-redirector");
        errThread.setDaemon(true);
        errThread.start();

        int result = 0;
        if ( timeout > 0 ) {
            result = process.waitFor();
            //wait for logging to be flushed to output byte array asynchronously
            errThread.join(timeout);
            outThread.join(timeout);
        }

        return new ChorusSelfTestResults(
            processOut.toString("UTF-8"),
            processErr.toString("UTF-8"),
            result
        );
    }

    private String getSwitches(Properties sysPropsForTest) {
        StringBuilder sb = new StringBuilder();
        for ( Map.Entry<Object,Object> property : sysPropsForTest.entrySet()) {
            String propertyName = property.getKey().toString();
            if ( isAChorusSwitchProperty(propertyName)) {
                ExecutionProperty c = ChorusConfigProperty.getConfigPropertyForSysProp(propertyName);
                sb.append(" ").append(c.getHyphenatedSwitch());
                sb.append(" ").append(property.getValue().toString());
            }
        }
        return sb.toString();
    }


    private StringBuilder getJvmArgs(Properties sysPropsForTest) {
        StringBuilder jvmArgs = new StringBuilder();
        for ( Map.Entry<Object,Object> property : sysPropsForTest.entrySet()) {
            //use switches instead of sys props for chorus properties when running forked
            //This tests the switches, but is also necessary since property values which include
            //spaces otherwise do not work - surrounding a sys prop value in quotes "" or '' does not
            //reliably work for both win and nix
            String key = property.getKey().toString();
            if (! isAChorusSwitchProperty(key)) {
                jvmArgs.append("-D");
                jvmArgs.append(key);
                if ( ! property.getValue().equals("")) {
                    jvmArgs.append("=");
                    jvmArgs.append(property.getValue());
                }
                jvmArgs.append(" ");
            }
        }
        return jvmArgs;
    }

    private boolean isAChorusSwitchProperty(String key) {
        return key.toString().startsWith("chorus") && ! SYS_PROP_ONLY_PROPERTIES.contains(key);
    }
}
