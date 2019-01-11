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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Nick E
 * Date: 12/06/12
 * Time: 10:15
 */
public abstract class AbstractConfigSource implements ExecutionConfigSource {

    List<ExecutionProperty> properties;

    protected AbstractConfigSource(List<ExecutionProperty> properties) {
        this.properties = properties;
    }

    protected List<String> getOrCreatePropertyList(Map<ExecutionProperty, List<String>> propertyMap, ExecutionProperty switchName) {
        List<String> tokens = propertyMap.get(switchName);
        if ( tokens == null) {
            tokens = new ArrayList<>();
            propertyMap.put(switchName, tokens);
        }
        return tokens;
    }

    protected List<ExecutionProperty> getProperties() {
        return properties;
    }

    protected ExecutionProperty getProperty(String s) {
        ExecutionProperty result = null;
        for (ExecutionProperty p : properties) {
            if ( p.matchesSwitch(s)) {
                result = p;
                break;
            }
        }
        return result;
    }
}
