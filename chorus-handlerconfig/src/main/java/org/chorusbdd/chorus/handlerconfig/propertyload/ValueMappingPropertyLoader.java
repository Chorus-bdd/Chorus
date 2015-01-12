package org.chorusbdd.chorus.handlerconfig.propertyload;

import org.chorusbdd.chorus.util.function.BiFunction;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * Created by GA2EBBU on 09/01/2015.
 *
 * Apply a function to convert the map entries from a source Properties object into a Properties instance with modified values
 */
public class ValueMappingPropertyLoader implements PropertyLoader {

    private PropertyLoader wrappedLoader;
    private BiFunction<String,String,String> mappingFunction;

    /**
     * @param mappingFunction a function which takes key and value from source map entry and returns a new value
     */
    public ValueMappingPropertyLoader(PropertyLoader wrappedLoader, BiFunction<String, String, String> mappingFunction) {
        this.wrappedLoader = wrappedLoader;
        this.mappingFunction = mappingFunction;
    }

    public Properties loadProperties() throws IOException {
        Properties p = wrappedLoader.loadProperties();
        Properties dest = new Properties();
        for ( Map.Entry<Object,Object> mapEntry : p.entrySet()) {
            dest.put(
                mapEntry.getKey(),
                mappingFunction.apply(
                    mapEntry.getKey().toString(),
                    mapEntry.getValue().toString()
                )
            );
        }
        return dest;
    }

    public static PropertyLoader stripValuePrefix(final String prefix, PropertyLoader p) {
        return new ValueMappingPropertyLoader(p, new BiFunction<String, String, String>() {
            public String apply(String key, String value) {
                return value.startsWith(prefix) ? value.substring(prefix.length()) : value;
            }
        });
    }
}
