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

import java.util.List;

/**
* Created with IntelliJ IDEA.
* User: nick
* Date: 20/09/12
* Time: 22:22
* 
* A ChorusProcess using JDK 1.7 ProcessHandler
* Should be used when we have a 1.7+ runtime
* 
*/
public class ProcessBuilderProcess extends AbstractChorusProcess {

    private static ChorusLog log = ChorusLogFactory.getLog(ProcessBuilderProcess.class);

    public ProcessBuilderProcess(String name, List<String> command, ProcessLogOutput logOutput) throws Exception {
        super(name, logOutput);
        ProcessBuilder processBuilder = new ProcessBuilder(command);

        switch (logOutput.getStdOutMode()) {
            case FILE :
                log.debug("Will write process std out to file at " + logOutput.getStdOutLogFile());
                processBuilder.redirectOutput(
                    logOutput.isAppendToLogs() ? 
                        ProcessBuilder.Redirect.appendTo(logOutput.getStdOutLogFile()) :
                        ProcessBuilder.Redirect.to(logOutput.getStdOutLogFile())
                );
                break;
            case INLINE :
                processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
                break;
            case CAPTURED: 
                break;
            case CAPTUREDWITHLOG:
                break;
        }
        
        switch ( logOutput.getStdErrMode() ) {
            case FILE :
                log.debug("Will write process std err to file at " + logOutput.getStdErrLogFile());
                processBuilder.redirectError(
                    logOutput.isAppendToLogs() ?
                        ProcessBuilder.Redirect.appendTo(logOutput.getStdErrLogFile()) :
                        ProcessBuilder.Redirect.to(logOutput.getStdErrLogFile())
                );
                break;
            case INLINE :
                processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
                break;
            case CAPTURED:
                break;
            case CAPTUREDWITHLOG:
                break;
        }
        
        this.process = processBuilder.start();

        log.debug("Started process " + process + " with log output " + logOutput);
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
}
