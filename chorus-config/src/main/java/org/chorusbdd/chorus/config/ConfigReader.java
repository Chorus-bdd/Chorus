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
package org.chorusbdd.chorus.config;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 12/06/12
 * Time: 09:33
 *
 * Reads and validates configuration specified either as system properties, command line switches or defaults
 *
 * Takes a collection of ConfigurationProperty which define the options and possible values - in the case of
 * Chorus itself these settings are defined in ChorusConfigurationProperty enum
 *
 * Configuration may be provided as switches/arguments to the process, or alternatively as System properties
 * Failing this, any defaults will apply
 *
 * Where a property is set in multiple sources, behaviour will depend on the PropertySourceMode for the property
 * Where APPEND, values from each source are combined to produce a merged list
 * Where OVERRIDE, the source with the highest priority (the last source which provided a value) will determine the value
 */
public class ConfigReader implements ConfigProperties {

    //there's a chicken and egg problem which means we can't use ChorusLog here since the config properties themselves
    //are used to set the log implementation in use
    
    private List<ConfigurationProperty> properties;
    private String[] args;

    //the properties provided be each source
    private Map<ConfigSource, Map<ConfigurationProperty, List<String>>> sourceToPropertiesMap = new HashMap<ConfigSource, Map<ConfigurationProperty, List<String>>>();

    //the merged set of properties, after PropertySourceMode is applied
    private Map<ConfigurationProperty, List<String>> propertyMap = new HashMap<ConfigurationProperty, List<String>>();

    //ordered list of property sources
    private ConfigSource[] propertySources;

    /**
     * Create a configuration using System Properties and defaults only
     */
    public ConfigReader(List<ConfigurationProperty> properties) {
        this(properties, new String[0]);
    }

    /**
     * Create a configuration using process arguments, System Properties and defaults
     */
    public ConfigReader(List<ConfigurationProperty> properties, String[] args) {
        this.properties = properties;
        this.args = args;

        //where a property is in PropertySourceMode.OVERRIDE the ordering of sources here is important
        //sys props are last since it is useful to be able to use a sys prop to override command line
        //parameters for test suites which run as part of a component build which is checked in to source control -
        //otherwise it is necessary to commit changes to files to achieve something simple like increasing logging
        //to debug level. Continuous integration tools such as team city let you set a sys prop easily to do this.
        propertySources = new ConfigSource[] {
            new DefaultsConfigSource(properties),
            new CommandLineParser(properties),
            new SystemPropertyParser(properties)
        };
    }

    public ConfigReader readConfiguration() throws InterpreterPropertyException {
        for ( ConfigSource s : propertySources) {
            Map<ConfigurationProperty, List<String>> propertyMap = new HashMap<ConfigurationProperty, List<String>>();
            propertyMap = s.parseProperties(propertyMap, args);
            sourceToPropertiesMap.put(s, propertyMap);
        }

        mergeProperties(sourceToPropertiesMap);
        validateProperties(propertyMap);
        return this;
    }

    //deermine the final set of properties according to PropertySourceMode for each property
    private void mergeProperties(Map<ConfigSource, Map<ConfigurationProperty, List<String>>> sourceToPropertiesMap) {
        for ( ConfigSource s : propertySources) {
            Map<ConfigurationProperty, List<String>> properties = sourceToPropertiesMap.get(s);
            for ( ConfigurationProperty p : properties.keySet()) {
                List<String> valuesFromSource = properties.get(p);
                if ( valuesFromSource != null && valuesFromSource.size() > 0 ) {
                    List<String> vals = getOrCreatePropertyValues(p);
                    mergeValues(valuesFromSource, p, vals);
                }
            }
        }
    }

    private void mergeValues(List<String> valuesFromSource, ConfigurationProperty p, List<String> vals) {
        switch(p.getPropertySourceMode()) {
            case APPEND:
                vals.addAll(valuesFromSource);
                break;
            case OVERRIDE:
                vals.clear();
                vals.addAll(valuesFromSource);
                break;
            default:
                throw new UnsupportedOperationException("Unknown source mode " + p.getPropertySourceMode());
        }
    }

    private List<String> getOrCreatePropertyValues(ConfigurationProperty p) {
        List<String> vals = propertyMap.get(p);
        if ( vals == null) {
            vals = new LinkedList<String>();
            propertyMap.put(p, vals);
        }
        return vals;
    }

    public void setProperty(ConfigurationProperty property, List<String> values) {
        propertyMap.put(property, values);
    }

    public List<String> getValues(ConfigurationProperty property) {
        return propertyMap.containsKey(property) ? propertyMap.get(property) : Collections.<String>emptyList();
    }

    /**
     * @return Single value for a property
     */
    public String getValue(ConfigurationProperty property) {
        List<String> values = propertyMap.get(property);
        return values != null ? values.get(0) : null;
    }

    public boolean isSet(ConfigurationProperty property) {
        return propertyMap.containsKey(property);
    }

    /**
     * for boolean properties, is the property set true
     */
    public boolean isTrue(ConfigurationProperty property) {
        return isSet(property) && propertyMap.get(property).size() == 1
            && "true".equalsIgnoreCase(propertyMap.get(property).get(0));
    }

    private void validateProperties(Map<ConfigurationProperty, List<String>> results) throws InterpreterPropertyException {
        for ( ConfigurationProperty p : properties) {
            checkIfMandatory(results, p);

            if ( results.containsKey(p)) {
                List<String> values = results.get(p);
                checkValueCount(p, values);
                checkValues(p, values);
            }
        }

    }

    private void checkValues(ConfigurationProperty p, List<String> values) throws InterpreterPropertyException {
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

    private void checkValueCount(ConfigurationProperty p, List<String> values) throws InterpreterPropertyException {
        if ( values.size() < p.getMinValueCount()) {
            throw new InterpreterPropertyException("At least " + p.getMinValueCount() + " value(s) must be supplied for the property " + p);
        } else if ( values.size() > p.getMaxValueCount()) {
            throw new InterpreterPropertyException("At most " + p.getMaxValueCount() + " value(s) must be supplied for the property " + p);
        }
    }

    private void checkIfMandatory(Map<ConfigurationProperty, List<String>> results, ConfigurationProperty p) throws InterpreterPropertyException {
        if ( p.isMandatory() && ! results.containsKey(p)) {
            throw new InterpreterPropertyException(
                "Mandatory property " + p + " was not set. " +
                "You can set this property with the -" + p.getSwitchName() + " switch, " +
                "the -" + p.getSwitchShortName() + " switch or the " +
                p.getSystemProperty() + " system property"
            );
        }
    }
}
