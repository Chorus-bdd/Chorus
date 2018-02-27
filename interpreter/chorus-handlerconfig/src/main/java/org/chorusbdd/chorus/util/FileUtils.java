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
