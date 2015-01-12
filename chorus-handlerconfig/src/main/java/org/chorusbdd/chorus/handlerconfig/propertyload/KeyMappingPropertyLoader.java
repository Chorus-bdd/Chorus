package org.chorusbdd.chorus.handlerconfig.propertyload;

import org.chorusbdd.chorus.util.function.BiFunction;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * Created by GA2EBBU on 09/01/2015.
 *
 * Apply a function to convert the map entries from a source Properties object into a Properties instance with modified keys
 */
public class KeyMappingPropertyLoader implements PropertyLoader {

    private PropertyLoader wrappedLoader;
    private BiFunction<String,String,String> mappingFunction;

    /**
     * @param mappingFunction a function which takes key and value from source map entry and returns a new key
     */
    public KeyMappingPropertyLoader(PropertyLoader wrappedLoader, BiFunction<String, String, String> mappingFunction) {
        this.wrappedLoader = wrappedLoader;
        this.mappingFunction = mappingFunction;
    }

    public Properties loadProperties() throws IOException {
        Properties p = wrappedLoader.loadProperties();
        Properties dest = new Properties();
        for ( Map.Entry<Object,Object> mapEntry : p.entrySet()) {
            dest.put(
                mappingFunction.apply(
                    mapEntry.getKey().toString(),
                    mapEntry.getValue().toString()
                ),
                mapEntry.getValue()
            );
        }
        return dest;
    }

    public static PropertyLoader prefixKeys(final String prefix, PropertyLoader p) {
        return new KeyMappingPropertyLoader(p, new BiFunction<String, String, String>() {
            public String apply(String key, String value) {
                return prefix + key;
            }
        });
    }
}
