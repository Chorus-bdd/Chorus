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
package org.chorusbdd.chorus.remoting.manager;

import org.chorusbdd.chorus.stepinvoker.StepInvoker;
import org.chorusbdd.chorus.stepinvoker.StepInvokerProvider;
import org.chorusbdd.chorus.subsystem.Subsystem;

import java.util.List;
import java.util.Properties;

/**
 * Created by nick on 30/08/2014.
 * 
 * A RemotingManager implements the remoting/network handling for a remoting protocol supported by the Chorus 
 * interpreter
 * 
 * A new instance of the RemotingManager for each supported protocol is created at the start of each scenario 
 * which uses RemotingHandler
 */
public interface RemotingManager extends Subsystem, StepInvokerProvider {

    /**
     * Find a step method in the remote component which matches the 'action' String
     *
     * @param configName
     * @param remotingConfig
     * @param action            - the step text from the scenario which we want to match to a remote step
     * @return                    the value returned by the remote component when invoking the remote step implementation
     * @throws org.chorusbdd.chorus.util.ChorusException if executing the step fails
     **/
    Object performActionInRemoteComponent(String configName, Properties remotingConfig, String action);

    public void connect(String configName, Properties remotingProperties);

    List<StepInvoker> getStepInvokers();

    void closeAllConnections();
}
