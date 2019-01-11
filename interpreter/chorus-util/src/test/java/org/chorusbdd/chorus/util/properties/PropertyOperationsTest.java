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

import org.chorusbdd.chorus.util.function.*;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.chorusbdd.chorus.util.properties.PropertyOperations.properties;
import static org.chorusbdd.chorus.util.function.Tuple2.tuple2;
import static org.junit.Assert.assertEquals;

public class PropertyOperationsTest {

    @Test
    public void testStream() throws IOException {
        Properties p = properties(new MockPropertyLoader()).loadProperties();
        assertEquals(p, mockProperties());
    }

    @Test
    public void testFilterKeys() throws IOException {
        Properties p = properties(mockProperties()).filterKeys(new Predicate<String>() {
            public boolean test(String k) {
            return k.equals("PropertyB");
            }
        }).loadProperties();

        assertEquals(p, newProperties(tuple2("PropertyB", "ValueB")));
    }

    @Test
    public void testFilterValues() throws IOException {
        Properties p = properties(mockProperties()).filterValues(new Predicate<String>() {
            public boolean test(String v) {
                return (! v.equals("ValueA"));
            }
        }).loadProperties();
        assertEquals(p, newProperties(tuple2("PropertyB", "ValueB"), tuple2("PropertyC", "ValueC")));
    }

    @Test
    public void testFilter() throws IOException {
        Properties p = properties(mockProperties()).filter(new BiPredicate<String, String>() {
            @Override
            public boolean test(String key, String value) {
                return key.equals("PropertyA") || value.equals("ValueC");
            }
        }).loadProperties();
        assertEquals(p, newProperties(tuple2("PropertyA", "ValueA"), tuple2("PropertyC", "ValueC")));
    }

    @Test
    public void testKeyTransform() throws IOException {
        Properties p = properties(mockProperties()).transformKeys(new Function<String, String>() {
            public String apply(String key) {
                return "prefix." + key;
            }
        }).loadProperties();
        assertEquals(p, newProperties(
                tuple2("prefix.PropertyA", "ValueA"),
                tuple2("prefix.PropertyB", "ValueB"),
                tuple2("prefix.PropertyC", "ValueC")));
    }

    @Test
    public void testKeyPrefix() throws IOException {
        Properties p = properties(mockProperties()).prefixKeys("prefix.").loadProperties();
        assertEquals(p, newProperties(
                tuple2("prefix.PropertyA", "ValueA"),
                tuple2("prefix.PropertyB", "ValueB"),
                tuple2("prefix.PropertyC", "ValueC")));
    }

    @Test
    public void testValueTransform() throws IOException {
        Properties p = properties(mockProperties()).transformValues(new Function<String, String>() {
            public String apply(String value) {
                return "prefix." + value;
            }
        }).loadProperties();
        assertEquals(p, newProperties(
                tuple2("PropertyA", "prefix.ValueA"),
                tuple2("PropertyB", "prefix.ValueB"),
                tuple2("PropertyC", "prefix.ValueC")));
    }

    @Test
    public void testGroupByKeyPrefix() throws IOException {
        Map<String,Properties> p =
                properties(mockProperties()).prefixKeys("prefix1.").merge(
                        properties(mockProperties()).prefixKeys("prefix2.")).
                        splitKeyAndGroup("\\.").loadPropertyGroups();

        Map<String, Properties> expected = new HashMap<>();
        expected.put("prefix1", mockProperties());
        expected.put("prefix2", mockProperties());
        assertEquals(expected, p);
    }

    @Test
    public void testGroupByKeyPrefixWhereDelimiterNotPresent() throws IOException {
        PropertyOperations prefixed = properties(mockProperties()).prefixKeys("prefix1").merge(
                properties(mockProperties()).prefixKeys("prefix2"));

        Map<String,Properties> p = prefixed.splitKeyAndGroup("\\.").loadPropertyGroups();

        //where the grouping delimiter cannot be matched, we simply use "" as the group
        //so here the source properties are unchanged but end up in the "" group
        Map<String, Properties> expected = new HashMap<>();
        expected.put("", prefixed.loadProperties());

        assertEquals(expected, p);
    }

    @Test
    public void testTransform() throws IOException {
        Properties p = properties(mockProperties()).transform(new BiFunction<String, String, Tuple2<String, String>>() {
            @Override
            public Tuple2<String, String> apply(String key, String value) {
                return tuple2("A", "B");
            }
        }).loadProperties();
        assertEquals(newProperties(tuple2("A", "B")), p);
    }

    private static class MockPropertyLoader implements PropertyLoader {

        public Properties loadProperties() {
            return mockProperties();
        }
    }

    private static Properties mockProperties() {
        Properties p = new Properties();
        p.put("PropertyA", "ValueA");
        p.put("PropertyB", "ValueB");
        p.put("PropertyC", "ValueC");
        return p;
    }

    public static Properties newProperties(Tuple2<String, String>... props) {
        Properties result = new Properties();
        for ( Tuple2<String,String> p : props) {
            result.setProperty(p.getOne(), p.getTwo());
        }
        return result;
    }

}