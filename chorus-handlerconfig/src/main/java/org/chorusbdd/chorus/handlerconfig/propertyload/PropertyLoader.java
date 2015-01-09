package org.chorusbdd.chorus.handlerconfig.propertyload;

import java.io.IOException;
import java.util.Collections;
import java.util.Properties;

/**
 * Created by GA2EBBU on 09/01/2015.
 */
public interface PropertyLoader {

    PropertyLoader NULL_LOADER = new PropertyLoader() {
        public Properties loadProperties() throws IOException {
            return (Properties)Collections.emptyMap();
        }
    };


    public Properties loadProperties() throws IOException;
}
