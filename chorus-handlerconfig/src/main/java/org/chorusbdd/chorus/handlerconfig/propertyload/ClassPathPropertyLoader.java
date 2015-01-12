package org.chorusbdd.chorus.handlerconfig.propertyload;

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * Created by GA2EBBU on 09/01/2015.
 */
public class ClassPathPropertyLoader implements PropertyLoader {

    private static ChorusLog log = ChorusLogFactory.getLog(ClassPathPropertyLoader.class);

    private String path;

    public ClassPathPropertyLoader(String path) {
        this.path = path;
    }

    public Properties loadProperties() throws IOException {
        URL u = ClassPathPropertyLoader.class.getResource(path);
        Properties props = new Properties();

        log.trace(String.format("About to load configuration properties from classpath at path: %s, resource exists? " + (u != null), path));

        if ( u != null ) {
            InputStream is = ClassPathPropertyLoader.class.getResourceAsStream(path);
            props.load(is);
            is.close();
            log.debug(String.format("Loaded configuration properties from classpath at path: %s", path));
        }
        return props;
    }

}
