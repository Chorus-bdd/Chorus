/* Copyright (C) 2000-2011 The Software Conservancy as Trustee.
 * All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 *
 * Nothing in this notice shall be deemed to grant any rights to trademarks,
 * copyrights, patents, trade secrets or any other intellectual property of the
 * licensor or any contributor except as expressly stated herein. No patent
 * license is granted separate from the Software, for code that you delete from
 * the Software, or for combinations of the Software with other software or
 * hardware.
 */

package uk.co.smartkey.chorus.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by: Steve Neal
 * Date: 31/10/11
 */
public class CommandLineParser {

    /**
     * Creates a Map<flag names, values following that flag>
     *
     * @param args
     * @return
     */
    public static Map<String, List<String>> parseArgs(String[] args) {
        Map<String, List<String>> results = new HashMap<String, List<String>>();
        String lastFlag = null;
        List<String> lastFlagsValues = null;
        for (String arg : args) {
            if (arg.startsWith("-")) {
                lastFlag = arg.substring(1, arg.length());
                lastFlagsValues = results.get(lastFlag);
                if (lastFlagsValues == null) {
                    lastFlagsValues = new ArrayList<String>();
                    results.put(lastFlag, lastFlagsValues);
                }
            } else {
                List<String> values = results.get(lastFlag);
                values.add(arg);
            }
        }
        return results;
    }

}
