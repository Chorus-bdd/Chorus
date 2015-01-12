package org.chorusbdd.chorus.handlerconfig.propertyload;

import org.chorusbdd.chorus.util.function.*;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by nick on 12/01/15.
 *
 *
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
     * @param delimiterExpression
     */
    public GroupedPropertyLoader stripAndGroupByFirstKeyToken(final String delimiterExpression) {
        return group(new BiFunction<String, String, Tuple3<String, String, String>>() {
            public Tuple3<String, String, String> apply(String key, String value) {
                String[] keyTokens = key.split(delimiterExpression, 2);
                if ( keyTokens.length == 1) {
                    keyTokens = new String[] {"", keyTokens[0]};
                }
                return Tuple3.tuple3(keyTokens[0], keyTokens[1], value);
            }
        });
    }

    public Properties loadProperties() throws IOException {
        return propertyLoader.loadProperties();
    }

    public static PropertyOperations properties(PropertyLoader propertyLoader) {
        return new PropertyOperations(propertyLoader);
    }

    public static PropertyOperations properties(final Properties properties) {
        return new PropertyOperations(new PropertyLoader() {
            public Properties loadProperties() throws IOException {
                return properties;
            }
        });
    }
}
