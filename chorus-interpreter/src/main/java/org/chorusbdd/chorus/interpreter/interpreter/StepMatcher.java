/**
 *  Copyright (C) 2000-2014 The Software Conservancy and Original Authors.
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
package org.chorusbdd.chorus.interpreter.interpreter;

import org.chorusbdd.chorus.interpreter.invoker.StepInvoker;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.util.RegexpUtils;

import java.util.regex.Matcher;

/**
 * Created with IntelliJ IDEA.
 * User: GA2EBBU
 * Date: 11/09/14
 * Time: 17:54
 *
 */
public class StepMatcher {

    private static ChorusLog log = ChorusLogFactory.getLog(RegexpUtils.class);

    /**
     * TODO
     * Yuk, this is ugly, we're doing two things here, both matching the step and funding the types of the
     * parameters, this should be split into separate functions
     *
     * Extracts the groups in a regular expression into typed data
     *
     * @return an array of typed data or null if regex does not match the text, or the types are not compatible
     */
    public static Object[] extractGroupsAndCheckMethodParams(StepInvoker invoker, String stepText) {
        Matcher matcher = invoker.getStepPattern().matcher(stepText);
        if (matcher.matches()) {
            int groupCount = matcher.groupCount();

            //check that there are the same number of expected values as there are regex groups
            if (groupCount != invoker.getParameterTypes().length) {
                //I think this is always an error in the handler's step definition - group should always match param count
                //it's worth logging it to warn level, or people may spend hours looking and may not spot the problem
                log.warn("Number of method parameters does not match regex groups");
                return null;
            }

            //collect the regex group values
            String[] regexGroupValues = new String[groupCount];
            for (int i = 0; i < groupCount; i++) {
                regexGroupValues[i] = matcher.group(i + 1);
            }

            //convert the strings from the regex groups into an Object[]
            Object[] values = new Object[groupCount];
            for (int i = 0; i < groupCount; i++) {
                String valueStr = regexGroupValues[i];
                Class type = invoker.getParameterTypes()[i];
                Object coercedValue = TypeCoercion.coerceType(valueStr, type);
                if (("null".equals(valueStr) && coercedValue == null ) || coercedValue != null) {
                    values[i] = coercedValue;
                } else {
                    //the type coercion failed for this method parameter
                    //return null to indicate this reg exp / method is not a match
                    //log at info level that we found a match but could not perform the coercion  - this will not show
                    //at the default log level warn, but will show as soon as user increases it
                    //It seems valid to support a form of method parameter overloading here, where two methods have
                    //the same regex but different class types for their parameters, logging at warn by default might
                    //get irritating in this case
                    log.info("Matched step but could not coerce " + valueStr + " to type " + type);
                    values = null;
                    break;
                }
            }
            return values;

        } else {
            return null;
        }
    }
}
