package org.chorusbdd.chorus.handlerconfig.propertyload;

import org.chorusbdd.chorus.util.function.Function;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * Created by GA2EBBU on 09/01/2015.
 *
 * Apply a function to convert the keys from a source Properties object into a Properties instance
 * with modified keys
 */
public class KeyMappingPropertyLoader implements PropertyLoader {

    private PropertyLoader wrappedLoader;
    private Function mappingFunction;

    public KeyMappingPropertyLoader(PropertyLoader wrappedLoader, Function<String, String> mappingFunction) {
        this.wrappedLoader = wrappedLoader;
        this.mappingFunction = mappingFunction;
    }

    public Properties loadProperties() throws IOException {
        Properties p = wrappedLoader.loadProperties();
        Properties dest = new Properties();
        for ( Map.Entry<Object,Object> mapEntry : p.entrySet()) {
            dest.put(mappingFunction.apply(mapEntry.getKey()), mapEntry.getValue());
        }
        return dest;
    }

    public static PropertyLoader prefixKeys(final String prefix, PropertyLoader p) {
        return new KeyMappingPropertyLoader(p, new Function<String, String>() {
            @Override
            public String apply(String argument) {
                return prefix + argument;
            }
        });
    }
}
