/**
 *  Copyright (C) 2000-2012 The Software Conservancy as Trustee.
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
package org.chorusbdd.chorus.util.config;

import java.util.List;
import java.util.Map;

/**
 * Created by: Steve Neal
 * Date: 31/10/11
 *
 * This class is responsible for parsing command line switches into a Map of arguments by switch name
 */
public class CommandLineParser extends AbstractPropertySource {

    /**
     * Add to the provided propertyMap any properties available from this source
     *
     * Where the map already contains property values under a given key, extra property values should be
     * appended to the List

     * @return propertyMap, with parsed properties added
     */
    public Map<InterpreterProperty, List<String>> parseProperties(Map<InterpreterProperty, List<String>> propertyMap, String... args) throws InterpreterPropertyException {
        InterpreterProperty lastFlag = null;
        for (String arg : args) {
            lastFlag = processArgument(propertyMap, lastFlag, arg);
        }
        return propertyMap;
    }

    private InterpreterProperty processArgument(Map<InterpreterProperty, List<String>> results, InterpreterProperty lastProperty, String arg) throws InterpreterPropertyException {
        if (arg.startsWith("-")) {
            String flag = arg.substring(1, arg.length());
            lastProperty = InterpreterProperty.getProperty(flag);
            if (lastProperty == null ) {
                throw new InterpreterPropertyException("Unsupported parameter " + flag);
            }
            //we want to create an empty list, even if there are no subsequent values
            //since this is used to determine that the flag was present
            getOrCreatePropertyList(results, lastProperty);
        } else if ( lastProperty != null ) {
            //add value to value list for this property
            List<String> values = getOrCreatePropertyList(results, lastProperty);
            values.add(arg);
        } else {
            throw new InterpreterPropertyException("Unknown argument " + arg);
        }
        return lastProperty;
    }

}
