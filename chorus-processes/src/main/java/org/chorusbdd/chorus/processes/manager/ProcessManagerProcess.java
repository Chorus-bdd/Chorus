/**
 *  Copyright (C) 2000-2014 The Software Conservancy and Original Authors.
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
package org.chorusbdd.chorus.processes.manager;

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.processes.manager.config.LogFileAndMode;
import org.chorusbdd.chorus.processes.manager.patternmatching.PatternMatcherFactory;
import org.chorusbdd.chorus.processes.manager.patternmatching.ProcessOutputPatternMatcher;
import org.chorusbdd.chorus.util.assertion.ChorusAssert;

import java.io.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
* Created with IntelliJ IDEA.
* User: nick
* Date: 20/09/12
* Time: 22:22
* 
* A ChorusProcess using JDK 1.7 ProcessHandler
*/
class ProcessManagerProcess implements ChorusProcess {

    private static ChorusLog log = ChorusLogFactory.getLog(ProcessManagerProcess.class);
    private final ProcessBuilder processBuilder;
    protected String name;
    protected Process process;

    private PatternMatcherFactory patternMatcherFactory = new PatternMatcherFactory();
    private ProcessOutputPatternMatcher stdOutPatternMatcher;
    private ProcessOutputPatternMatcher stdErrPatternMatcher;

    //output stream/writer to write to the process std in
    private OutputStream outputStream;
    private BufferedWriter outputWriter;
    private ProcessOutputConfiguration outputConfig;

    public ProcessManagerProcess(String name, List<String> command, ProcessOutputConfiguration outputConfig) throws Exception {
        this.name = name;
        this.outputConfig = outputConfig;
        processBuilder = new ProcessBuilder(command);
    }

    public void start() throws IOException {
        stdOutPatternMatcher = setUpOutput(outputConfig.getStdOutFileAndMode());
        stdErrPatternMatcher = setUpOutput(outputConfig.getStdErrFileAndMode());

        this.process = processBuilder.start();
        log.debug("Started process " + process + " with log output " + getOutputConfig());
    }

    private ProcessOutputPatternMatcher setUpOutput(LogFileAndMode logFileAndMode) {
        redirectOutput(logFileAndMode);
        return patternMatcherFactory.createPatternMatcher(this, logFileAndMode);
    }

    private void redirectOutput(LogFileAndMode logFileAndMode) {
        switch (logFileAndMode.getMode()) {
            case FILE :
            case CAPTURED :
            case CAPTUREDWITHLOG :
                File logFile = logFileAndMode.getFile();
                log.debug("Will write process " + logFileAndMode.getStreamDescription() + " to file at " + logFile);
                redirectOutputToFile(logFile, logFileAndMode.isStdError());
                break;
            case INLINE :
                redirectOutputInline(logFileAndMode.isStdError());
                break;
        }
    }

    private void redirectOutputInline(boolean isErrStream) {
        if ( isErrStream ) {
            processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
        } else {
            processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        }
    }

    private void redirectOutputToFile(File logFile, boolean isErrStream) {
        if ( isErrStream ) {
            processBuilder.redirectError(
                    outputConfig.isAppendToLogs() ?
                            ProcessBuilder.Redirect.appendTo(logFile) :
                            ProcessBuilder.Redirect.to(logFile)
            );
        } else {
            processBuilder.redirectOutput(
                outputConfig.isAppendToLogs() ?
                        ProcessBuilder.Redirect.appendTo(logFile) :
                        ProcessBuilder.Redirect.to(logFile)
            );
        }
    }

    public boolean isStopped() {
        boolean stopped = true;
        try {
            process.exitValue();
        } catch (IllegalThreadStateException e) {
            stopped = false;
        }
        return stopped;
    }

    public void destroy() {
        try {
            // destroying the process will close its stdout/stderr and so cause our ProcessRedirector daemon threads to exit
            log.debug("Destroying process " + process);
            process.destroy();
            try {
                //this ensures that all of the processes resources are cleaned up before proceeding
                process.waitFor();
            } catch (InterruptedException e) {
                log.error("Interrupted while waiting for process to terminate",e);
            }
        } finally {
            closeStreams();
        }
    }

