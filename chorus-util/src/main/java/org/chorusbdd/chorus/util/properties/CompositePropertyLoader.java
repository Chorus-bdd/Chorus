package org.chorusbdd.chorus.util.properties;

import java.io.IOException;
import java.util.*;

/**
 * Created by nick on 12/01/15.
 */
class CompositePropertyLoader implements PropertyLoader {

    private LinkedList<PropertyLoader> childLoaders = new LinkedList<>();

    public CompositePropertyLoader(PropertyLoader... propertyLoaders) {
        this(Arrays.asList(propertyLoaders));
    }

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
    public Properties loadProperties()  {
        Properties result = new Properties();
        for( PropertyLoader p : childLoaders) {
            result.putAll(p.loadProperties());
        }
        return result;
    }
}
