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

import org.chorusbdd.chorus.util.logging.ChorusOut;
import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

/**
* Created with IntelliJ IDEA.
* User: nick
* Date: 20/09/12
* Time: 22:22
* To change this template use File | Settings | File Templates.
*/
public class ProcessHandlerProcess {

    private static ChorusLog log = ChorusLogFactory.getLog(ProcessHandlerProcess.class);

    private Process process;
    private ProcessRedirector outRedirector;
    private ProcessRedirector errRedirector;

    public ProcessHandlerProcess(String name, String command, String stdoutLogPath, String stderrLogPath) throws Exception {
        this.process = Runtime.getRuntime().exec(command);

        InputStream processOutStream = process.getInputStream();
        InputStream processErrorStream = process.getErrorStream();
        log.debug("Started process " + process + " with out stream " + processOutStream + " and err stream " + processErrorStream);

        //if there are no log paths set, redirect the process output to appear inline with the chorus interpreter std out/err
        if (null == stdoutLogPath) {
            this.outRedirector = new ProcessRedirector(processOutStream, false, ChorusOut.out);
        } else {
            PrintStream out = new PrintStream(new FileOutputStream(stdoutLogPath), true);
            this.outRedirector = new ProcessRedirector(processOutStream, true, out);
        }
        if (null == stderrLogPath) {
            this.errRedirector = new ProcessRedirector(processErrorStream, false, ChorusOut.err);
        } else {
            PrintStream err = new PrintStream(new FileOutputStream(stderrLogPath), true);
            this.errRedirector = new ProcessRedirector(processErrorStream, true, err);
        }

        Thread outThread = new Thread(outRedirector, name + "-stdout");
        outThread.setDaemon(true);
        outThread.start();
        Thread errThread = new Thread(errRedirector, name + "-stderr");
        errThread.setDaemon(true);
        errThread.start();
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
        // destroying the process will close its stdout/stderr and so cause our ProcessRedirector daemon threads to exit
        log.debug("Destroying process " + process);
        process.destroy();
        try {
            //this ensures that all of the processes resources are cleaned up before proceeding
            process.waitFor();
        } catch (InterruptedException e) {
            log.error("Interrupted while waiting for process to terminate",e);
        }
    }

    public void waitFor() throws InterruptedException {
        process.waitFor();
    }


}
