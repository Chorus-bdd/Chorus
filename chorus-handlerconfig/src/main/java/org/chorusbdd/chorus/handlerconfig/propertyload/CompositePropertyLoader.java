package org.chorusbdd.chorus.handlerconfig.propertyload;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * Created by nick on 12/01/15.
 */
public class CompositePropertyLoader implements PropertyLoader {

    private LinkedList<PropertyLoader> childLoaders = new LinkedList<PropertyLoader>();

    public CompositePropertyLoader( Collection<PropertyLoader> loaders ) {
        childLoaders.addAll(loaders);
    }

    public CompositePropertyLoader() {
    }

    public void add(PropertyLoader propertyLoader) {
        childLoaders.add(propertyLoader);
    }

    public void addAll(List<PropertyLoader> propertyLoaders) {
        childLoaders.addAll(propertyLoaders);
    }

    @Override
    public Properties loadProperties() throws IOException {
        Properties result = new Properties();
        for( PropertyLoader p : childLoaders) {
            result.putAll(p.loadProperties());
        }
        return result;
    }
}
