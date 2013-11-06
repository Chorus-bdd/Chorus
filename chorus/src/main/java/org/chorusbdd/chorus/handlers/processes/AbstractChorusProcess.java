/**
 *  Copyright (C) 2000-2012 The Software Conservancy and Original Authors.
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
package org.chorusbdd.chorus.handlers.processes;

import org.chorusbdd.chorus.core.interpreter.ChorusContext;
import org.chorusbdd.chorus.util.assertion.ChorusAssert;
import org.chorusbdd.chorus.util.logging.ChorusLog;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: nick
 * Date: 24/07/13
 * Time: 08:55
 */
public abstract class AbstractChorusProcess implements ChorusProcess {

    //where we are managing writing process' output to log files ourselves (i.e. jdk 1.5 or CAPTUREANDLOG mode)
    //then these are the output streams to the log files for the process.
    protected FileOutputStream stdOutLogfileStream;
    protected BufferedWriter stdOutLogBufferedWriter;
    
    protected FileOutputStream stdErrLogfileStream;
    protected BufferedWriter stdErrLogBufferedWriter;
    
    //when we are in CAPTURED mode
    private InputStreamAndReader stdOutInputStreams;
    private InputStreamAndReader stdErrInputStreams;

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
        ChorusAssert.assertTrue("Process std out mode must be captured", OutputMode.isCaptured(logOutput.getStdOutMode())); 
        if ( stdOutInputStreams == null) {
            stdOutInputStreams = new InputStreamAndReader("Std Out");
            InputStream inputStream = process.getInputStream();
            stdOutInputStreams.createStreams(logOutput, inputStream);
        }
        
        if ( logOutput.getStdOutMode() == OutputMode.CAPTUREDWITHLOG && stdOutLogBufferedWriter == null) {
            createStdOutLogfileStream(logOutput);    
        }
        
