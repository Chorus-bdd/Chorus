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

import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by: Steve Neal
 * Date: 31/10/11
 *
 * This class is responsible for parsing command line switches into a Map of arguments by switch name
 */
public class CommandLineParser extends AbstractConfigSource {

    public CommandLineParser(List<ExecutionProperty> properties) {
        super(properties);
    }

    /**
     * Add to the provided propertyMap any properties available from this source
     *
     * Where the map already contains property values under a given key, extra property values should be
     * appended to the List

     * @return propertyMap, with parsed properties added
     */
    public Map<ExecutionProperty, List<String>> parseProperties(Map<ExecutionProperty, List<String>> propertyMap, String... args) throws InterpreterPropertyException {

        //if some command line switches were specified
        if ( args.length > 0 ) {
            //easiest to build the args back up into a single string then split by -
            StringBuilder allArgs = new StringBuilder();
            for (String s : args) {
                allArgs.append(s).append(" ");
            }

            String allargs = allArgs.toString();
            if ( allargs.trim().length() > 0 && ! allargs.startsWith("-")) {
                throw new InterpreterPropertyException("arguments must start with a switch, e.g. -f");
            }

            //having checked the first char is a -, we now have to strip it
            //otherwise end up with a first "" token following the split - not sure that's correct behaviour from split
            allargs = allargs.substring(1);
            //hyphens may exist within paths so only split by those which have preceding empty space
            String[] splitParameterList = allargs.split(" -");

            for ( String parameterList : splitParameterList) {

                //tokenize, first token will be the property name, rest will be the values
                StringTokenizer st = new StringTokenizer(parameterList, " ");

                //find the property
                ExecutionProperty property = getProperty(parameterList, st);

                //add its values
                addPropertyValues(propertyMap, st, property);
            }
        }
        return propertyMap;
    }

    private void addPropertyValues(Map<ExecutionProperty, List<String>> propertyMap, StringTokenizer st, ExecutionProperty property) throws InterpreterPropertyException {
        List<String> l = getOrCreatePropertyList(propertyMap, property);
        if ( ! st.hasMoreTokens() ) {
            if (isBooleanSwitchProperty(property)) {
                l.add("true");
            } else {
                throw new InterpreterPropertyException(
                    String.format("No value was given for switch -%s (-%s), and Chorus cannot provide a default",
                        property.getSwitchShortName(),
                        property.getSwitchName()
                    )
                );
            }
        } else {
            while(st.hasMoreTokens()) {
                l.add(st.nextToken());
            }
        }
    }

    /**
     * This is a property with a boolean value which defaults to false
     *
     * The user can 'turn this option on' by passing a command line switch without a value (e.g. -d ) in which case we need to
     * set the value to true
     *
     * @param property
     * @return true if this is a boolean switch property
     */
    private boolean isBooleanSwitchProperty(ExecutionProperty property) {
        return property.hasDefaults() && property.getDefaults().length == 1 && property.getDefaults()[0].equals("false");
    }

    private ExecutionProperty getProperty(String parameterList, StringTokenizer st) throws InterpreterPropertyException {
        String switchProperty = st.nextToken();
        ExecutionProperty property = getProperty(switchProperty);
        if (property == null ) {
           throw new InterpreterPropertyException("Unsupported parameter " + parameterList);
        }
        return property;
    }
}