    public void waitFor() throws InterruptedException {
        process.waitFor();
    }

    protected ChorusLog getLog() {
        return log;
    }

    ProcessOutputConfiguration getOutputConfig() {
        return outputConfig;
    }

    public String toString() {
        return getClass().getSimpleName() + "{" +
                "name='" + name + '}';
    }

    /**
     * Write the text to the std in of the process
     * @param newLine append a new line
     */
    public void writeToStdIn(String text, boolean newLine) {
        if ( outputStream == null) {
            outputStream = new BufferedOutputStream(process.getOutputStream());
            outputWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
        }

        try {
            outputWriter.write(text);
            if ( newLine ) {
                outputWriter.newLine();
            }
            outputWriter.flush();
            outputStream.flush();
        } catch (IOException e) {
            getLog().debug("Error when writing to process in for " + this, e);
            ChorusAssert.fail("IOException when writing line to process");
        }
    }

    public boolean isExitCodeFailure() {
        return process.exitValue() != 0;
    }

    public int getExitCode() {
        return process.exitValue();
    }

    /**
     * Check the process for a short time after it is started, and only pass the start process step
     * if the process has not terminated with a non-zero (error) code
     *
     * @param processCheckDelay
     * @throws Exception
     */
    public void checkProcess(int processCheckDelay) throws Exception {
        //process checking can be turned off by setting delay == -1
        if ( processCheckDelay > 0) {
            int cumulativeSleepTime = 0;
            boolean stopped;
            while(cumulativeSleepTime < processCheckDelay) {
                int sleep = Math.min(50, processCheckDelay - cumulativeSleepTime);
                Thread.sleep(sleep);
                cumulativeSleepTime += sleep;

                stopped = isStopped();
                if ( stopped ) {
                    if ( isExitCodeFailure()) {
                        throw new ProcessCheckFailedException(
                                "Process terminated with a non-zero exit code during processCheckDelay period, step fails");
                    } else {
                        getLog().debug("Process stopped during processCheckDelay period, exit code zero, passing step");
                        break;
                    }
                }

                if ( ! stopped ) {
                    getLog().debug("Process still running after processCheckDelay period, passing step");
                }
            }

        }
    }

    public void waitForMatchInStdOut(String pattern, boolean searchWithinLines) {
        stdOutPatternMatcher.waitForMatch(pattern, searchWithinLines, TimeUnit.SECONDS, outputConfig.getReadTimeoutSeconds());
    }

    public void waitForMatchInStdErr(String pattern, boolean searchWithinLines) {
        stdErrPatternMatcher.waitForMatch(pattern, searchWithinLines, TimeUnit.SECONDS, outputConfig.getReadTimeoutSeconds());
    }

    public void waitForMatchInStdOut(String pattern, boolean searchWithinLines, TimeUnit timeUnit, long length) {
        stdOutPatternMatcher.waitForMatch(pattern, searchWithinLines, timeUnit, length);
    }

    public void waitForMatchInStdErr(String pattern, boolean searchWithinLines, TimeUnit timeUnit, long length) {
        stdErrPatternMatcher.waitForMatch(pattern, searchWithinLines, timeUnit, length);
    }

    protected void closeStreams() {

        stdErrPatternMatcher.close();
        stdOutPatternMatcher.close();

        if ( outputWriter != null ) {
            try {
                getLog().trace("Closing output writer for process " + this);
                outputWriter.close();
                outputWriter = null;
            } catch (IOException e) {
                getLog().trace("Failed to flush and close output writer", e);
            }
        }

        if ( outputStream != null ) {
            try {
                getLog().trace("Closing output stream for process " + this);
                outputStream.close();
                outputStream = null;
            } catch (IOException e) {
                getLog().trace("Failed to close output stream for process", e);
            }
        }
    }
}
