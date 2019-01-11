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

    public void setStandardOutput(String standardOutput) {
        this.standardOutput = standardOutput;
    }

    public void setStandardError(String standardError) {
        this.standardError = standardError;
    }

    public void setInterpreterReturnCode(int interpreterReturnCode) {
        this.interpreterReturnCode = interpreterReturnCode;
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
        output = replaceSystemSpecificProcessDetails(output);
        output = replaceLineNumbers(output);
        output = replaceSystemExceptions(output);
        return output;
    }

    private String replaceSystemSpecificProcessDetails(String output) {
        return output.replaceAll("About to run process: " + ".*", "About to run process: " + " <system specific process details replaced>");
    }

    //ensure we have consistent paths to compare
    private String replaceWindowsWithUnixPaths(String output) {
        return output.replace('\\', '/');
    }

    //this appears in the std out in some envs where a sys property _JAVA_OPTIONS is set
    private String removeJavaOptionsVariable(String result) {
        return result.replaceAll("Picked up _JAVA_OPTIONS: .*?\n", "");
    }

    private String replaceLineNumbers(String stdOut) {
        return stdOut.replaceAll("\\(([\\w\\$]+)" + ":\\d+\\)-", "($1:linenumber)-");
    }

    private String replaceSystemExceptions(String stdOut) {
        return stdOut.replaceAll(", caused by: \\[(.*)\\]", " caused by: [replaced]");
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
