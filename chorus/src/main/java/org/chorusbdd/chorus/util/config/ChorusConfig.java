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

import org.chorusbdd.chorus.util.ChorusOut;
import org.chorusbdd.chorus.util.DeepCopy;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: GA2EBBU
 * Date: 12/06/12
 * Time: 09:33
 *
 * Reads and validates interpreter configuration
 *
 * Configuration may be provided as switches/arguments to the interpreter process, or alternatively as System properties
 * Failing this, any defaults will apply
 *
 * The available parameters are specified in InterpreterProperty enumeration
 */
public class ChorusConfig implements DeepCopy<ChorusConfig> {

    private String[] args;
    private Map<InterpreterProperty, List<String>> propertyMap = new HashMap<InterpreterProperty, List<String>>();

    //ordered list of property sources
    private PropertySource[] propertySources = new PropertySource[] {
        new CommandLineParser(),
        new SystemPropertyParser(),
        new DefaultsPropertySource()
    };

    /**
     * Create a configuration using System Properties only
     */
    public ChorusConfig() {
        this(new String[0]);
    }

    /**
     * Create a configuration using process arguments and System Properties
     */
    public ChorusConfig(String[] args) {
        this.args = args;
    }

    public ChorusConfig readConfiguration() throws InterpreterPropertyException {
        for ( PropertySource s : propertySources) {
            s.parseProperties(propertyMap, args);
        }

        validateProperties(propertyMap);
        return this;
    }

    public void setProperty(InterpreterProperty property, List<String> values) {
        propertyMap.put(property, values);
    }

    public List<String> getValues(InterpreterProperty property) {
        return propertyMap.get(property);
    }

    /**
     * @return The value for a property which should always a single value
     */
    public String getSingleValue(InterpreterProperty property) {
        List<String> values = propertyMap.get(property);
        if ( values == null || values.size() != 1) {
            throw new RuntimeException("Property " + property + " did not have a single value, instead had the values " + values);
        }
        return values.get(0);
    }

    public boolean isSet(InterpreterProperty property) {
        return propertyMap.containsKey(property);
    }

    /**
     * for boolean properties, is the property set true
     */
    public boolean isTrue(InterpreterProperty property) {
        return isSet(property) && propertyMap.get(property).size() == 1
            && "true".equalsIgnoreCase(propertyMap.get(property).get(0));
    }

    private void validateProperties(Map<InterpreterProperty, List<String>> results) throws InterpreterPropertyException {
        for ( InterpreterProperty p : InterpreterProperty.values()) {
            checkIfMandatory(results, p);

            if ( results.containsKey(p)) {
                List<String> values = results.get(p);
                checkValueCount(p, values);
                checkValues(p, values);
            }
        }

    }

    private void checkValues(InterpreterProperty p, List<String> values) throws InterpreterPropertyException {
        Pattern pattern = Pattern.compile(p.getValidatingExpression());
        for (String value : values) {
            Matcher m = pattern.matcher(value);
            if ( ! m.matches()) {
                throw new InterpreterPropertyException(
                    "Could not parse the value for interpreter property " + p +
                    " expected to be in the form " + p.getExample()
                );
            }
        }
    }

    private void checkValueCount(InterpreterProperty p, List<String> values) throws InterpreterPropertyException {
        if ( values.size() < p.getMinValueCount()) {
            throw new InterpreterPropertyException("At least " + p.getMinValueCount() + " value(s) must be supplied for the property " + p);
        } else if ( values.size() > p.getMaxValueCount()) {
            throw new InterpreterPropertyException("At most " + p.getMaxValueCount() + " value(s) must be supplied for the property " + p);
        }
    }

    private void checkIfMandatory(Map<InterpreterProperty, List<String>> results, InterpreterProperty p) throws InterpreterPropertyException {
        if ( p.isMandatory() && ! results.containsKey(p)) {
            throw new InterpreterPropertyException(
                "Mandatory property " + p + " was not set. " +
                "You can set this property with the -" + p.getSwitchName() + " switch, " +
                "the -" + p.getSwitchShortName() + " switch or the " +
                p.getSystemProperty() + " system property"
            );
        }
    }

    public static void logHelp() {
        ChorusOut.err.println("Usage: Main -f [feature_dirs | feature_files] -h [handler base packages] [-name Test Suite Name] [-t tag_expression] [-jmxListener host:port] [-showErrors] [-dryrun] [-showsummary] ");
    }

    //to get the suite name we concatenate all the values provided for suite name switch
    public String getSuiteName() {
        return isSet(InterpreterProperty.SUITE_NAME) ?
            concatenateName(getValues(InterpreterProperty.SUITE_NAME)) :
            "";
    }

    private String concatenateName(List<String> name) {
        StringBuilder sb = new StringBuilder();
        if ( name.size() > 0 ) {
            Iterator<String> i = name.iterator();
            sb.append(i.next());
            while (i.hasNext()) {
                sb.append(" ");
                sb.append(i.next());
            }
        }
        return sb.toString();
    }

    public ChorusConfig deepCopy() {
        ChorusConfig c = new ChorusConfig();
        c.args = args;
        c.propertyMap = new HashMap<InterpreterProperty, List<String>>();
        for ( Map.Entry<InterpreterProperty, List<String>> e : propertyMap.entrySet()) {
            List<String> cloneList = new ArrayList<String>();
            cloneList.addAll(e.getValue());
            c.propertyMap.put(e.getKey(), cloneList);
        }
        return c;
    }

}
