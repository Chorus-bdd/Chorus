package simplefeature;

/**
* Created by IntelliJ IDEA.
* User: Nick Ebbutt
* Date: 26/06/12
* Time: 08:42
*/
class ChorusSelfTestResults {

    private String standardOutput;
    private String standardError;
    private int interpreterReturnCode;

    /**
     * Create a new ChorusSelfTestResults
     * Strip any carriage return characters from standard err and out
     */
    ChorusSelfTestResults(String standardOutput, String standardError, int interpreterReturnCode) {
        this.standardOutput = standardOutput.replaceAll("\r", "");
        this.standardError = standardError.replaceAll("\r", "");
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
    public String toString() {
        return "ChorusSelfTestResults{" +
                "standardOutput='" + standardOutput + '\'' +
                ", standardError='" + standardError + '\'' +
                ", interpreterReturnCode=" + interpreterReturnCode +
                '}';
    }
}
