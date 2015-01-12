package org.chorusbdd.chorus.handlerconfig.propertyload;

import org.chorusbdd.chorus.util.function.BiFunction;
import org.chorusbdd.chorus.util.function.Tuple2;
import org.chorusbdd.chorus.util.function.Tuple3;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by nick on 12/01/15.
 *
 * Group properties from a PropertyLoader using a BinaryFunction to calculate the group
 */
public class PropertyGrouper implements GroupedPropertyLoader {

    private PropertyLoader loader;
    private BiFunction<String, String, Tuple3<String, String, String>> binaryFunction;

    public PropertyGrouper(PropertyLoader loader, BiFunction<String, String, Tuple3<String,String,String>> binaryFunction) {
        this.loader = loader;
        this.binaryFunction = binaryFunction;
    }

    public Map<String, Properties> loadProperties() throws IOException {
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

    public static GroupedPropertyLoader groupByKeyPrefix(PropertyLoader l) {
        return new PropertyGrouper(l, new BiFunction<String, String, String>() {
            public Tuple apply(String key, String val) {
                return key.split(".", 2)[0];
            }
        });
    }
}
