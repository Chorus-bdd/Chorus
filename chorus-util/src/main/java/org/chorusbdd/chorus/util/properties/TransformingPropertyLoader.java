package org.chorusbdd.chorus.util.properties;

import org.chorusbdd.chorus.util.function.BiFunction;
import org.chorusbdd.chorus.util.function.Tuple2;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * Created by GA2EBBU on 09/01/2015.
 *
 * Apply a function to convert the map entries from a source Properties object into a Properties instance with modified values
 */
class TransformingPropertyLoader implements PropertyLoader {

    private PropertyLoader wrappedLoader;
    private BiFunction<String, String, Tuple2<String, String>> mappingFunction;

    /**
     * @param mappingFunction a function which takes key and value from source map entry and returns a Tuple2 containing a new key and value
     */
    public TransformingPropertyLoader(PropertyLoader wrappedLoader,
                                      BiFunction<String, String, Tuple2<String,String>> mappingFunction) {
        this.wrappedLoader = wrappedLoader;
        this.mappingFunction = mappingFunction;
    }

    public Properties loadProperties()  {
        Properties p = wrappedLoader.loadProperties();
        Properties dest = new Properties();
        for ( Map.Entry<Object,Object> mapEntry : p.entrySet()) {
            Tuple2<String,String> newEntry = mappingFunction.apply(
                mapEntry.getKey().toString(),
                mapEntry.getValue().toString()
            );
            dest.put(newEntry.getOne(), newEntry.getTwo());
        }
        return dest;
    }

}
