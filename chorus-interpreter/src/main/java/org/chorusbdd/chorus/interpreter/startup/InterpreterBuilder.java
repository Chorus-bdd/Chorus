/**
 *  Copyright (C) 2000-2015 The Software Conservancy and Original Authors.
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
package org.chorusbdd.chorus.interpreter.startup;

import org.chorusbdd.chorus.config.ConfigProperties;
import org.chorusbdd.chorus.executionlistener.ExecutionListenerSupport;
import org.chorusbdd.chorus.interpreter.interpreter.ChorusInterpreter;
import org.chorusbdd.chorus.interpreter.subsystem.SubsystemManager;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 24/02/13
 * Time: 16:16
 *
 * Build and configurea a ChorusInterpreter
 */
public class InterpreterBuilder {

    private ChorusLog log = ChorusLogFactory.getLog(InterpreterBuilder.class);

    private ExecutionListenerSupport listenerSupport;
    
    public InterpreterBuilder(ExecutionListenerSupport listenerSupport) {
        this.listenerSupport = listenerSupport;
    }

    /**
     * Run the interpreter, collating results into the executionToken
     */
    public ChorusInterpreter buildAndConfigure(ConfigProperties config, SubsystemManager subsystemManager) {
        ChorusInterpreter chorusInterpreter = new ChorusInterpreter(listenerSupport);
        chorusInterpreter.setBasePackages(config.getValues(ChorusConfigProperty.HANDLER_PACKAGES));
        chorusInterpreter.setScenarioTimeoutMillis(Integer.valueOf(config.getValue(ChorusConfigProperty.SCENARIO_TIMEOUT)) * 1000);
        chorusInterpreter.setDryRun(config.isTrue(ChorusConfigProperty.DRY_RUN));
        chorusInterpreter.setSubsystemManager(subsystemManager);
        return chorusInterpreter;
    }
}
