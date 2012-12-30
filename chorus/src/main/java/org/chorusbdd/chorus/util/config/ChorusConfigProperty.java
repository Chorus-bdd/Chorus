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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
public enum ChorusConfigProperty implements ConfigurationProperty {

    FEATURE_PATHS("-featurePaths", "-f", "chorusFeaturePaths", true, 1, Integer.MAX_VALUE, null, ".*", "-f c:\\my\\path ..\\my\\path  ..\\my\\path\\myfeature.feature",
    "Relative or absolute paths to the directories containing your feature files or paths to specific feature files. Directories will be searched recursively"),

    HANDLER_PACKAGES("-handlerPackages", "-h", "chorusHandlerPackages", false, 1, Integer.MAX_VALUE, ChorusConstants.ANY_PACKAGE, "[\\w\\.\\*]+", "-h my.package.name",
    "Handler package names to restrict the search for handler classes - this is optional but may speed up handler searching for larger projects"),

    DRY_RUN("-dryrun", "-d", "chorusDryRun", false, 0, 1, new String[] {"false"}, "(?i)(false|true)", "-d (false|true)",
    "Whether to actually execute steps or just detect and log the discovery of handlers and step definitions"),

    SHOW_SUMMARY("-showsummary", "-s", "chorusShowSummary", false, 0, 1, new String[] {"true"},  "(?i)(false|true)", "-s (false|true)",
    "Whether to show the closing summary of pass/fail information"),

    TAG_EXPRESSION("-tagExpression", "-t", "chorusTagExpression", false, 1, Integer.MAX_VALUE, null, "[\\w+@\\|!\\s]+", "-t @MyTagName",
    "One or more tags which can be used to restrict features which are executed"),

    JMX_LISTENER("-jmxListener", "-j", "chorusJmxListener", false, 1, Integer.MAX_VALUE, null, "[\\w\\.]+:\\d{2,5}", "-j myhost.mydomain:1001",
    "Network address of an agent which will receive execution events as the interpreter runs"),

    SUITE_NAME("-suiteName", "-n", "chorusSuiteName", false, 1, Integer.MAX_VALUE, new String[] {ChorusConstants.DEFAULT_SUITE_NAME}, "[\\w\\s]+", "-n My Suite Name",
    "Name for the test suite to be run"),

    SHOW_ERRORS("-showErrors", "-e", "chorusShowErrors", false, 0, 1, new String[] {"false"},  "(?i)(false|true)",  "-e (false|true)",
    "Whether stack traces should be shown in the interpreter output (rather than just a message) when step implementations throws exceptions"),

    LOG_LEVEL("-logLevel", "-l", "chorusLogLevel", false, 0, 1, new String[] {"warn"}, "(?i)(trace|debug|info|warn|error|fatal)", "-l (trace|debug|info|warn|error|fatal)",
    "The log level to be used by Chorus' built in log provider"),

    LOG_PROVIDER("-logProvider", "-p", "chorusLogProvider", false, 0, 1, null, "[\\w\\.]+", "-p org.chorusbdd.chorus.util.logging.StandardOutLogProvider",
    "The log provider class to be used to instantiate Chorus loggers");



    private String switchName;
    private String switchShortName;
    private String systemProperty;
    private boolean mandatory;              //if property must be defined, either user provided or defaulted
    private final int minValueCount;        //min number of values for this property if defined
    private final int maxValueCount;        //max number of values for this property if defined
    private String validatingExpression;    //regular expression to validate property values
    private String[] defaults;              //default values for this property if not defined by user
    private String example;                 //example property values to show to user
    private String description;

    private ChorusConfigProperty(String switchName,
                                 String switchShortName,
                                 String systemProperty,
                                 boolean mandatory,
                                 int minValues,
                                 int maxValues,
                                 String[] defaults, //be be null == 'not set'
                                 String validatingExpression,
                                 String example,
                                 String description) {
        this.defaults = defaults;
        this.example = example;
        this.description = description;
        this.switchName = switchName.substring(1);
        this.switchShortName = switchShortName.substring(1);
        this.systemProperty = systemProperty;
        this.mandatory = mandatory;
        this.minValueCount = minValues;
        this.maxValueCount = maxValues;
        this.validatingExpression = validatingExpression;
    }


    /**
     * @return the ConfigurationProperty for which either switchName or switchShortName matches flag
     */
    public boolean matchesSwitch(String s) {
        return getSwitchName().equals(s) || getSwitchShortName().equals(s);
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

    public String getDescription() {
        return description;
    }

    /**
     * @return default values for this property, or null if the property defaults to 'not set'
     */
    public String[] getDefaults() {
        return defaults;
    }

    public static ConfigurationProperty getConfigPropertyForSysProp(String systemProperty) {
        ConfigurationProperty result = null;
        for ( ConfigurationProperty p : values()) {
            if ( p.getSystemProperty().equals(systemProperty)) {
                result = p;
                break;
            }
        }
        return result;
    }

    public static List<ConfigurationProperty> getAll() {
        List<ConfigurationProperty> l = new ArrayList<ConfigurationProperty>();
        Collections.addAll(l, values());
        return l;
    }

    //useful for generating a table of chorus' input parameters in csv format
//    public static void main(String[] args) {
//        StringBuilder sb = new StringBuilder();
//        for ( ConfigurationProperty p : values() ) {
//            sb.append(p.getSwitchName()).append(",").
//               append("-").append(p.getSwitchShortName()).append(",").
//               append(p.getSystemProperty()).append(",").
//               append(p.isMandatory()).append(",").
//               append(p.getDefaults() != null ? p.getDefaults()[0] : "").append(",").
//               append(p.getExample()).append(",").
//               append(p.getDescription()).append("\n");
//        }
//        System.out.println(sb);
//    }

}
