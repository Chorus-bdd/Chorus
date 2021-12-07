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
package org.chorusbdd.chorus.processes.manager.process;

import org.chorusbdd.chorus.processes.manager.config.ProcessManagerConfig;

import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 17/07/13
 * Time: 21:33
 * To change this template use File | Settings | File Templates.
 */
public interface ChorusProcess {

    boolean isStopped();

    void destroy();

    void waitFor() throws InterruptedException;

    boolean isExitWithFailureCode();

    int getExitCode();

    /**
     * Check the process has not terminated with a non-zero (error) code
     * This call will block until checkMillis milliseconds have elapsed
     *
     * @throws Exception if the process terminates with an error code within the delay period
     */
    void checkNoFailureWithin(int checkMillis) throws Exception;

    /**
     * Match the pattern in the process stdOut
     * @param searchWithinLines false to match pattern against whole lines, not within a line
     */
    void waitForMatchInStdOut(String pattern, boolean searchWithinLines, TimeUnit timeUnit, long length);

    void waitForMatchInStdErr(String pattern, boolean searchWithinLines, TimeUnit timeUnit, long length);

    void writeToStdIn(String line, boolean newLine);

    ProcessManagerConfig getConfiguration();
}
