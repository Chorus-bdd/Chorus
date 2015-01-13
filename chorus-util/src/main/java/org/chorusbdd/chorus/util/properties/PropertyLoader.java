package org.chorusbdd.chorus.util.properties;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by GA2EBBU on 09/01/2015.
 */
public interface PropertyLoader {

    PropertyLoader NULL_LOADER = new PropertyLoader() {
        public Properties loadProperties() throws IOException {
            return new Properties();
        }
    };


    public Properties loadProperties() throws IOException;
}
