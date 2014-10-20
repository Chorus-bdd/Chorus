package org.chorusbdd.chorus.processes.manager;

import java.util.concurrent.TimeUnit;

/**
 * Created by nick on 20/10/2014.
 */
public interface ProcessOutputPatternMatcher {
    String LAST_MATCH = "ProcessesHandler.match";

    void waitForMatch(String pattern, boolean searchWithinLines, TimeUnit timeUnit, long length);

    void close();
}
