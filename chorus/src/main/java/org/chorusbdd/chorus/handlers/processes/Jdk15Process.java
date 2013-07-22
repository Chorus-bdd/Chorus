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

import org.chorusbdd.chorus.util.assertion.ChorusAssert;
import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;
import org.chorusbdd.chorus.util.logging.ChorusOut;

import java.io.*;

/**
* Created with IntelliJ IDEA.
* User: nick
* Date: 20/09/12
* Time: 22:22
* To change this template use File | Settings | File Templates.
*/
public class Jdk15Process implements ChorusProcess {

    private static ChorusLog log = ChorusLogFactory.getLog(Jdk15Process.class);
    private FileOutputStream stdoutStream;
    private FileOutputStream stderrStream;

    private Process process;
    private ProcessRedirector outRedirector;
    private ProcessRedirector errRedirector;

    public Jdk15Process(String name, String command, ProcessLogOutput logOutput) throws Exception {
        this.process = Runtime.getRuntime().exec(command);

        InputStream processOutStream = process.getInputStream();
        InputStream processErrorStream = process.getErrorStream();
        log.debug("Started process " + process + " with out stream " + processOutStream + " and err stream " + processErrorStream);

        //if there are no log paths set, redirect the process output to appear inline with the chorus interpreter std out/err

        createOutputRedirectors(logOutput, processOutStream, processErrorStream);
        
        if ( outRedirector != null) {
            Thread outThread = new Thread(outRedirector, name + "-stdout");
            outThread.setDaemon(true);
            outThread.start();
        }
        
        if ( errRedirector != null ) {
            Thread errThread = new Thread(errRedirector, name + "-stderr");
            errThread.setDaemon(true);
            errThread.start();
        }
    }

    private void createOutputRedirectors(ProcessLogOutput logOutput, InputStream processOutStream, InputStream processErrorStream) {
        switch ( logOutput.getStdOutMode() ) {
            case FILE:
                boolean success = createFileStdOutStream(logOutput);
                ChorusAssert.assertTrue("Failed to create output stream to std out log at " + logOutput.getStdOutLogFile(), success);
                PrintStream out = new PrintStream(stdoutStream, true);
                this.outRedirector = new ProcessRedirector(processOutStream, true, out); 
                break;
            case INLINE:
                this.outRedirector = new ProcessRedirector(processOutStream, false, ChorusOut.out);
                break;
        }

        switch ( logOutput.getStdErrMode() ) {
            case FILE:
                boolean success = createFileStdErrStream(logOutput);
                ChorusAssert.assertTrue("Failed to create output stream to std err log at " + logOutput.getStdErrLogFile(), success);
                PrintStream err = new PrintStream(stderrStream, true);
                this.errRedirector = new ProcessRedirector(processErrorStream, true, err);
                break;
            case INLINE:
                this.errRedirector = new ProcessRedirector(processErrorStream, false, ChorusOut.err);
                break;
        }
    }

    private boolean createFileStdErrStream(ProcessLogOutput logOutput) {
        boolean result = false;
        File stdErrLogFile = logOutput.getStdErrLogFile();
        try {
            log.debug("Creating process log at " + stdErrLogFile.getPath());
            stderrStream = new FileOutputStream(stdErrLogFile, logOutput.isAppendToLogs());
            result = true;
        } catch (Exception e) {
            log.warn("Failed to create log file to error log file " + stdErrLogFile.getPath() + " will not write a log file");
        }
        return result;
    }

    private boolean createFileStdOutStream(ProcessLogOutput logOutput) {
        boolean result = false;
        File stdOutLogFile = logOutput.getStdOutLogFile();
        try {
            log.debug("Creating process log at " + stdOutLogFile.getPath());
            stdoutStream = new FileOutputStream(stdOutLogFile, logOutput.isAppendToLogs());
            result = true;
        } catch (Exception e) {
            log.warn("Failed to create log file to output log file " + stdOutLogFile.getPath() + " will not write a log file");
        }
        return result;
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

    public void waitFor() throws InterruptedException {
        process.waitFor();
    }


    public boolean isExitCodeFailure() {
        return process.exitValue() != 0;
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
                        log.debug("Process stopped during processCheckDelay period, exit code zero, passing step");
                        break;
                    }
                }

                if ( ! stopped ) {
                    log.debug("Process still running after processCheckDelay period, passing step");
                }
            }

        }
    }

    public void destroy() {
        // destroying the process will close its stdout/stderr and so cause our ProcessRedirector daemon threads to exit
        try {
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

    private void closeStreams() {
        if ( stdoutStream != null) {
            try {
                stdoutStream.flush();
                stdoutStream.close();
            } catch (IOException e) {
                log.trace("Failed to flush and close stdout log file stream", e);
            }
        }

        if ( stderrStream != null) {
            try {
                stderrStream.flush();
                stderrStream.close();
            } catch (IOException e) {
                log.trace("Failed to flush and close stderr log file stream", e);
            }
        }
    }

}
