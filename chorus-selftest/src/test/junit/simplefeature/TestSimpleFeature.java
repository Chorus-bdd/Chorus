package simplefeature;

import junit.framework.Assert;
import org.chorusbdd.chorus.handlers.ProcessesHandler;
import org.junit.Test;

import java.io.*;
import java.util.Map;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 25/06/12
 * Time: 22:14
 */
public class TestSimpleFeature extends Assert {

    @Test
    public void testSimpleFeature() throws Exception {
        Properties sysProps = new Properties();
        sysProps.put("chorusFeaturePaths", "src/test/features/simplefeature");
        sysProps.put("chorusHandlerPackages", "simplefeature");
        sysProps.put("logProvider", "org.chorusbdd.chorus.util.logging.StandardOutLogProvider");

        ChorusSelfTestResults r = runChorusInterpreter(sysProps);

        ChorusSelfTestResults expectedResults = new ChorusSelfTestResults(
                "Feature: Simple Feature                                                                              \n" +
                "  Scenario: Simple Scenario\n" +
                "    Given Chorus is working properly                                                         PASSED  \n" +
                "    Then I can run a feature with a single scenario                                          PASSED  \n" +
                "\n" +
                "\n" +
                "Scenarios (total:1) (passed:1) (failed:0)\n" +
                "Steps (total:2) (passed:2) (failed:0) (undefined:0) (pending:0) (skipped:0)\n",
                "",
                0);

        //System.out.println("*" + r.getStandardOutput() + "*");
        //System.out.println("*" + expectedResults.getStandardOutput() + "*");

        assertEquals("Interpreter exit code", expectedResults.getInterpreterExitCode(), r.getInterpreterExitCode());
        assertEquals("Interpreter std out", expectedResults.getStandardOutput(), r.getStandardOutput().replaceAll("\r", ""));
        assertEquals("Interpreter std err", expectedResults.getStandardError(), r.getStandardError().replaceAll("\r", ""));

    }

    private ChorusSelfTestResults runChorusInterpreter(Properties systemProperties) throws IOException, InterruptedException {
        String jre = System.getProperty("java.home");

        //See notes also in ProcessHandler
        //surrounding the classpath in quotes is currently breaking the classpath parsing for linux when launched via
        //Runtime.getRuntime().exec() (but it is ok from the shell)
        //I think we want to keep this in on windows, since we will more likely encounter directory names with spaces -
        //I'm worried those will break for linux although this will fix the classpath issue.
        //-so this workaround at least gets things working, but may break for folders with spaces in the name on 'nix
        boolean isWindows = System.getProperty("os.name").toLowerCase().indexOf("win") >= 0;
        String commandTxt = isWindows ?
                "%s%sbin%sjava %s -classpath \"%s\" %s %s" :
                "%s%sbin%sjava %s -classpath %s %s %s";

        String classPath = System.getProperty("java.class.path");

        String args = "";

        StringBuilder jvmArgs = new StringBuilder();
        for ( Map.Entry<Object,Object> property : systemProperties.entrySet()) {
            jvmArgs.append("-D");
            jvmArgs.append(property.getKey());
            jvmArgs.append("=");
            jvmArgs.append(property.getValue());
            jvmArgs.append(" ");
        }

        //construct a command
        String command = String.format(
                commandTxt,
                jre,
                File.separatorChar,
                File.separatorChar,
                jvmArgs,
                classPath,
                "org.chorusbdd.chorus.Main",
                args).trim();

        System.out.println("About to run Java: " + command);

        ByteArrayOutputStream interpreterOut = new ByteArrayOutputStream();
        ByteArrayOutputStream interpreterErr = new ByteArrayOutputStream();

        Process process = Runtime.getRuntime().exec(command);
        ProcessesHandler.ProcessRedirector outRedirector = new ProcessesHandler.ProcessRedirector(process.getInputStream(), new PrintStream(interpreterOut), false);
        ProcessesHandler.ProcessRedirector errRedirector = new ProcessesHandler.ProcessRedirector(process.getErrorStream(), new PrintStream(interpreterErr), false);

        Thread outThread = new Thread(outRedirector, "interpreter-stdout");
        outThread.setDaemon(true);
        outThread.start();
        Thread errThread = new Thread(errRedirector, "interpreter-stderr");
        errThread.setDaemon(true);
        errThread.start();

        int result = process.waitFor();

        Thread.sleep(1000);

        return new ChorusSelfTestResults(
            interpreterOut.toString("UTF-8"),
            interpreterErr.toString("UTF-8"),
            result
        );
    }

    private static class ChorusSelfTestResults {

        private String standardOutput;
        private String standardError;
        private int interpreterReturnCode;

        private ChorusSelfTestResults(String standardOutput, String standardError, int interpreterReturnCode) {
            this.standardOutput = standardOutput;
            this.standardError = standardError;
            this.interpreterReturnCode = interpreterReturnCode;
        }

        public String getStandardOutput() {
            return standardOutput;
        }

        public String getStandardError() {
            return standardError;
        }

        public int getInterpreterExitCode() {
            return interpreterReturnCode;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ChorusSelfTestResults that = (ChorusSelfTestResults) o;

            if (interpreterReturnCode != that.interpreterReturnCode) return false;
            if (standardError != null ? !standardError.equals(that.standardError) : that.standardError != null)
                return false;
            if (standardOutput != null ? !standardOutput.equals(that.standardOutput) : that.standardOutput != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = standardOutput != null ? standardOutput.hashCode() : 0;
            result = 31 * result + (standardError != null ? standardError.hashCode() : 0);
            result = 31 * result + interpreterReturnCode;
            return result;
        }

        @Override
        public String toString() {
            return "ChorusSelfTestResults{" +
                    "standardOutput='" + standardOutput + '\'' +
                    ", standardError='" + standardError + '\'' +
                    ", interpreterReturnCode=" + interpreterReturnCode +
                    '}';
        }
    }
}
