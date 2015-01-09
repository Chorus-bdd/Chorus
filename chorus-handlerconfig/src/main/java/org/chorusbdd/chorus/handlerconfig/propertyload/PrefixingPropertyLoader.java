package org.chorusbdd.chorus.handlerconfig.propertyload;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * Created by GA2EBBU on 09/01/2015.
 *
 * Prefix all the properties loaded from a wrapped loader with a String prefix
 */
public class PrefixingPropertyLoader implements PropertyLoader {

    private PropertyLoader wrappedLoader;
    private String prefixToApply;

    public PrefixingPropertyLoader(PropertyLoader wrappedLoader, String prefixToApply) {
        this.wrappedLoader = wrappedLoader;
        this.prefixToApply = prefixToApply;
    }

    public Properties loadProperties() throws IOException {
        Properties p = wrappedLoader.loadProperties();
        Properties dest = new Properties();
        for ( Map.Entry<Object,Object> mapEntry : p.entrySet()) {
            dest.put(prefixToApply + mapEntry.getKey(), mapEntry.getValue());
        }
        return dest;
    }
}
