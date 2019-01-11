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
package org.chorusbdd.chorus.util;

import org.chorusbdd.chorus.annotations.Scope;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.results.ScenarioToken;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * File utils for handlers
 */
public class FileUtils {

    public static String readScriptFile(ChorusLog log, String configName, String scriptPath, File script) {
        String scriptContents;
        if ( script.canRead()) {
            try {
                log.debug(format("Reading script at %s in browser %s", script.getAbsolutePath(), configName ));
                scriptContents = Files.lines(script.toPath()).collect(Collectors.joining());
            } catch (IOException e) {
                throw new ChorusException("Failed while reading script file", e);
            }
        } else {
            throw new ChorusException("Cannot find a script file [" + scriptPath + "] at [" + script.getAbsolutePath() + "], or it is not readable");
        }
        return scriptContents;
    }
}
