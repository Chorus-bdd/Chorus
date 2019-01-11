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

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.processes.manager.commandlinebuilder.AbstractCommandLineBuilder;
import org.chorusbdd.chorus.processes.manager.commandlinebuilder.JavaProcessCommandLineBuilder;
import org.chorusbdd.chorus.processes.manager.commandlinebuilder.NativeProcessCommandLineBuilder;
import org.chorusbdd.chorus.processes.manager.process.ChorusProcess;
import org.chorusbdd.chorus.processes.manager.process.NamedProcess;
import org.chorusbdd.chorus.results.FeatureToken;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 17/07/13
 * Time: 22:15
 * To change this template use File | Settings | File Templates.
 */
public class ChorusProcessFactory {

    private ChorusLog log = ChorusLogFactory.getLog(ChorusProcessFactory.class);

    public ChorusProcess startChorusProcess(NamedProcess namedProcess, FeatureToken featureToken) throws Exception {

        String name = namedProcess.getProcessName();

        //work out where the process std out and err should go
        ProcessOutputConfiguration outputConfig = new ProcessOutputConfiguration(featureToken, namedProcess);

        List<String> commandLineTokens = buildCommandLine(namedProcess, featureToken, outputConfig.getLogFileBaseName());

        ProcessManagerProcess processManagerProcess = new ProcessManagerProcess(namedProcess, commandLineTokens, outputConfig);
        processManagerProcess.start();

        return processManagerProcess;
    }

    private List<String> buildCommandLine(NamedProcess namedProcess, FeatureToken featureToken, String logFileBaseName) {
        File featureDir = featureToken.getFeatureDir();
        AbstractCommandLineBuilder b = namedProcess.isJavaProcess() ?
                new JavaProcessCommandLineBuilder(featureDir, namedProcess, logFileBaseName) :
                new NativeProcessCommandLineBuilder(namedProcess, featureDir);

        List<String> commandTokens = b.buildCommandLine();
        logCommandLine(namedProcess, commandTokens);
        return commandTokens;
    }

    private void logCommandLine(NamedProcess namedProcess, List<String> commandTokens) {
        String command = commandTokens.stream().collect(Collectors.joining(" "));
        log.info("About to run process: " + namedProcess.getProcessName());
        if (log.isDebugEnabled()) {
            log.debug("Process config name " + namedProcess.getConfigName());
            log.debug("Command line: " + command);
        }
    }
}
