package org.chorusbdd.chorus.selftest;

/**
* Created by IntelliJ IDEA.
* User: Nick Ebbutt
* Date: 26/06/12
* Time: 08:42
*/
public class ChorusSelfTestResults {

    private String standardOutput;
    private String standardError;
    private int interpreterReturnCode;

    /**
     * Create a new ChorusSelfTestResults
     * Strip any carriage return characters from standard err and out
     */
    public ChorusSelfTestResults(String standardOutput, String standardError, int interpreterReturnCode) {
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

    public void preProcessForTests() {
        this.standardOutput = preProcessTestResultOutput(this.standardOutput);
        this.standardError = preProcessTestResultOutput(this.standardError);
    }

    private String preProcessTestResultOutput(String output) {
        output = removeCarriageReturns(output);
        output = removeJavaOptionsVariable(output);
        output = replaceWindowsWithUnixPaths(output);
        return output;
    }

    //ensure we have consistent paths to compare
    private String replaceWindowsWithUnixPaths(String output) {
        return output.replace('\\', '/');
    }

    //this appears in the std out in some envs where a sys property _JAVA_OPTIONS is set
    private String removeJavaOptionsVariable(String result) {
        return result.replaceAll("Picked up _JAVA_OPTIONS: .*?\n", "");
    }

    private String removeCarriageReturns(String output) {
        return output.replaceAll("\r", "");
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
