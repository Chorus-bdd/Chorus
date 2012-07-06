/**
 *  Copyright (C) 2000-2012 The Software Conservancy and Original Authors.
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

import org.chorusbdd.chorus.util.ChorusConstants;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 12/06/12
 * Time: 08:52
 *
 * All supported command line switches, argument counts and their System Property equivalents
 *
 * Each has a long switch name, a short switch name, min and max bounds for number of values,
 * and regular expression to validate values
 */
public enum InterpreterProperty {

    FEATURE_PATHS("-featurePaths", "-f", "chorusFeaturePaths", true, 1, Integer.MAX_VALUE, null, ".*", "c:\\my\\path or ..\\my\\path"),
    HANDLER_PACKAGES("-handlerPackages", "-h", "chorusHandlerPackages", false, 1, Integer.MAX_VALUE, ChorusConstants.ANY_PACKAGE, "[\\w\\.\\*]+", "my.package.name"),
    DRY_RUN("-dryrun", "-d", "chorusDryRun", false, 0, 1, new String[] {"false"}, "(?i)(false|true)", "(false|true)"),
    SHOW_SUMMARY("-showsummary", "-s", "chorusShowSummary", false, 0, 1, new String[] {"true"},  "(?i)(false|true)", "(false|true)"),
    TAG_EXPRESSION("-tagExpression", "-t", "chorusTagExpression", false, 1, Integer.MAX_VALUE, null, "\\w+", "MyTagName"),
    JMX_LISTENER("-jmxListener", "-j", "chorusJmxListener", false, 1, Integer.MAX_VALUE, null, "[\\w\\.]+:\\d{2,5}", "myhost.mydomain:1001"),
    SUITE_NAME("-suiteName", "-n", "chorusSuiteName", false, 1, Integer.MAX_VALUE, new String[] {"Test Suite"}, "[\\w\\s]+", "My Suite Name"),
    SHOW_ERRORS("-showErrors", "-e", "chorusShowErrors", false, 0, 1, new String[] {"false"},  "(?i)(false|true)",  "(false|true)"),
    LOG_LEVEL("-logLevel", "-l", "chorusLogLevel", false, 0, 1, new String[] {"warn"}, "(?i)(trace|debug|info|warn|error|fatal)", "(trace|debug|info|warn|error|fatal)");

    private String switchName;
    private String switchShortName;
    private String systemProperty;
    private boolean mandatory;              //if property must be defined, either user provided or defaulted
    private final int minValueCount;        //min number of values for this property if defined
    private final int maxValueCount;        //max number of values for this property if defined
    private String validatingExpression;    //regular expression to validate property values
    private String[] defaults;              //default values for this property if not defined by user
    private String example;                 //example property values to show to user

    private InterpreterProperty(String switchName,
                                String switchShortName,
                                String systemProperty,
                                boolean mandatory,
                                int minValues,
                                int maxValues,
                                String[] defaults, //be be null == 'not set'
                                String validatingExpression,
                                String example) {
        this.defaults = defaults;
        this.example = example;
        this.switchName = switchName.substring(1);
        this.switchShortName = switchShortName.substring(1);
        this.systemProperty = systemProperty;
        this.mandatory = mandatory;
        this.minValueCount = minValues;
        this.maxValueCount = maxValues;
        this.validatingExpression = validatingExpression;
    }


    /**
     * @return the InterpreterProperty for which either switchName or switchShortName matches flag
     */
    public static InterpreterProperty getProperty(String flag) {
        InterpreterProperty result = null;
        for (InterpreterProperty p : values()) {
            if ( p.getSwitchName().equals(flag) || p.getSwitchShortName().equals(flag)) {
                result = p;
                break;
            }
        }
        return result;
    }

    public String getSwitchName() {
        return switchName;
    }

    public String getSwitchShortName() {
        return switchShortName;
    }

    public String getHyphenatedSwitch() {
        return "-" + getSwitchName();
    }

    public String getSystemProperty() {
        return systemProperty;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public int getMinValueCount() {
        return minValueCount;
    }

    public int getMaxValueCount() {
        return maxValueCount;
    }

    public String toString() {
        return getSwitchName();
    }

    public String getValidatingExpression() {
        return validatingExpression;
    }

    public String getExample() {
        return example;
    }

    /**
     * @return default values for this property, or null if the property defaults to 'not set'
     */
    public String[] getDefaults() {
        return defaults;
    }
}
