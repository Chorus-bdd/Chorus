package org.chorusbdd.chorus.processes.manager.patternmatching;

import org.chorusbdd.chorus.processes.manager.process.ChorusProcess;
import org.chorusbdd.chorus.processes.manager.config.LogFileAndMode;
import org.chorusbdd.chorus.processes.manager.config.OutputMode;

/**
 * Created by nick on 10/11/14.
 */
public class PatternMatcherFactory {

    public ProcessOutputPatternMatcher createPatternMatcher(ChorusProcess chorusProcess, LogFileAndMode logFileAndMode) {
        return OutputMode.isWriteToLogFile(logFileAndMode.getMode()) ?
                new TailLogPatternMatcher(chorusProcess, logFileAndMode.getFile()) :
                new WarnOnMatchPatternMatcher(logFileAndMode.getStreamDescription());
    }
}
