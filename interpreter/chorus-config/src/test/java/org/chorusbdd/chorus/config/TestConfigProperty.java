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
package org.chorusbdd.chorus.config;

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
 * Config properties for testing
 */
public enum TestConfigProperty implements ExecutionProperty {

    FEATURE_PATHS("-featurePaths", "-f", "chorusFeaturePaths", true, 1, Integer.MAX_VALUE, null, ".*", "-f c:\\my\\path ..\\my\\path  ..\\my\\path\\myfeature.feature",
    "Relative or absolute paths to the directories containing your feature files or paths to specific feature files. Directories will be searched recursively", PropertySourceMode.OVERRIDE),

    HANDLER_PACKAGES("-handlerPackages", "-h", "chorusHandlerPackages", true, 1, Integer.MAX_VALUE, null, "[\\w\\.\\*]+", "-h my.package.name",
    "Handler package names to restrict search for handler classes, speeds up handler class searching and avoids unnecessary class scanning. Subpackages will also be scanned", PropertySourceMode.OVERRIDE),

    STEPMACRO_PATHS("-stepMacroPaths", "-m", "chorusStepMacroPaths", false, 0, Integer.MAX_VALUE, null, ".*", "-m c:\\my\\path ..\\my\\path  ..\\my\\path\\mymacros.stepmacro",
    "Relative or absolute paths to the directories containing your stepmacro files or paths to specific stepmacro files. If not specified featurePaths will be used", PropertySourceMode.OVERRIDE),

    DRY_RUN("-dryrun", "-d", "chorusDryRun", false, 0, 1, new String[] {"false"}, "(?i)(false|true)", "-d (false|true)",
    "Whether to actually execute steps or just detect and log the discovery of handlers and step definitions", PropertySourceMode.OVERRIDE),

    SHOW_SUMMARY("-showsummary", "-s", "chorusShowSummary", false, 0, 1, new String[] {"true"},  "(?i)(false|true)", "-s (false|true)",
    "Whether to show the closing summary of pass/fail information", PropertySourceMode.OVERRIDE),

    TAG_EXPRESSION("-tagExpression", "-t", "chorusTagExpression", false, 1, Integer.MAX_VALUE, null, "[\\w+@\\|!\\s]+", "-t @MyTagName",
    "One or more tags which can be used to restrict features which are executed", PropertySourceMode.OVERRIDE),

    JMX_LISTENER("-jmxListener", "-j", "chorusJmxListener", false, 1, Integer.MAX_VALUE, null, "[\\w\\.]+:\\d{2,5}", "-j myhost.mydomain:1001",
    "Network address of an agent which will receive execution events as the interpreter runs", PropertySourceMode.APPEND),

    SUITE_NAME("-suiteName", "-n", "chorusSuiteName", false, 1, Integer.MAX_VALUE, new String[] {ChorusConstants.DEFAULT_SUITE_NAME}, "[\\w\\s]+", "-n My Suite Name",
    "Name for the test suite to be run", PropertySourceMode.OVERRIDE),

    SHOW_ERRORS("-showErrors", "-e", "chorusShowErrors", false, 0, 1, new String[] {"false"},  "(?i)(false|true)",  "-e (false|true)",
    "Whether stack traces should be shown in the interpreter output (rather than just a message) when step implementations throws exceptions", PropertySourceMode.OVERRIDE),

    LOG_LEVEL("-logLevel", "-l", "chorusLogLevel", false, 0, 1, new String[] {"warn"}, "(?i)(trace|debug|info|warn|error|fatal)", "-l (trace|debug|info|warn|error|fatal)",
    "The log level to be used by Chorus' built in log provider", PropertySourceMode.OVERRIDE),

    LOG_PROVIDER("-logProvider", "-p", "chorusLogProvider", false, 0, 1, null, "[\\w\\.]+", "-p org.chorusbdd.chorus.logging.ChorusCommonsLogProvider",
    "The log provider class to be used to instantiate Chorus loggers", PropertySourceMode.OVERRIDE),

    SCENARIO_TIMEOUT("-scenarioTimeout", "-o", "chorusScenarioTimeout", false, 0, 1, new String[] {"360"}, "\\d{1,8}", "360",
    "Number of seconds after which a scenario will timeout", PropertySourceMode.OVERRIDE),

    EXECUTION_LISTENER("-executionListener", "-x", "chorusExecutionListener", false, 1, Integer.MAX_VALUE, null, "[\\w\\.]+", "com.mycom.MyListener", "One or more user specified ExecutionListener classes", PropertySourceMode.OVERRIDE),

    OUTPUT_FORMATTER("-outputWriter", "-w", "chorusOutputWriter", false, 0, 1, new String[] {"org.chorusbdd.chorus.output.PlainOutputWriter"}, "[\\w\\.]+", "-c org.myorg.MyWriter", "The output writer used to format the interpreter and log output for Chorus, if specified without a classname places Chorus in console mode", PropertySourceMode.OVERRIDE),

    CONSOLE_MODE("-console", "-c", "chorusConsoleMode", false, 0, 1, new String[] {"false"}, "(?i)(false|true)", "-c", "Enable chorus console mode which is best when displaying output in a console", PropertySourceMode.OVERRIDE);


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
    private PropertySourceMode propertySourceMode;

    private TestConfigProperty(String switchName,
                               String switchShortName,
                               String systemProperty,
                               boolean mandatory,
                               int minValues,
                               int maxValues,
                               String[] defaults, //be be null == 'not set'
                               String validatingExpression,
                               String example,
                               String description,
                               PropertySourceMode propertySourceMode) {
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
        this.propertySourceMode = propertySourceMode;
    }


    /**
     * @return the ExecutionProperty for which either switchName or switchShortName matches flag
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

    public PropertySourceMode getPropertySourceMode() {
        return propertySourceMode;
    }

    /**
     * @return default values for this property, or null if the property defaults to 'not set'
     */
    public String[] getDefaults() {
        return defaults == null ?  new String[0] : defaults;
    }

    public boolean hasDefaults() {
        return getDefaults().length > 0;
    }

    public static ExecutionProperty getConfigPropertyForSysProp(String systemProperty) {
        ExecutionProperty result = null;
        for ( ExecutionProperty p : values()) {
            if ( p.getSystemProperty().equals(systemProperty)) {
                result = p;
                break;
            }
        }
        return result;
    }

    public static List<ExecutionProperty> getAll() {
        List<ExecutionProperty> l = new ArrayList<>();
        Collections.addAll(l, values());
        return l;
    }

    //useful for generating a table of chorus' input parameters in csv format
//    public static void main(String[] args) {
//        StringBuilder sb = new StringBuilder();
//        for ( ExecutionProperty p : values() ) {
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
