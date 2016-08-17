/**
 *  Copyright (C) 2000-2015 The Software Conservancy and Original Authors.
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: GA2EBBU
 * Date: 12/06/12
 * Time: 10:15
 */
public abstract class AbstractConfigSource implements ConfigSource {

    List<ConfigurationProperty> properties;

    protected AbstractConfigSource(List<ConfigurationProperty> properties) {
        this.properties = properties;
    }

    protected List<String> getOrCreatePropertyList(Map<ConfigurationProperty, List<String>> propertyMap, ConfigurationProperty switchName) {
        List<String> tokens = propertyMap.get(switchName);
        if ( tokens == null) {
            tokens = new ArrayList<>();
            propertyMap.put(switchName, tokens);
        }
        return tokens;
    }

    protected List<ConfigurationProperty> getProperties() {
        return properties;
    }

    protected ConfigurationProperty getProperty(String s) {
        ConfigurationProperty result = null;
        for (ConfigurationProperty p : properties) {
            if ( p.matchesSwitch(s)) {
                result = p;
                break;
            }
        }
        return result;
    }
}
