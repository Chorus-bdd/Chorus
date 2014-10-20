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

import org.chorusbdd.chorus.context.ChorusContext;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.util.assertion.ChorusAssert;

import java.io.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: nick
 * Date: 24/07/13
 * Time: 08:55
 */
public abstract class AbstractChorusProcess implements ChorusProcess {

    public static final String LAST_MATCH = "ProcessesHandler.match";
    
    //when reading logs to match against process output
    private TailLogBufferedReader stdOutLogInputStream;
    private TailLogBufferedReader stdErrLogInputStream;

    //output stream/writer to write to the process std in
    private OutputStream outputStream;
    private BufferedWriter outputWriter;
    
    protected String name;
    private ProcessLogOutput logOutput;

    protected Process process;

    public AbstractChorusProcess(String name, ProcessLogOutput logOutput) {
        this.name = name;
        this.logOutput = logOutput;
    }
    
    protected abstract ChorusLog getLog();

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

    public void waitForMatchInStdOut(String pattern, boolean searchWithinLines) {
        waitForMatchInStdOut(pattern, searchWithinLines, TimeUnit.SECONDS, logOutput.getReadTimeoutSeconds());
    }

    public void waitForMatchInStdOut(String pattern, boolean searchWithinLines, TimeUnit timeUnit, long length) {
        ChorusAssert.assertTrue("Process std out mode cannot be INLINE when pattern matching", OutputMode.canSearchOutput(logOutput.getStdOutMode()));
        if ( stdOutLogInputStream == null) {
            stdOutLogInputStream = new TailLogBufferedReader(logOutput.getStdOutLogFile());
        }

        long timeoutMilliseconds = timeUnit.toMillis(length);
        waitForOutputPattern(pattern, stdOutLogInputStream, searchWithinLines, timeoutMilliseconds);
    }

    public void waitForMatchInStdErr(String pattern, boolean searchWithinLines) {
        waitForMatchInStdErr(pattern, searchWithinLines, TimeUnit.SECONDS, logOutput.getReadTimeoutSeconds());
    }

    public void waitForMatchInStdErr(String pattern, boolean searchWithinLines, TimeUnit timeUnit, long length) {
        ChorusAssert.assertTrue("Process std err mode cannot be INLINE when pattern matching", OutputMode.canSearchOutput(logOutput.getStdErrMode()));
        if ( stdErrLogInputStream == null) {
            stdErrLogInputStream = new TailLogBufferedReader(logOutput.getStdErrLogFile());
        }

        long timeoutMilliseconds = timeUnit.toMillis(length);
        waitForOutputPattern(pattern, stdErrLogInputStream, searchWithinLines, timeoutMilliseconds);
    }

    private void waitForOutputPattern(String pattern, TailLogBufferedReader bufferedReader, boolean searchWithinLines, long timeoutMilliseconds) {
        Pattern p = Pattern.compile(pattern);
        long timeout = System.currentTimeMillis() + timeoutMilliseconds;
        try {
            String matched = waitForPattern(timeout, bufferedReader, p, searchWithinLines, timeoutMilliseconds / 1000);
            
            //store into the ChorusContext the exact string which matched the pattern so this can be used
            //in subsequent test steps
            ChorusContext.getContext().put(LAST_MATCH, matched);
        } catch (IOException e) {
            getLog().warn("Failed while matching pattern " + p, e);
            ChorusAssert.fail("Failed while matching pattern");
        }
    }

    //read ahead without blocking and attempt to match the pattern
    private String waitForPattern(long timeout, TailLogBufferedReader bufferedReader, Pattern pattern, boolean searchWithinLines, long timeoutInSeconds) throws IOException {
        StringBuilder sb = new StringBuilder();
        String result = null;
        label:
        while(true) {
            while ( bufferedReader.ready() ) {
                int c = bufferedReader.read();
                if ( c != -1 ) {
                    if (c == '\n' || c == '\r') {
                        if (sb.length() > 0) {
                            Matcher m = pattern.matcher(sb);
                            boolean match = searchWithinLines ? m.find() : m.matches();
                            if (match) {
                                result = sb.toString();
                                break label;
                            } else {
                                sb.setLength(0);
                            }
                        }
                    } else {
                        sb.append((char) c);
                    }
                }
            } 
            
            //nothing more to read, does the current output match the pattern?
            if ( sb.length() > 0 && searchWithinLines) {
                Matcher m = pattern.matcher(sb);
                if ( m.find() ) {
                    result = m.group(0);
                    break label;
                }
            }
            
            try {
                Thread.sleep(10); //avoid a busy loop since we are using nonblocking ready() / read()
            } catch (InterruptedException e) {}

            checkTimeout(timeout, timeoutInSeconds);

            if ( isStopped() && ! bufferedReader.ready()) {
                ChorusAssert.fail(
                    isExitCodeFailure() ? 
                        "Process stopped with error code " + getExitCode() + " while waiting for match" :
                        "Process stopped while waiting for match"
                );
            }
        }
        return result;
    }

    private void checkTimeout(long timeout, long seconds) {
        if ( System.currentTimeMillis() > timeout ) {
            ChorusAssert.fail("Timed out after " + seconds + " seconds");
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

    protected void closeStreams() {
        
        if ( stdOutLogInputStream != null) {
            try {
                stdOutLogInputStream.close();
            } catch (IOException e) {
                getLog().trace("Failed to close stdOutLogInputStream", e);
            }
        }

        if ( stdErrLogInputStream != null) {
            try {
                stdErrLogInputStream.close();
            } catch (IOException e) {
                getLog().trace("Failed to close stdErrLogInputStream", e);
            }
        }

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
