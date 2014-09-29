/**
 *  Copyright (C) 2000-2013 The Software Conservancy and Original Authors.
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
package org.chorusbdd.chorus.processes.processmanager;

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.util.JavaVersion;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 17/07/13
 * Time: 22:15
 * To change this template use File | Settings | File Templates.
 */
public class ChorusProcessFactory {

    private static ChorusLog log = ChorusLogFactory.getLog(ProcessBuilderProcess.class);

    public ChorusProcess createChorusProcess(String name, List<String> commandTokens, ProcessLogOutput logOutput) throws Exception {

        StringBuilder commandBuilder = new StringBuilder();
        for ( String s : commandTokens) {
            commandBuilder.append(s).append(" ");
        }
        commandBuilder.deleteCharAt(commandBuilder.length() - 1);
        
        String command = commandBuilder.toString();
        log.info(ChorusProcess.STARTING_PROCESS_LOG_PREFIX + command);

        if (JavaVersion.IS_1_7_OR_GREATER) {
            log.debug("Using ProcessBuilder to start the process since detected java runtime >= 1.7");
            return new ProcessBuilderProcess(name, commandTokens, logOutput);
        } else {
            log.debug("Using Runtime.exec() to start the process since detected java runtime < 1.7");
            return new Jdk15Process(name, commandTokens, logOutput);
        }
    }
}
