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
package org.chorusbdd.chorus.util.handler;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Nick E on 07/01/2015.
 */
public class HandlerPatterns {

    public static final String namePermittedChars = "a-zA-Z0-9-_";

    /**
     * A single process name
     */
    public static final String namePattern = "([" + namePermittedChars +"]+)";

    /**
     * A comma separated list of process names processName1, processName2,  processName3
     */
    public static final String nameListPattern = "([" + namePermittedChars + ", ]+)";


    private static final Pattern nameWithAlias = Pattern.compile(namePattern + "\\s+" + "as" + "\\s+" + namePattern);

    /**
     * Get a Map of process name/alias to process config name
     * A config may be reused under multiple aliases
     *
     * @param nameList a list of process names conforming to the processNameListPattern
     */
    public static Map<String,String> getNamesWithAliases(String nameList) {
        String[] names = nameList.split(",");
        Map<String,String> results = new LinkedHashMap<>();  //retain ordering / determinism
        for ( String p : names) {
            String configName = p.trim();

            if ( configName.length() > 0) {
                Matcher matcher = nameWithAlias.matcher(configName);
                if (matcher.matches()) {
                    results.put(matcher.group(2), matcher.group(1));
                } else {
                    results.put(configName, configName);
                }
            }
        }
        return results;
    }

    /**
     * Get a List of process names from a comma separated list
     *
     * @param nameList a list of process names conforming to the processNameListPattern
     */
    public static List<String> getNames(String nameList) {
        String[] names = nameList.split(",");
        List<String> results = new LinkedList<>();
        for ( String p : names) {
            String configName = p.trim();
            if ( configName.length() > 0) {
                results.add(configName);
            }
        }
        return results;
    }
}
