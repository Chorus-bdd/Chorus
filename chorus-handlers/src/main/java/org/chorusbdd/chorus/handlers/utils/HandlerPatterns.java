package org.chorusbdd.chorus.handlers.utils;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by GA2EBBU on 07/01/2015.
 */
public class HandlerPatterns {

    public static final String processNamePermittedChars = "a-zA-Z0-9-_";

    /**
     * A single process name
     */
    public static final String processNamePattern = "([" + processNamePermittedChars +"]+)";

    /**
     * A comma separated list of process names processName1, processName2,  processName3
     */
    public static final String processNameListPattern = "([" + processNamePermittedChars + ", ]+)";

    /**
     * Get a List of process names from a comma separated list
     *
     * @param processNameList a list of process names conforming to the processNameListPattern
     */
    public static List<String> getProcessNames(String processNameList) {
        String[] processNames = processNameList.split(",");
        List<String> results = new LinkedList<String>();
        for ( String p : processNames) {
            String processConfigName = p.trim();
            if ( processConfigName.length() > 0) {
                results.add(processConfigName);
            }
        }
        return results;
    }
}
