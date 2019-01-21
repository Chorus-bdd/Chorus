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
package org.chorusbdd.chorus.remoting.manager;

import org.chorusbdd.chorus.annotations.SubsystemConfig;
import org.chorusbdd.chorus.handlerconfig.ConfigPropertySource;
import org.chorusbdd.chorus.handlerconfig.ConfigurableManager;
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
@SubsystemConfig(
    id = "remotingManager", 
    implementationClass = "org.chorusbdd.chorus.remoting.ProtocolAwareRemotingManager",
    overrideImplementationClassSystemProperty = "chorusRemotingManager")
public interface RemotingManager extends Subsystem, StepInvokerProvider {

    void connect(String configName, Properties remotingProperties);

    List<StepInvoker> getStepInvokers();

    void closeAllConnections();
}
