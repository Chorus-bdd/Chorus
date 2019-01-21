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
package org.chorusbdd.chorus.processes.manager;

import org.chorusbdd.chorus.annotations.Scope;
import org.chorusbdd.chorus.annotations.SubsystemConfig;
import org.chorusbdd.chorus.handlerconfig.ConfigPropertySource;
import org.chorusbdd.chorus.subsystem.Subsystem;

import java.util.Properties;

/**
 * Created by nick on 26/09/2014.
 */
@SubsystemConfig(
    id = "processManager", 
    implementationClass = "org.chorusbdd.chorus.processes.manager.ProcessManagerImpl",
    overrideImplementationClassSystemProperty = "chorusProcessManager")
public interface ProcessManager extends Subsystem {

    void startProcess(String configName, String processName, Properties processProperties) throws Exception;

    void stopProcess(String processName);

    /**
     * Stop all processes which are configured to run in the scope s
     */
    void stopProcesses(Scope s);

    void stopAllProcesses();

    /**
     * Get a Properties object representing the configuration of a process
     * This is a copy of the config in use, changing it will have no impact on the process
     *
     * @return a Properties object or null if the process manager is not managing processName
     */
    Properties getProcessProperties(String processName);

    void checkProcessHasStopped(String processName);

    void checkProcessIsRunning(String processName);

    void checkProcessIsNotRunning(String processName);

    void waitForProcessToTerminate(String processName);

    void readFromProcess(String pattern, String processName, boolean searchWithinLines);

    void readFromProcessWithinNSeconds(String pattern, String processName, boolean searchWithinLines, int seconds);

    void readFromProcessStdError(String pattern, String processName, boolean searchWithinLines);

    void readFromProcessStdErrorWithinNSeconds(String pattern, String processName, boolean searchWithinLines, int seconds);

    void writeToProcess(String line, String processName, boolean newLine);

    void waitForProcessToTerminate(String processName, int waitTimeSeconds);

    int getNumberOfInstancesStarted(String configName);
}
