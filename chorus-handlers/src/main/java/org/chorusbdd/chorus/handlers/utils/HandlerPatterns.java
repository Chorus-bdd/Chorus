/**
 *  Copyright (C) 2000-2015 The Software Conservancy and Original Authors.
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
package org.chorusbdd.chorus.handlers.utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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


    private static final Pattern processWithAlias = Pattern.compile(processNamePattern + "\\s+" + "as" + "\\s+" + processNamePattern);

    /**
     * Get a Map of process name/alias to process config name
     * A config may be reused under multiple aliases
     *
     * @param processNameList a list of process names conforming to the processNameListPattern
     */
    public static Map<String,String> getProcessNamesWithAliases(String processNameList) {
        String[] processNames = processNameList.split(",");
        Map<String,String> results = new HashMap<>();
        for ( String p : processNames) {
            String processConfigName = p.trim();

            if ( processConfigName.length() > 0) {
                Matcher matcher = processWithAlias.matcher(processConfigName);
                if (matcher.matches()) {
                    results.put(matcher.group(2), matcher.group(1));
                } else {
                    results.put(processConfigName, processConfigName);
                }
            }
        }
        return results;
    }

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
