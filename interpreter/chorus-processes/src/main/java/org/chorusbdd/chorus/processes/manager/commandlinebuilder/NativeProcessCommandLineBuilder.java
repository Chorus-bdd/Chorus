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
package org.chorusbdd.chorus.processes.manager.commandlinebuilder;

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.processes.manager.process.NamedProcess;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 21/07/13
 * Time: 12:29
 * To change this template use File | Settings | File Templates.
 */
public class NativeProcessCommandLineBuilder extends AbstractCommandLineBuilder {

    private ChorusLog log = ChorusLogFactory.getLog(NativeProcessCommandLineBuilder.class);

    private NamedProcess namedProcess;
    private File featureDir;

    public NativeProcessCommandLineBuilder(NamedProcess namedProcess, File featureDir) {
        this.namedProcess = namedProcess;
        this.featureDir = featureDir;
    }
    
    @Override
    public List<String> buildCommandLine() {
        String executableToken = getExecutableToken(namedProcess);
        List<String> argsTokens = getSpaceSeparatedTokens(namedProcess.getArgs());

        List<String> commandLineTokens = new ArrayList<>();
        commandLineTokens.add(executableToken);
        commandLineTokens.addAll(argsTokens);
        return commandLineTokens;
    }

    private String getExecutableToken(NamedProcess processesConfig) {
        String executableTxt = processesConfig.getPathToExecutable();
        executableTxt = getPathToExecutable(featureDir, executableTxt);
        return executableTxt;
    }
    
    public static String getPathToExecutable(File featureDir, String path) {
        File scriptPath = new File(path);
        if ( ! scriptPath.isAbsolute() ) {
            scriptPath = new File(featureDir, path);
        }
        return scriptPath.getAbsolutePath();
    }
    
}
