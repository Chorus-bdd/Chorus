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
package org.chorusbdd.chorus.util.properties;

import org.chorusbdd.chorus.util.function.BiFunction;
import org.chorusbdd.chorus.util.function.Tuple3;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by nick on 12/01/15.
 *
 * Group properties from a PropertyLoader using a BinaryFunction to calculate the group
 */
class PropertyGrouper implements GroupedPropertyLoader {

    private PropertyLoader loader;
    private BiFunction<String, String, Tuple3<String, String, String>> binaryFunction;

    public PropertyGrouper(PropertyLoader loader, BiFunction<String, String, Tuple3<String,String,String>> binaryFunction) {
        this.loader = loader;
        this.binaryFunction = binaryFunction;
    }

    public Map<String, Properties> loadPropertyGroups() {
        Map<String, Properties> results = new HashMap<>();

        Properties p = loader.loadProperties();
        for ( Map.Entry m : p.entrySet()) {
            String key = m.getKey().toString();
            String value = m.getValue().toString();
            Tuple3<String,String,String> t3 = binaryFunction.apply(key, value);
            addGroupProperty(results, t3.getOne(), t3.getTwo(), t3.getThree());
        }
        return results;
    }

    private static void addGroupProperty(Map<String, Properties> results, String group, String key, String value) {
        Properties p = results.get(group);
        if ( p == null) {
            p = new Properties();
            results.put(group, p);
        }
        p.setProperty(key, value);
    }
}
