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

import java.util.Properties;

/**
 * Created by nick on 12/01/15.
 *
 * A monadic type to provide functions to transform Properties
 */
public class PropertyOperations implements PropertyLoader {

    private PropertyLoader propertyLoader;

    public PropertyOperations(PropertyLoader propertyLoader) {
        this.propertyLoader = propertyLoader;
    }

    public PropertyOperations filterKeys(final Predicate<String> keyPredicate) {
        return new PropertyOperations(new FilteringPropertyLoader(propertyLoader, new BiPredicate<String, String>() {
            @Override
            public boolean test(String key, String value) {
                return keyPredicate.test(key);
            }
        }));
    }

    public PropertyOperations filterValues(final Predicate<String> valuePredicate) {
        return new PropertyOperations(new FilteringPropertyLoader(propertyLoader, new BiPredicate<String, String>() {
            @Override
            public boolean test(String key, String value) {
                return valuePredicate.test(value);
            }
        }));
    }

    public PropertyOperations filterByKeyPrefix(final String keyPrefix) {
        return filterKeys(new Predicate<String>() {
            @Override
            public boolean test(String key) {
                return key.startsWith(keyPrefix);
            }
        });
    }

    public PropertyOperations filterKeysNotStartingWith(final String keyPrefix) {
        return filterKeys(new Predicate<String>() {
            @Override
            public boolean test(String key) {
                return ! key.startsWith(keyPrefix);
            }
        });
    }

    public PropertyOperations filter(final BiPredicate<String,String> keyAndValueFilter) {
        return new PropertyOperations(new FilteringPropertyLoader(propertyLoader, keyAndValueFilter));
    }

    public PropertyOperations transformKeys(final Function<String,String> transform) {
        return new PropertyOperations(new TransformingPropertyLoader(propertyLoader, new BiFunction<String, String, Tuple2<String, String>>() {
            @Override
            public Tuple2<String, String> apply(String key, String value) {
                return Tuple2.tuple2(transform.apply(key), value);
            }
        }));
    }

    public PropertyOperations prefixKeys(final String prefix) {
        return transformKeys(new Function<String, String>() {
            @Override
            public String apply(String argument) {
                return prefix + argument;
            }
        });
    }

    public PropertyOperations removeKeyPrefix(final String handlerPrefix) {
        return transformKeys(new Function<String, String>() {
            public String apply(String key) {
                return key.startsWith(handlerPrefix) ? key.substring(handlerPrefix.length()) : key;
            }
        });
    }

    public PropertyOperations filterByAndRemoveKeyPrefix(final String prefix) {
        return filterByKeyPrefix(prefix).removeKeyPrefix(prefix);
    }

    public PropertyOperations transformValues(final Function<String,String> transform) {
        return new PropertyOperations(new TransformingPropertyLoader(propertyLoader, new BiFunction<String, String, Tuple2<String, String>>() {
            @Override
            public Tuple2<String, String> apply(String key, String value) {
                return Tuple2.tuple2(key, transform.apply(value));
            }
        }));
    }

    public PropertyOperations transform(final BiFunction<String,String,Tuple2<String,String>> transform) {
        return new PropertyOperations(new TransformingPropertyLoader(propertyLoader, transform));
    }

    public PropertyOperations merge(PropertyLoader p) {
        return new PropertyOperations(new CompositePropertyLoader(propertyLoader, p));
    }

    public PropertyOperations merge(PropertyOperations p) {
        return new PropertyOperations(new CompositePropertyLoader(this.propertyLoader, p.propertyLoader));
    }

    public GroupedPropertyLoader group(BiFunction<String, String, Tuple3<String,String,String>> groupingFunction) {
        return new PropertyGrouper(propertyLoader, groupingFunction);
    }

    /**
     * Split the key into 2 and use the first token to group
     *
     * <pre>
     * For keys in the form:
     *   Properties()
     *   group.key1=value1
     *   group.key2=value2
     *   group2.key2=value2
     *   group2.key3=value3
     *
     * create a GroupedPropertyLoader to load one Properties map for each of the groups, stripping the group name from property keys
     *
     * e.g.
     *   group1 = Properties()
     *            key1=value1
     *            key2=value2
     *
     *   group2 = Properties()
     *            key2 = value2
     *            key3 = value3
     *
     *  Where the delimiter does not exist in the source key, the group "" will be used.
     * </pre>
     *            
     * @param keyDelimiter
     */
    public GroupedPropertyLoader splitKeyAndGroup(final String keyDelimiter) {
        return group(new BiFunction<String, String, Tuple3<String, String, String>>() {
            public Tuple3<String, String, String> apply(String key, String value) {
                String[] keyTokens = key.split(keyDelimiter, 2);
                if ( keyTokens.length == 1) {
                    keyTokens = new String[] {"", keyTokens[0]};
                }
                return Tuple3.tuple3(keyTokens[0], keyTokens[1], value);
            }
        });
    }

    public Properties loadProperties() {
        return propertyLoader.loadProperties();
    }

    public Properties getProperties() {
        return propertyLoader.loadProperties();
    }

    /**
     * unit operation, return the wrapped loader
     */
    public PropertyLoader getLoader() {
        return propertyLoader;
    }

    public static PropertyOperations properties(PropertyLoader propertyLoader) {
        return new PropertyOperations(propertyLoader);
    }

    /**
     * @param properties, may be null in which case an new empty Properties instance will be used to seed this PropertyOperations
     */
    public static PropertyOperations properties(final Properties properties) {
        return new PropertyOperations(new PropertyLoader() {
            public Properties loadProperties() {
                return properties != null ? properties : new Properties();
            }
        });
    }

    public static PropertyOperations emptyProperties() {
        return properties(new Properties());
    }
}