        waitForOutputPattern(pattern, stdOutInputStreams, searchWithinLines, stdOutLogBufferedWriter);
    }

    public void waitForMatchInStdErr(String pattern, boolean searchWithinLines) {
        ChorusAssert.assertTrue("Process std err mode must be captured", OutputMode.isCaptured(logOutput.getStdErrMode()));
        if ( stdErrInputStreams == null) {
            stdErrInputStreams = new InputStreamAndReader("Std Err");
            InputStream inputStream = process.getErrorStream();
            stdErrInputStreams.createStreams(logOutput, inputStream);
        }

        if ( logOutput.getStdErrMode() == OutputMode.CAPTUREDWITHLOG && stdErrLogBufferedWriter == null) {
            createStdErrLogfileStream(logOutput);
        }
        
        waitForOutputPattern(pattern, stdErrInputStreams, searchWithinLines, stdErrLogBufferedWriter);
    }

    private void waitForOutputPattern(String pattern, InputStreamAndReader i, boolean searchWithinLines, Writer logStream) {
        Pattern p = Pattern.compile(pattern);
        long timeout = System.currentTimeMillis() + (logOutput.getReadTimeoutSeconds() * 1000);
        try {
            String matched = waitForPattern(timeout, i.reader, p, searchWithinLines, logStream);
            
            //store into the ChorusContext the exact string which matched the pattern so this can be used
            //in subsequent test steps
            ChorusContext.getContext().put("ProcessesHandler.match", matched);
        } catch (IOException e) {
            getLog().warn("Failed while matching pattern " + p, e);
            ChorusAssert.fail("Failed while matching pattern");
        }
    }

    //read ahead without blocking and attempt to match the pattern
    private String waitForPattern(long timeout, BufferedReader bufferedReader, Pattern pattern, boolean searchWithinLines, Writer logStream) throws IOException {
        boolean writeToLog = logStream != null;
        StringBuilder sb = new StringBuilder();
        String result = null;
        label:
        while(true) {
            while ( bufferedReader.ready() ) {
                int c = bufferedReader.read();
                if ( c == -1 ) {
                    ChorusAssert.fail("End of stream while waiting for match");
                }
                
                if (writeToLog) {
                    logStream.write(c);
                }
                
                if (c == '\n' || c == '\r' ) {
                    if ( sb.length() > 0) {
                        Matcher m = pattern.matcher(sb);
                        if ( m.matches() ) {
                            result = sb.toString();
                            break label;    
                        } else {
                            sb.setLength(0);
                        }
                    }
                } else {
                    sb.append((char)c);                    
                }

                if ( writeToLog) {
                    logStream.flush();
                }
            } 
            
            //nothing more to read, does the current output match the pattern?
            if ( searchWithinLines) {
                Matcher m = pattern.matcher(sb);
                if ( m.find() ) {
                    result = m.group(0);
                    break label;
                }
            }
            
            try {
                Thread.sleep(10); //avoid a busy loop since we are using nonblocking ready() / read()
            } catch (InterruptedException e) {}

            checkTimeout(timeout);

            if ( isStopped() && ! bufferedReader.ready()) {
                ChorusAssert.fail(
                    isExitCodeFailure() ? 
                        "Process stopped with error code " + getExitCode() + " while waiting for match" :
                        "Process stopped while waiting for match"
                );
            }
        }
        
        if ( writeToLog) {
            logStream.flush();
        }
        return result;
    }

    private void checkTimeout(long timeout) {
        if ( System.currentTimeMillis() > timeout ) {
            ChorusAssert.fail("Timed out after " + logOutput.getReadTimeoutSeconds() + " seconds");
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

    protected void createStdErrLogfileStream(ProcessLogOutput logOutput) {
        stdErrLogfileStream = createFileOutputStream(logOutput, logOutput.getStdErrLogFile());
        ChorusAssert.assertNotNull("Failed to create output stream to std error log at " + logOutput.getStdErrLogFile(), stdErrLogfileStream);
        stdErrLogBufferedWriter = new BufferedWriter(new OutputStreamWriter(stdErrLogfileStream));
    }

    protected void createStdOutLogfileStream(ProcessLogOutput logOutput) {
        stdOutLogfileStream = createFileOutputStream(logOutput, logOutput.getStdOutLogFile());
        ChorusAssert.assertNotNull("Failed to create output stream to std out log at " + logOutput.getStdOutLogFile(), stdOutLogfileStream);
        stdOutLogBufferedWriter = new BufferedWriter(new OutputStreamWriter(stdOutLogfileStream));
    }

    private FileOutputStream createFileOutputStream(ProcessLogOutput logOutput, File logFile) {
        FileOutputStream result = null;
        try {
            getLog().debug("Creating process log at " + logFile.getPath());
            result = new FileOutputStream(logFile, logOutput.isAppendToLogs());
        } catch (Exception e) {
            getLog().warn("Failed to create log file  " + logFile.getPath() + " will not write this log file");
        }
        return result;
    }

    protected void closeStreams() {
        
        if ( stdOutInputStreams != null) {
           stdOutInputStreams.close();
        }

        if ( stdErrInputStreams != null) {
          stdErrInputStreams.close();
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

        if ( stdOutLogfileStream != null) {
            try {
                getLog().trace("Closing stdout log file stream for process " + this);
                stdOutLogBufferedWriter.flush();
                stdOutLogBufferedWriter.close();
                stdErrLogfileStream = null;
                stdOutLogBufferedWriter = null;
            } catch (IOException e) {
                getLog().trace("Failed to flush and close stdout log file stream", e);
            }
        }

        if ( stdErrLogfileStream != null) {
            try {
                getLog().trace("Closing stderr log file stream for process " + this);
                stdErrLogBufferedWriter.flush();
                stdErrLogBufferedWriter.close();
                stdErrLogfileStream = null;
                stdErrLogfileStream = null;
            } catch (IOException e) {
                getLog().trace("Failed to flush and close stderr log file stream", e);
            }
        }
    }

    /**
     * Input stream and reader for process std out or std error
     */
    private class InputStreamAndReader {
        
        String name;
        BufferedInputStream inputStream;
        BufferedReader reader;

        private InputStreamAndReader(String name) {
            this.name = name;
        }

        void createStreams(ProcessLogOutput logOutput, InputStream processErrorStream) {
            int readAheadBuffer = logOutput.getReadAheadBufferSize();
            if ( readAheadBuffer > 0 ) {
                getLog().trace("Creating new read ahead buffered input stream " + name + " for process " + this + " in captured mode");
                inputStream = new ReadAheadBufferedStream(processErrorStream, 8192, logOutput.getReadAheadBufferSize()).startReadAhead();
            } else {
                getLog().trace("Creating new simple buffered input stream " + name + " for process " + this + " in captured mode");
                inputStream = new BufferedInputStream(processErrorStream);
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
        }

        void close() {
            if ( inputStream != null) {
                try {
                    getLog().trace("Closing input stream " + name + " for process " + this);
                    inputStream.close();
                } catch (IOException e) {}
            }
            
            if ( reader != null ) {
                try {
                    getLog().trace("Closing reader " + name + " for process " + this);
                    reader.close();
                } catch (IOException e) {}            
            }
        }
    }
}
